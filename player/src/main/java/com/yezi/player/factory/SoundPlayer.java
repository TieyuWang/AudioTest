package com.yezi.player.factory;

import android.annotation.SuppressLint;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;


/**
 * @author : yezi
 * @date : 2020/4/2 14:53
 * desc   :
 * version: 1.0
 */
public class SoundPlayer implements IPlayerController {
    private final String TAG = "SoundPlayer";
    private SoundPool mSoundPool;
    private int mSoundPoolId;
    private PlayerListener mListener;
    private int mPlayerState = PLAYER_UNKNOWN;
    private boolean mLoadComplete = false;
    private boolean hasPlayCmd = false;

    public SoundPlayer(){
    }

    //添加自动停止功能

    final int MSG_AUTO_PAUSE = 1;
    final long AUTO_PAUSE_TIME = 2000;
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if(msg.what == MSG_AUTO_PAUSE){
                pause();
            }
        }
    };

    private void notifyPlayerState() {
        if(mListener!=null) {
            mListener.onPlayerStateUpdate(mPlayerState);
        }
    }


    @Override
    public boolean init(AudioAttributes audioAttributes, AssetFileDescriptor assetFd, PlayerListener listener) {
        mSoundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .build();
        mSoundPoolId = mSoundPool.load(assetFd,Thread.NORM_PRIORITY);
        mListener = listener;
        Log.d(TAG, "init: "+mSoundPoolId);
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                mLoadComplete = true;
                if(hasPlayCmd){
                    play();
                }
            }
        });
        return mSoundPoolId != -1;
    }

    @Override
    public void play() {
        hasPlayCmd = true;
        if(mLoadComplete) {
            mSoundPool.play(mSoundPoolId, 1.0f, 1.0f, Thread.NORM_PRIORITY, 0, 1.0f);
            mPlayerState = PLAYER_PLAY;
            notifyPlayerState();
            mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_AUTO_PAUSE),AUTO_PAUSE_TIME);
        }
    }

    @Override
    public void pause() {
        hasPlayCmd = false;
        mSoundPool.pause(mSoundPoolId);
        mPlayerState = PLAYER_PAUSE;
        notifyPlayerState();
    }

    @Override
    public void resume() {
        hasPlayCmd = false;
        mSoundPool.resume(mSoundPoolId);
        mPlayerState = PLAYER_PLAY;
        notifyPlayerState();
    }

    @Override
    public void stop() {
        hasPlayCmd = false;
        mSoundPool.stop(mSoundPoolId);
        mPlayerState = PLAYER_STOP;
        notifyPlayerState();
    }

    @Override
    public void release() {
        hasPlayCmd = false;
        mSoundPool.unload(mSoundPoolId);
        mSoundPool.release();
        mPlayerState = PLAYER_RELEASE;
        notifyPlayerState();
    }

    @Override
    public void duck() {

    }

    @Override
    public void unDuck() {

    }

    @Override
    public boolean isDucking() {
        return false;
    }

    @Override
    public boolean canBeDuck() {
        return false;
    }

    @Override
    public boolean isPlaying() {
        return mPlayerState == PLAYER_PLAY;
    }

    @Override
    public int getPlayerState() {
        return mPlayerState;
    }
}
