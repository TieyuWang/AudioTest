package com.yezi.player.factory;

import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.util.Log;

/**
 * @author : yezi
 * @date : 2020/4/8 16:56
 * desc   :
 * version: 1.0
 */
class CallPlayer extends NormalPlayer implements IPlayerController {

    private static final String TAG = "CallPlayer";
    private AudioManager mAudioManager;
    private int lastMode;
    private AudioAttributes mAudioAttributes;

    public CallPlayer(AudioManager audioManager) {
        super();
        Log.d(TAG, "CallPlayer: ");
        mAudioManager = audioManager;
    }

    @Override
    public boolean init(AudioAttributes audioAttributes, AssetFileDescriptor sourceFd, PlayerListener listener) {
        Log.d(TAG, "init: "+audioAttributes );
        if(isPlaying()){
            releaseWithOutNormalModeSet();
        }
        mAudioAttributes = audioAttributes;
        return super.init(audioAttributes, sourceFd, listener);
    }

    @Override
    public void play() {
        Log.d(TAG, "play: ");
        switch (mAudioAttributes.getVolumeControlStream()){
            case AudioManager.STREAM_RING:
                mAudioManager.setMode(AudioManager.MODE_RINGTONE);
                break;
            case AudioManager.STREAM_VOICE_CALL:
                //use MODE_IN_COMMUNICATION instead of MODE_IN_CALL
                mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                break;
            default:
        }
        super.play();
    }

    @Override
    public void pause() {
        release();
    }

    @Override
    public void resume() {

    }

    @Override
    public void stop() {
        release();
    }

    @Override
    public void release() {
        super.release();
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
    }

    /**
     *  RING -> CALL not set mode NORMAL
     */
    private void releaseWithOutNormalModeSet(){
        Log.d(TAG, "releaseWithOutNormalModeSet: ");
        mediaPlayer.release();
        mediaPlayer = null;
    }
}
