package com.yezi.player.factory;

import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.SoundPool;

/**
 * @author : yezi
 * @date : 2020/4/2 14:53
 * desc   :
 * version: 1.0
 */
public class SoundPlayer implements IPlayerController {
    private SoundPool mSoundPool;
    private AssetFileDescriptor mAssetFileDescriptor;
    private int mSoundPoolId;
    private PlayerListener mListener;
    private int mPlayerState = PLAYER_UNKNOWN;

    public SoundPlayer(AudioAttributes audioAttributes, AssetFileDescriptor assetFileDescriptor){
        mSoundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .build();
        mAssetFileDescriptor = assetFileDescriptor;

    }

    private void notifyPlayerState() {
        if(mListener!=null) {
            mListener.onPlayerStateUpdate(mPlayerState);
        }
    }

    @Override
    public boolean init() {
        mSoundPoolId = mSoundPool.load(mAssetFileDescriptor,Thread.NORM_PRIORITY);
        return false;
    }

    @Override
    public void play() {
        mSoundPool.play(mSoundPoolId,1.0f,1.0f,Thread.NORM_PRIORITY,0,1.0f);
        mPlayerState = PLAYER_PLAY;
        notifyPlayerState();
    }

    @Override
    public void pause() {
        mSoundPool.pause(mSoundPoolId);
        mPlayerState = PLAYER_PAUSE;
        notifyPlayerState();
    }

    @Override
    public void resume() {
        mSoundPool.resume(mSoundPoolId);
        mPlayerState = PLAYER_PLAY;
        notifyPlayerState();
    }

    @Override
    public void stop() {
        mSoundPool.stop(mSoundPoolId);
        mPlayerState = PLAYER_STOP;
        notifyPlayerState();
    }

    @Override
    public void release() {
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
    public void setPlayerListener(PlayerListener listener) {
        mListener = listener;
    }

    @Override
    public int getPlayerState() {
        return mPlayerState;
    }
}
