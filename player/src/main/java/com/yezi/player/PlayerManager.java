package com.yezi.player;

import android.app.Application;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.yezi.player.bean.PlayerInfo;
import com.yezi.player.factory.IPlayerController;
import com.yezi.player.process.IMediaPlayerListener;
import com.yezi.player.process.ProcessPoolManager;
import com.yezi.player.process.IMediaPlayerService;

import java.util.ArrayList;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author : yezi
 * @date : 2020/3/30 10:05
 * desc   :
 * version: 1.0
 */
public class PlayerManager {
    private final String TAG = "PlayerManager";
    private static volatile PlayerManager mInstance;
    private ProcessPoolManager mProcessPoolManager;
    private ArrayList<PlayerInfo> mPlayerList = new ArrayList<>();
    private WeakHashMap<Integer,PlayerManagerCallback> mCallbackWeakHashMap = new WeakHashMap<>();
    private static Object mPlayerLock = new Object();
    private static int mMediaSessionId = 0;
    private static int callbackId = 0;

    private PlayerManager(Application application){
        mProcessPoolManager = ProcessPoolManager.getInstance(application);
    }

    public static PlayerManager getInstance(@NonNull Application application){
        if(mInstance == null){
            synchronized (PlayerManager.class){
                if(mInstance == null){
                    mInstance = new PlayerManager(application);
                }
            }
        }
        return mInstance;
    }

    public int registerPlayerListListener(PlayerManagerCallback cb){
        callbackId++;
        mCallbackWeakHashMap.put(callbackId,cb);
        return callbackId;
    }

    public void unregisterPlayerListListener(int callbackId){
        mCallbackWeakHashMap.remove(callbackId);
    }

    public int addPlayer(final PlayerInfo info){
        Log.d(TAG, "start addPlayer: "+info+" "+mMediaSessionId);
        synchronized (mPlayerLock) {
            mMediaSessionId++;
            IMediaPlayerService mediaPlayerService = mProcessPoolManager.getIdleMediaPlayerService();
            if (mediaPlayerService != null) {
                try {
                    if (mediaPlayerService.init(info)) {
                        info.setMediaSessionId(mMediaSessionId);
                        boolean state = mPlayerList.add(info);
                        mediaPlayerService.addMediaPlayerListener(new MediaServiceListener(info));
                        mediaPlayerService.play();
                        Log.d(TAG, "player add success: " + info + " current list size ="
                                + mPlayerList.size());
                    }else{
                        Log.w(TAG, "addPlayer: init failed add player failed");
                    }
                    return mMediaSessionId;
                } catch (Exception e) {
                    mProcessPoolManager.disposeExceptionService(mediaPlayerService);
                    e.printStackTrace();
                }
            }else{
                Log.w(TAG, "addPlayer: mediaService is null");
            }
        }
        return -1;
    }
    private static PlayerInfo mMockCallPlayerInfo;
    public final static int CALL_STATE_NORMAL = 0x00;
    public final static int CALL_STATE_RING = 0x01;
    public final static int CALL_STATE_IN_CALL = 0x02;
    public final static int CALL_STATE_IN_VOIP_CALL = 0x03;
    public final static int ERROR_NO_IDLE_PROCESS = 101;
    public final static int ERROR_PLAYER_INIT_FAILED = 102;
    private static final Object callPlayerLock = new Object();

