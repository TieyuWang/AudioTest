package com.yezi.player.process;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yezi.player.R;
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
public abstract class BaseMediaPlayerService extends Service implements AudioManager.OnAudioFocusChangeListener {
    final String TAG = getTag();
    private PlayerFactory mPlayerFactory;

    private WeakHashMap<Integer,IMediaPlayerListener> mListenerMap = new WeakHashMap<>();
    private int mListenerId = 100;

    private AudioManager mAudioManager;
    private AudioFocusRequest mAudioFocusRequest;
    private final Object mFocusLock = new Object();
    boolean mPlaybackDelayed = false;
    boolean mResumeOnFocusGain = false;
    boolean mResumeDuckOnFocusGain = false;

    private static int id = 1;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        super.onCreate();

        String CHANNEL_ONE_ID = "CHANNEL_ONE_ID";
        String CHANNEL_ONE_NAME= "CHANNEL_ONE_ID";
        NotificationChannel notificationChannel= null;
        //启动前台服务
        //进行8.0的判断
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel= new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
        }

        Notification notification= new Notification.Builder(this,CHANNEL_ONE_ID)
                .build();
        notification.flags|= Notification.FLAG_NO_CLEAR;
        startForeground(id++,notification);

        mPlayerFactory = PlayerFactory.getInstance(getApplication());
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    protected abstract @NonNull String getTag();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        return mMediaPlayer;
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "handleMessage: "+msg);
        }
    };

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
            if(!mResumeOnFocusGain && (state == IPlayerController.PLAYER_PAUSE || state == IPlayerController.PLAYER_STOP
                    || state == IPlayerController.PLAYER_RELEASE)){
                if(mAudioManager != null && mAudioFocusRequest != null) {
                    int res = mAudioManager.abandonAudioFocusRequest(mAudioFocusRequest);
                    Log.d(TAG, "onPlayerStateUpdate: abandonAudioFocusRequest res "+res);
                    if(res == 1){
                        dispatchAudioFocusChange(AudioManager.AUDIOFOCUS_LOSS);
                    }
                }
            }
        }
    };

    @Override
    public void onAudioFocusChange(int focusChange){
        currentAudioFocusState = focusChange;
        dispatchAudioFocusChange(focusChange);
        if(mPlayer == null){
            return;
        }
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                Log.d(TAG, "onAudioFocusChange: AUDIOFOCUS_GAIN");
                if (mPlaybackDelayed || mResumeOnFocusGain) {
                    synchronized (mFocusLock) {
                        mPlaybackDelayed = false;
                        mResumeOnFocusGain = false;
                    }
                    mPlayer.play();
                }
                if(mResumeDuckOnFocusGain){
                    synchronized (mFocusLock) {
                        mResumeDuckOnFocusGain = false;
                    }
                    mPlayer.unDuck();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                Log.d(TAG, "onAudioFocusChange: AUDIOFOCUS_LOSS");
                synchronized (mFocusLock) {
                    // this is not a transient loss, we shouldn't automatically resume for now
                    mResumeOnFocusGain = false;
                    mPlaybackDelayed = false;
                }
                mPlayer.pause();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                Log.d(TAG, "onAudioFocusChange: AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                // duck player if it can be duck
                if(mPlayer.canBeDuck()) {
                    synchronized (mFocusLock) {
                        mResumeDuckOnFocusGain = true;
                        mPlaybackDelayed = false;
                    }
                    mPlayer.duck();
                    break;
                }
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                Log.d(TAG, "onAudioFocusChange: AUDIOFOCUS_LOSS_TRANSIENT");
                synchronized (mFocusLock) {
                    mResumeOnFocusGain = mPlayer.isPlaying();
                    mPlaybackDelayed = false;
                }
                mPlayer.pause();
                break;
            default:
        }
    }

    private void dispatchAudioFocusChange(int focusChange) {
        for(Map.Entry<Integer, IMediaPlayerListener> entry : mListenerMap.entrySet()) {
            try {
                entry.getValue().onAudioFocusStateChange(focusChange);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    int currentAudioFocusState = 0;
    IPlayerController mPlayer;
    protected IBinder mMediaPlayer = new IMediaPlayerService.Stub(){

        @Override
        public boolean init(PlayerInfo info) throws RemoteException {
            AudioAttributes audioAttributes = infoToAttribute(info);

            if(audioAttributes.getUsage() == AudioAttributes.USAGE_MEDIA
                    || audioAttributes.getUsage() == AudioAttributes.USAGE_GAME) {
                Log.d(TAG, "init: AUDIOFOCUS_GAIN ");
                mAudioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                        .setAudioAttributes(audioAttributes)
                        .setOnAudioFocusChangeListener(BaseMediaPlayerService.this, mHandler)
                        .build();
            }else{
                Log.d(TAG, "init: AUDIOFOCUS_GAIN_TRANSIENT ");
                mAudioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                        .setAudioAttributes(audioAttributes)
                        .setOnAudioFocusChangeListener(BaseMediaPlayerService.this, mHandler)
                        .build();
            }
            if(audioAttributes.getUsage() == AudioAttributes.USAGE_VOICE_COMMUNICATION
                    || audioAttributes.getUsage() == AudioAttributes.USAGE_VOICE_COMMUNICATION_SIGNALLING){
            }


            Log.d(TAG, "init: "+mAudioFocusRequest);
            if(mPlayerFactory != null) {
                mPlayer = mPlayerFactory.createPlayer(audioAttributes);
                return mPlayer.init(audioAttributes,mPlayerFactory.getMediaSource(audioAttributes),mPlayerListener);
            }
            return false;
        }

        @Override
        public void play() throws RemoteException {
            Log.d(TAG, "play: ");
            if(currentAudioFocusState == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
                Log.d(TAG, "play: already have audio focus");
                mPlayer.play();
                return;
            }
            currentAudioFocusState = mAudioManager.requestAudioFocus(mAudioFocusRequest);
            synchronized (mFocusLock) {
                if (currentAudioFocusState == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
                    dispatchAudioFocusChange(currentAudioFocusState);
                    mPlaybackDelayed = false;
                } else if (currentAudioFocusState == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    dispatchAudioFocusChange(currentAudioFocusState);
                    mPlaybackDelayed = false;
                    mPlayer.play();
                } else if (currentAudioFocusState == AudioManager.AUDIOFOCUS_REQUEST_DELAYED) {
                    mPlaybackDelayed = true;
                }
            }
        }

        @Override
        public void pause() throws RemoteException {
            if(mPlayer != null && mPlayer.isPlaying()) {
                mPlayer.pause();
            }
        }

        @Override
        public void resume() throws RemoteException {
            if(mPlayer != null && !mPlayer.isPlaying()){
                mPlayer.resume();
            }
        }

        @Override
        public void stop() throws RemoteException {
            if(mPlayer != null) {
                mPlayer.stop();
            }
        }

        @Override
        public void release() throws RemoteException {
            if(mPlayer != null) {
                mPlayer.release();
            }
        }

        @Override
        public int addMediaPlayerListener(IMediaPlayerListener listener) throws RemoteException {
            Log.d(TAG, "addMediaPlayerListener: callPid = "+getCallingPid() );
            mListenerId++;
            mListenerMap.put(mListenerId,listener);
            listener.onPidUpdate(android.os.Process.myPid());
            if(mPlayer!=null && mPlayer.getPlayerState() != IPlayerController.PLAYER_UNKNOWN
                    && mPlayer.getPlayerState() != IPlayerController.PLAYER_RELEASE){
                listener.onPlayerStateUpdate(mPlayer.getPlayerState());
            }

            return mListenerId;
        }

        @Override
        public void removeMediaPlayerListener(int listenerId) throws RemoteException {
            mListenerMap.remove(listenerId);
        }
    };

    private AudioAttributes infoToAttribute(PlayerInfo info) {
        return new AudioAttributes.Builder()
                .setLegacyStreamType(info.getStream())
                .setUsage(info.getUsage())
                .build();
    }
}
