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
    private static String[] mReserveServiceNames = {"MediaPlayer_1","MediaPlayer_2","MediaPlayer_3"};

    private ProcessPoolManager(Application application){
        mApplication = application;
        setupProcessMap();
    }


    public IMediaPlayerService getIdleMediaPlayerService(){
        for(ProcessInfo processInfo : mProcessList){
            if(processInfo.processState == PLAYER_PROCESS_IDLE){
                return processInfo.remoteService;
            }
        }
        Log.w(TAG, "getIdleMediaPlayerService: have no idle process");
        return null;
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

    final int MSG_PLAY_STATE_UPDATE = 0x001;
    final int MSG_PROCESS_STATE_UPDATE = 0x002;
    final int MSG_SERVICE_CONNECTED = 0x003;
    final int MSG_SERVICE_DISCONNECTED = 0x004;
    final int MSG_SERVICE_DIED = 0x005;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler(){

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_PLAY_STATE_UPDATE:
                    ProcessInfo playerStateInfo = (ProcessInfo) msg.obj;
                    if(msg.arg1 == IPlayerController.PLAYER_RELEASE){
                        playerStateInfo.processState = PLAYER_PROCESS_IDLE;
                    }else{
                        playerStateInfo.processState = PLAYER_PROCESS_BUSY;
                    }
                    updateConnectService();
                    break;
                case MSG_SERVICE_CONNECTED:
                    final ProcessInfo info = (ProcessInfo) msg.obj;
                    try {
                        updateConnectService();
                        info.remoteService.addMediaPlayerListener(new IMediaPlayerListener.Stub() {
                            @Override
                            public void onPidUpdate(int pid) throws RemoteException {
                                info.pid = pid;
                            }

                            @Override
                            public void onPlayerStateUpdate(int state) throws RemoteException {
                                mHandler.sendMessage(mHandler.obtainMessage(MSG_PLAY_STATE_UPDATE,state,0,info));
                            }
                        });
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                case MSG_SERVICE_DISCONNECTED:
                case MSG_SERVICE_DIED:
                    updateConnectService();
                    break;
                default:
            }
        }
    };

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
        updateConnectService();
    }

    /**
     * 尽量保证有两个process 处于idle状态
     */
    private void updateConnectService() {
        int processCount = mProcessList.size();
        int idleCount = 0;
        int unConnectedCount = 0;
        int busyCount = 0;
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
            }else if(processInfo.processState == PLAYER_PROCESS_IDLE){
                if(idleCount>2){
                    stopService(processInfo);
                }
                idleCount++;
            }else if(processInfo.processState == PLAYER_PROCESS_BUSY){
                busyCount++;
            }
        }
        int connectingCount = 0;
        if(idleCount < 2 && unConnectedCount > 0){
            if(firstUnConnect != null) {
                startService(firstUnConnect);
                connectingCount++;
                unConnectedCount--;
            }
            if(idleCount +connectingCount < 2 && unConnectedCount > 0 && secondUnConnect != null){
                Log.d(TAG, "updateConnectService: 3");
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
        mApplication.stopService(new Intent(mApplication,processInfo.serviceClass));
    }

    private void startService(ProcessInfo processInfo){
        Log.d(TAG, "startService: "+processInfo);
        mApplication.bindService(new Intent(mApplication,processInfo.serviceClass),
                mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "onServiceConnected: "+componentName);
            final ProcessInfo processInfo = findProcessInfo(componentName);
            if(processInfo != null){
                processInfo.remoteService = IMediaPlayerService.Stub.asInterface(iBinder);
                processInfo.processState = PLAYER_PROCESS_IDLE;
                mHandler.sendMessage(mHandler.obtainMessage(MSG_SERVICE_CONNECTED,processInfo));;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "onServiceDisconnected: "+componentName);
            ProcessInfo processInfo = findProcessInfo(componentName);
            if(processInfo != null){
                processInfo.remoteService = null;
                processInfo.processState = PLAYER_PROCESS_UNCONNECTED;
                mHandler.sendMessage(mHandler.obtainMessage(MSG_SERVICE_DISCONNECTED,processInfo));
            }
        }

        @Override
        public void onBindingDied(ComponentName componentName) {
            Log.d(TAG, "onBindingDied: "+componentName);
            ProcessInfo processInfo = findProcessInfo(componentName);
            if(processInfo != null){
                processInfo.remoteService = null;
                processInfo.processState = PLAYER_PROCESS_UNCONNECTED;
                mHandler.sendMessage(mHandler.obtainMessage(MSG_SERVICE_DIED,processInfo));
            }
        }
    };

    private ProcessInfo findProcessInfo(ComponentName componentName){
        String className = componentName.getClassName();
        Log.d(TAG, "findProcessInfo: "+className);
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

    static int PLAYER_PROCESS_IDLE = 0x01;
    static int PLAYER_PROCESS_BUSY = 0x02;
    static int PLAYER_PROCESS_UNCONNECTED = 0x03;
    private class ProcessInfo {
        int pid;
        String serviceName;
        int playerProcessId;
        int processState;
        Class serviceClass;
        IMediaPlayerService remoteService;

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
     * TODO：可能在方法超过65535时，dex分包，可能存在问题
     * @param packageName
     * @return
     */
    private List<String > getClassName(String packageName){
        List<String >classNameList=new ArrayList<String >();
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