    public int changeCallPlayer(int cmd) {
        synchronized (callPlayerLock) {
            Log.d(TAG, "changeCallPlayer: " + mMockCallPlayerInfo);
            if(mMockCallPlayerInfo != null) {
                IMediaPlayerService mediaPlayerService = mProcessPoolManager
                        .getMediaPlayerService(mMockCallPlayerInfo.getPid());
                if (mediaPlayerService == null) {
                    Log.w(TAG, "changeCallPlayer: mediaPlayerService is null with call info pid = "
                            + mMockCallPlayerInfo.getPid());
                    return ERROR_PLAYER_INIT_FAILED;
                }
                try {
                    if (cmd == CALL_STATE_NORMAL) {
                        mediaPlayerService.release();
                        return 0;
                    }
                    updatePlayerInfoForCall(cmd);
                    Log.d(TAG, "changeCallPlayer: init");
                    mediaPlayerService.init(mMockCallPlayerInfo);
                    mediaPlayerService.play();
                    Log.d(TAG, "changeCallPlayer: play");
                    return 0;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            //first to create player
            if (mMockCallPlayerInfo == null && cmd != CALL_STATE_NORMAL) {
                updatePlayerInfoForCall(cmd);
                int res = addPlayer(mMockCallPlayerInfo);
                if (res == -1) {
                    Log.w(TAG, "changeCallPlayer: player services are all busy,please release one to mock call");
                    return ERROR_NO_IDLE_PROCESS;
                }
            }
            if (cmd == CALL_STATE_NORMAL && mMockCallPlayerInfo == null) {
                Log.w(TAG, "changeCallPlayer: nothing to do");
                return 0;
            }

            return ERROR_PLAYER_INIT_FAILED;
        }
    }

    private void updatePlayerInfoForCall(int state){
        int stream;
        int usage;
        switch (state){
            case CALL_STATE_RING:
                stream = AudioManager.STREAM_RING;
                usage = AudioAttributes.USAGE_NOTIFICATION_RINGTONE;
                break;
            case CALL_STATE_IN_CALL:
            case CALL_STATE_IN_VOIP_CALL:
                stream = AudioManager.STREAM_VOICE_CALL;
                usage = AudioAttributes.USAGE_VOICE_COMMUNICATION;
                break;
            default:
                stream = AudioManager.STREAM_MUSIC;
                usage = AudioAttributes.USAGE_MEDIA;
        }
        if(mMockCallPlayerInfo == null){
            mMockCallPlayerInfo = new PlayerInfo("Call",stream,usage);
        }else{
            mMockCallPlayerInfo.setStream(stream);
            mMockCallPlayerInfo.setUsage(usage);
        }
    }

    public void playerControl(int mediaSessionId,int playerStateWanted){
        switch (playerStateWanted){
            case IPlayerController.PLAYER_PLAY:
                play(mediaSessionId);
                break;
            case IPlayerController.PLAYER_PAUSE:
                pause(mediaSessionId);
                break;
            case IPlayerController.PLAYER_RELEASE:
                release(mediaSessionId);
                break;
            default:
        }
    }

    public void play(int mediaSessionId){
        Log.d(TAG, "play: mediaSessionId = "+mediaSessionId);
        try {
            IMediaPlayerService mediaPlayerService = findMediaPlayerService(mediaSessionIdToPid(mediaSessionId));
            if(mediaPlayerService!=null)
                mediaPlayerService.play();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void pause(int mediaSessionId){
        Log.d(TAG, "pause: mediaSessionId = "+mediaSessionId);
        try {
            IMediaPlayerService mediaPlayerService = findMediaPlayerService(
                    mediaSessionIdToPid(mediaSessionId));
            if(mediaPlayerService!=null) {
                Log.d(TAG, "pause: ");
                mediaPlayerService.pause();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void stop(int mediaSessionId){
        Log.d(TAG, "stop: mediaSessionId = "+mediaSessionId);
        try {
            IMediaPlayerService mediaPlayerService = findMediaPlayerService(
                    mediaSessionIdToPid(mediaSessionId));
            if(mediaPlayerService!=null)
                mediaPlayerService.stop();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void release(int mediaSessionId){
        Log.d(TAG, "release: mediaSessionId = "+mediaSessionId);
        try {
            if(mMockCallPlayerInfo != null && mediaSessionId == mMockCallPlayerInfo.getMediaSessionId()){
                Log.d(TAG, "release: mMockCallPlayerInfo");
                mMockCallPlayerInfo = null;
            }
            IMediaPlayerService mediaPlayerService = findMediaPlayerService(
                    mediaSessionIdToPid(mediaSessionId));
            if(mediaPlayerService!=null)
                mediaPlayerService.release();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public boolean releaseAll(){
        try {
            for(PlayerInfo info : mPlayerList){
                release(info.getMediaSessionId());
                mProcessPoolManager.releaseProcess(info.getPid());
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private int mediaSessionIdToPid(int mediaSessionId){
        for(PlayerInfo info : mPlayerList) {
            if(info.getMediaSessionId() == mediaSessionId){
                return info.getPid();
            }
        }
        Log.w(TAG, "mediaSessionIdToPid: not find");
        return -1;
    }

    private IMediaPlayerService findMediaPlayerService(int pid){
        if(pid == -1){
            return null;
        }
        return mProcessPoolManager.getMediaPlayerService(pid);
    }



    public interface PlayerManagerCallback {
        /**
         * 回调
         * @param mediaSessionId
         * @param list
         */
        void onPlayerInfoUpdate(int mediaSessionId,int listPos,ArrayList<PlayerInfo> list);
    }

    class MediaServiceListener extends IMediaPlayerListener.Stub{
        private final String TAG = "MediaServiceListener";
        private PlayerInfo info;

        public MediaServiceListener(PlayerInfo info){
            this.info = info;
        }

        @Override
        public void onPidUpdate(int pid) throws RemoteException {
            Log.d(TAG, "onPidUpdate: "+pid);
            info.setPid(pid);
            updateInfoToCb();
        }

        @Override
        public void onPlayerStateUpdate(int state) throws RemoteException {
            Log.d(TAG, "onPlayerStateUpdate: "+state);
            info.setPlayerState(state);
            if(state == IPlayerController.PLAYER_RELEASE){
                mPlayerList.remove(info);
            }
            updateInfoToCb();
        }

        @Override
        public void onAudioFocusStateChange(int focusChange) throws RemoteException{
            Log.d(TAG, "onAudioFocusStateChange: "+focusChange);
            info.setAudioFocusState(focusChange);
            updateInfoToCb();
        }

        private void updateInfoToCb(){
            //pos == -1; remove
            int pos = mPlayerList.indexOf(info);
            for(Map.Entry<Integer, PlayerManagerCallback> entry : mCallbackWeakHashMap.entrySet()) {
                Log.d(TAG, "updateInfoToCb: ");
                entry.getValue().onPlayerInfoUpdate(info.getMediaSessionId(),pos,mPlayerList);
            }
        }
    }
}
