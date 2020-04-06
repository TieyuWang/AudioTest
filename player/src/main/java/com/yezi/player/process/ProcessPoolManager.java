package com.yezi.player.process;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.yezi.player.factory.IPlayerController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

import dalvik.system.DexFile;

/**
 * @author : yezi
 * @date : 2020/3/30 16:13
 * desc   :
 * version: 1.0
 */
public class ProcessPoolManager {
    private static final String TAG = "ProcessPoolManager";
    private final String POOL_PACKAGE_PATH = "com.yezi.player.process.pool";
    private static ProcessPoolManager mInstance;
    private Application mApplication;
    private static ArrayList<ProcessInfo> mProcessList = new ArrayList<>(20);
    private static String[] mReserveServiceNames = {"MediaPlayer_1","MediaPlayer_2","MediaPlayer_3"
                                                    ,"MediaPlayer_4","MediaPlayer_5","MediaPlayer_6"
                                                    ,"MediaPlayer_7","MediaPlayer_8","MediaPlayer_9"
                                                    ,"MediaPlayer_10"};

    private ProcessPoolManager(Application application){
        mApplication = application;
        setupProcessMap();
    }


    public IMediaPlayerService getIdleMediaPlayerService(){
        for(ProcessInfo processInfo : mProcessList){
            Log.d(TAG, "getIdleMediaPlayerService: "+processInfo);
            if(processInfo.processState == PLAYER_PROCESS_CONNECTED_IDLE){
                Log.d(TAG, "getIdleMediaPlayerService: "+processInfo.serviceName);
                return processInfo.remoteService;
            }
        }
        Log.w(TAG, "getIdleMediaPlayerService: have no idle process");
        return null;
    }

    public IMediaPlayerService getMediaPlayerService(int pid){
        for(ProcessInfo processInfo : mProcessList){
            if(processInfo.pid == pid){
                return processInfo.remoteService;
            }
        }
        Log.w(TAG, "getMediaPlayerService: can not find pid = "+pid+" player service");
        return null;
    }

    public int releaseProcess(int pid) {
        for(ProcessInfo processInfo : mProcessList){
            if(processInfo.pid == pid){
                stopService(processInfo);
                return pid;
            }
        }
        return -1;
    }

    public static ProcessPoolManager getInstance(@NonNull Application application){
        if(mInstance == null){
            synchronized (ProcessPoolManager.class){
                if(mInstance == null){
                    mInstance = new ProcessPoolManager(application);
                }
            }
        }
        return mInstance;
    }

    private final long UPDATE_PROCESS_TIME = 50;

