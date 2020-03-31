package com.yezi.player.process;

import android.app.Service;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.yezi.player.factory.IPlayerController;
import com.yezi.player.factory.PlayerFactory;
import com.yezi.player.bean.PlayerInfo;
import com.yezi.player.factory.PlayerListener;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author : yezi
 * @date : 2020/3/30 16:01
 * desc   :
 * version: 1.0
 */
public abstract class BaseMediaPlayerService extends Service {
    final String TAG = "BaseMediaPlayerService";
    private PlayerFactory mPlayerFactory;
    private WeakHashMap<Integer,IMediaPlayerListener> mListenerMap = new WeakHashMap<>();
    private int mListenerId = 100;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        mPlayerFactory = PlayerFactory.getInstance(getApplication());
    }

    private PlayerListener mPlayerListener = new PlayerListener() {
        @Override
        public void onPlayerStateUpdate(int state) {
            for(Map.Entry<Integer, IMediaPlayerListener> entry : mListenerMap.entrySet()) {
                try {
                    entry.getValue().onPlayerStateUpdate(state);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    protected IBinder mMediaPlayer = new IMediaPlayerService.Stub(){
        IPlayerController mPlayer;
        @Override
        public boolean init(PlayerInfo info) throws RemoteException {
            Log.d(TAG, "play: ");
            if(mPlayerFactory != null) {
                mPlayer = mPlayerFactory.createPlayer(info);
                mPlayer.setPlayerListener(mPlayerListener);
                return mPlayer.init();
            }
            return false;
        }

        @Override
        public boolean play() throws RemoteException {
            Log.d(TAG, "play: ");
            return mPlayer.play();
        }

        @Override
        public boolean pause() throws RemoteException {
            return mPlayer.pause();
        }

        @Override
        public boolean resume() throws RemoteException {
            return mPlayer.resume();
        }

        @Override
        public boolean stop() throws RemoteException {
            return mPlayer.stop();
        }

        @Override
        public boolean release() throws RemoteException {
            return mPlayer.release();
        }

        @Override
        public int addMediaPlayerListener(IMediaPlayerListener listener) throws RemoteException {
            Log.d(TAG, "addMediaPlayerListener: callPid = "+getCallingPid() );
            mListenerId++;
            mListenerMap.put(mListenerId,listener);
            listener.onPidUpdate(android.os.Process.myPid());
            if(mPlayer!=null && mPlayer.getPlayerState() != IPlayerController.PLAYER_UNKNOWN){
                listener.onPlayerStateUpdate(mPlayer.getPlayerState());
            }

            return mListenerId;
        }

        @Override
        public void removeMediaPlayerListener(int listenerId) throws RemoteException {
            mListenerMap.remove(listenerId);
        }
    };
}
