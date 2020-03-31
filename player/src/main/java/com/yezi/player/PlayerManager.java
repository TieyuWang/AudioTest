package com.yezi.player;

import android.app.Application;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.yezi.player.bean.PlayerInfo;
import com.yezi.player.process.IMediaPlayerListener;
import com.yezi.player.process.ProcessPoolManager;
import com.yezi.player.process.IMediaPlayerService;

import java.util.HashMap;
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
    private Application mApplication;
    private ProcessPoolManager mProcessPoolManager;
    private HashMap<Integer, PlayerInfo> mPlayerMap = new HashMap<>();
    private WeakHashMap<Integer,PlayerManagerCallback> mCallbackWeakHashMap = new WeakHashMap<>();

    private static int mMediaSessionId = 0;
    private static int callbackId = 0;

    private PlayerManager(Application application){
        mApplication = application;
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
        Log.d(TAG, "addPlayer: "+info);
        mMediaSessionId++;
        IMediaPlayerService mediaPlayerService = mProcessPoolManager.getIdleMediaPlayerService();
        if(mediaPlayerService != null){
            try {
                mediaPlayerService.addMediaPlayerListener(new MediaServiceListener(info));
                mediaPlayerService.init(info);
                mPlayerMap.put(mMediaSessionId,info);
                Log.d(TAG, "addPlayer: "+mPlayerMap.size());
                mediaPlayerService.play();
                return mMediaSessionId;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }



    public boolean releaseAllPlayers(){
        return false;
    }


    public interface PlayerManagerCallback {
        void onPlayerInfoUpdate(int mediaSessionId,HashMap<Integer, PlayerInfo> map);
    }

    class MediaServiceListener extends IMediaPlayerListener.Stub{
        PlayerInfo info;

        public MediaServiceListener(PlayerInfo info){
            this.info = info;
        }

        @Override
        public void onPidUpdate(int pid) throws RemoteException {
            info.setPid(pid);
            updateInfoToCb();
        }

        @Override
        public void onPlayerStateUpdate(int state) throws RemoteException {
            info.setPlayerState(state);
            updateInfoToCb();
        }

        private void updateInfoToCb(){
            for(Map.Entry<Integer, PlayerManagerCallback> entry : mCallbackWeakHashMap.entrySet()) {
                entry.getValue().onPlayerInfoUpdate(info.getMediaSessionId(),mPlayerMap);
            }
        }
    }
}