    private final int MSG_PLAY_STATE_UPDATE = 0x001;
    private final int MSG_PROCESS_STATE_UPDATE = 0x002;
    private final int MSG_SERVICE_CONNECTED = 0x003;
    private final int MSG_SERVICE_DISCONNECTED = 0x004;
    private final int MSG_SERVICE_DIED = 0x005;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_PLAY_STATE_UPDATE:
                    ProcessInfo processInfoPs = (ProcessInfo) msg.obj;
                    //解决 process state release 转 busy 错乱问题
                    if(msg.arg1 != IPlayerController.PLAYER_RELEASE
                            && processInfoPs.processState == PLAYER_PROCESS_CONNECTED_IDLE){
                        processInfoPs.processState = PLAYER_PROCESS_CONNECTED_BUSY;

                    }else if((msg.arg1 == IPlayerController.PLAYER_RELEASE
                            && processInfoPs.processState == PLAYER_PROCESS_CONNECTED_BUSY)){
                        processInfoPs.processState = PLAYER_PROCESS_CONNECTED_IDLE;
                    }
                    sendDelayMsg(MSG_PROCESS_STATE_UPDATE,0,UPDATE_PROCESS_TIME);
                    break;
                case MSG_SERVICE_CONNECTED:
                    final ProcessInfo processInfo = (ProcessInfo) msg.obj;
                    try {
                        processInfo.remoteService.addMediaPlayerListener(new MediaServiceListenerProcessMrg(processInfo));
                        sendDelayMsg(MSG_PROCESS_STATE_UPDATE,0,UPDATE_PROCESS_TIME);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                case MSG_PROCESS_STATE_UPDATE:
                    dynamicUpdateConnectService();
                    break;
                case MSG_SERVICE_DISCONNECTED:
                case MSG_SERVICE_DIED:
                    sendDelayMsg(MSG_PROCESS_STATE_UPDATE,0,UPDATE_PROCESS_TIME);
                    break;
                default:
            }
        }
    };

    private void sendMsg(int what,Object obj){
        sendMsg(what,0,0,obj,0);
    }

    private void sendMsg(int what,int arg1){
        sendMsg(what,arg1,0,null,0);
    }

    private void sendDelayMsg(int what,int arg1,long delayedTime){
        sendMsg(what,arg1,0,null,delayedTime);
    }

    private void sendDelayMsg(int what,Object obj,long delayedTime){
        sendMsg(what,0,0,obj,delayedTime);
    }

    private void sendMsg(int what,int arg1,int arg2,Object obj,long delayedTime) {
        if(mHandler.hasMessages(what)){
            mHandler.removeMessages(what);
        }
        Message message = mHandler.obtainMessage();
        message.what = what;
        message.arg1 = arg1;
        message.arg2 = arg2;
        message.obj = obj;
        if(delayedTime != 0) {
            mHandler.sendMessageDelayed(message,delayedTime);
        }else {
            mHandler.sendMessage(message);
        }
    }

    private void setupProcessMap() {
        Log.d(TAG, "setupProcessMap: ");
/*        try {
            List<String> classNames = getClassName(POOL_PACKAGE_PATH);
            Log.d(TAG, "setupProcessMap: " + classNames.size());
            for (String name : classNames) {
                Log.d(TAG, "setupProcessMap: " + name);
            }
        }catch (Exception e){
            Log.w(TAG, "setupProcessMap: get Class name failed by @Deprecated DexFile");
        }*/
        for(String name : mReserveServiceNames){
            String[] strs = name.split("_");
            ProcessInfo processInfo = new ProcessInfo();
            processInfo.playerProcessId = Integer.parseInt(strs[1]);
            processInfo.processState = PLAYER_PROCESS_UNCONNECTED;
            processInfo.serviceName = name;
            try {
                processInfo.serviceClass = Class.forName(POOL_PACKAGE_PATH+"."+name);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                continue;
            }
            mProcessList.add(processInfo);
        }
        sendDelayMsg(MSG_PROCESS_STATE_UPDATE,0,UPDATE_PROCESS_TIME);
    }

    /**
     * 尽量保证有两个process 处于idle状态
     */
    private void dynamicUpdateConnectService() {
        int processCount = mProcessList.size();
        int idleCount = 0;
        int unConnectedCount = 0;
        int busyCount = 0;
        int connectingCount = 0;
        ProcessInfo firstUnConnect = null;
        ProcessInfo secondUnConnect = null;
        for(ProcessInfo processInfo : mProcessList){
            if(processInfo.processState == PLAYER_PROCESS_UNCONNECTED){
                unConnectedCount++;
                if(unConnectedCount == 1) {
                    firstUnConnect = processInfo;
                }
                if(unConnectedCount == 2) {
                    secondUnConnect = processInfo;
                }
            }else if(processInfo.processState == PLAYER_PROCESS_CONNECTED_IDLE){
                if(idleCount>2){
                    stopService(processInfo);
                }
                idleCount++;
            }else if(processInfo.processState == PLAYER_PROCESS_CONNECTED_BUSY){
                busyCount++;
            }else if(processInfo.processState == PLAYER_PROCESS_CONNECTING){
                connectingCount++;
            }
        }

        if(idleCount + connectingCount < 2 && unConnectedCount > 0){
            if(firstUnConnect != null) {
                startService(firstUnConnect);
                connectingCount++;
                unConnectedCount--;
            }
            if(idleCount +connectingCount < 2 && unConnectedCount > 0 && secondUnConnect != null){
                startService(secondUnConnect);
                connectingCount++;
                unConnectedCount--;
            }
        }
        Log.d(TAG, "updateConnectService: processCount = "+processCount
                +" unConnectedCount = "+unConnectedCount
                +" connectingCount = "+connectingCount
                +" idleCount = "+idleCount+" busyCount = "+busyCount);
    }

    private void stopService(ProcessInfo processInfo) {
        Log.d(TAG, "stopService: "+processInfo);
        if(processInfo==null || processInfo.processState == PLAYER_PROCESS_UNCONNECTED
                || processInfo.processState == PLAYER_PROCESS_DISCONNECTING)
            return;

        processInfo.processState = PLAYER_PROCESS_DISCONNECTING;
        mApplication.unbindService(processInfo.serviceConnection);
        processInfo.remoteService = null;
        processInfo.processState = PLAYER_PROCESS_UNCONNECTED;
        Log.d(TAG, "stopService: "+processInfo);
        Log.d(TAG, "stopService: "+mProcessList);
        sendMsg(MSG_SERVICE_DISCONNECTED,processInfo);
    }

    private void startService(ProcessInfo processInfo){
        Log.d(TAG, "startService: "+processInfo);
        if(processInfo==null || processInfo.processState == PLAYER_PROCESS_CONNECTED_BUSY
                || processInfo.processState == PLAYER_PROCESS_CONNECTED_IDLE
                || processInfo.processState == PLAYER_PROCESS_CONNECTING)
            return;

        processInfo.processState = PLAYER_PROCESS_CONNECTING;
        ProcessPoolMrgServiceConnection poolMrgServiceConnection =
                new ProcessPoolMrgServiceConnection(processInfo);
        processInfo.serviceConnection = poolMrgServiceConnection;
        mApplication.bindService(new Intent(mApplication,processInfo.serviceClass),
                processInfo.serviceConnection, Context.BIND_AUTO_CREATE);
    }

    class ProcessPoolMrgServiceConnection implements ServiceConnection {
        private ProcessInfo mProcessInfo;
        public ProcessPoolMrgServiceConnection(ProcessInfo info){
            mProcessInfo = info;
        }

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "onServiceConnected: "+componentName);
            if(mProcessInfo != null){
                mProcessInfo.remoteService = IMediaPlayerService.Stub.asInterface(iBinder);
                sendMsg(MSG_SERVICE_CONNECTED,mProcessInfo);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "onServiceDisconnected: "+componentName);
            if(mProcessInfo != null){
                mProcessInfo.remoteService = null;
                mProcessInfo.processState = PLAYER_PROCESS_UNCONNECTED;
                sendMsg(MSG_SERVICE_DISCONNECTED,mProcessInfo);
            }
        }

        @Override
        public void onBindingDied(ComponentName name) {
            Log.d(TAG, "onBindingDied: "+name);
            if(mProcessInfo != null){
                mProcessInfo.remoteService = null;
                mProcessInfo.processState = PLAYER_PROCESS_UNCONNECTED;
                sendMsg(MSG_SERVICE_DIED,mProcessInfo);
            }
        }

        @Override
        public void onNullBinding(ComponentName name) {
            Log.d(TAG, "onNullBinding: "+name);
        }
    }

    private ProcessInfo findProcessInfo(ComponentName componentName){
        String className = componentName.getClassName();
        int lastPoint = className.lastIndexOf(".");
        String serviceName = className.substring(lastPoint+1);

        for(ProcessInfo info:mProcessList){
            if(info.serviceName.equals(serviceName)){
                Log.d(TAG, "findProcessInfo: have find "+info);
                return info;
            }
        }
        Log.w(TAG, "findProcessInfo: can not find process info by class name: "+serviceName);
        return null;
    }


    class MediaServiceListenerProcessMrg extends IMediaPlayerListener.Stub{
        private ProcessInfo info;
        public MediaServiceListenerProcessMrg(ProcessInfo info){
            this.info = info;
        }

        @Override
        public void onPidUpdate(int pid) throws RemoteException {
            info.pid = pid;
            // 04-06解决快速点击出现引用重复pid
            info.processState = PLAYER_PROCESS_CONNECTED_IDLE;
        }

        @Override
        public void onPlayerStateUpdate(int state) throws RemoteException {
            mHandler.sendMessage(mHandler.obtainMessage(MSG_PLAY_STATE_UPDATE,state,0,info));
        }

        @Override
        public void onAudioFocusStateChange(int focusChange) throws RemoteException {
            //nothing to do
        }
    }

    private static int PLAYER_PROCESS_CONNECTED_IDLE = 0x01;
    private static int PLAYER_PROCESS_CONNECTED_BUSY = 0x02;
    private static int PLAYER_PROCESS_UNCONNECTED = 0x03;
    private static int PLAYER_PROCESS_CONNECTING = 0x04;
    private static int PLAYER_PROCESS_DISCONNECTING = 0x05;
    private static class ProcessInfo {
        int pid;
        String serviceName;
        int playerProcessId;
        int processState;
        Class serviceClass;
        IMediaPlayerService remoteService;
        ServiceConnection serviceConnection;

        @Override
        public String toString() {
            return "ProcessInfo{" +
                    "pid=" + pid +
                    ", serviceName='" + serviceName + '\'' +
                    ", playerProcessId=" + playerProcessId +
                    ", processState=" + processState +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ProcessInfo that = (ProcessInfo) o;
            return  playerProcessId == that.playerProcessId &&
                    Objects.equals(serviceName, that.serviceName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(serviceName, playerProcessId);
        }
    }


    /**
     * TODO：可能在方法超过65535时，dex分包，可能存在问题 暂时手动添加
     * @param packageName package 路径
     * @return list
     */
    private List<String> getClassName(String packageName){
        List<String> classNameList = new ArrayList<String >();
        try {
            DexFile df = new DexFile(mApplication.getPackageCodePath());
            //通过DexFile查找当前的APK中可执行文件
            Enumeration<String> enumeration = df.entries();
            //获取df中的元素  这里包含了所有可执行的类名 该类名包含了包名+类名的方式
            while (enumeration.hasMoreElements()) {
                //遍历
                String className = (String) enumeration.nextElement();

                if (className.contains(packageName)) {
                    //在当前所有可执行的类里面查找包含有该包名的所有类
                    classNameList.add(className);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  classNameList;
    }
}
