package com.yezi.player.factory;


import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.util.Log;


import java.io.IOException;

/**
 * @author : yezi
 * @date : 2020/3/28 9:49
 * desc   :
 * version: 1.0
 */
public class NormalPlayer implements IPlayerController{
    private final String TAG = "NormalPlayer";
    protected MediaPlayer mediaPlayer;
    private PlayerListener mListener;

    private int mPlayerState = PLAYER_UNKNOWN;

    public NormalPlayer(){
        Log.d(TAG, "NormalPlayer: ");
    }

    @Override
    public boolean init(AudioAttributes audioAttributes,AssetFileDescriptor sourceFd,
                        PlayerListener listener) {
        Log.d(TAG, "init: "+audioAttributes+" "+sourceFd);
        mediaPlayer = new MediaPlayer();
        mListener = listener;
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mPlayerState = PLAYER_STOP;
                notifyPlayerState();
            }
        });
        try {
          mediaPlayer.setAudioAttributes(audioAttributes);
          mediaPlayer.setDataSource(sourceFd);
          mediaPlayer.prepare();
          return true;
        } catch (IOException e) {
          e.printStackTrace();
        }
        return false;
    }

    @Override
    public void play() {
        Log.d(TAG, "play: ");
        mediaPlayer.start();
        mPlayerState = PLAYER_PLAY;
        notifyPlayerState();
    }

    private void notifyPlayerState() {
        Log.d(TAG, "notifyPlayerState: "+mPlayerState);
        if(mListener!=null) {
            mListener.onPlayerStateUpdate(mPlayerState);
        }
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
        mPlayerState = PLAYER_PAUSE;
        notifyPlayerState();
    }

    @Override
    public void resume() {
        mediaPlayer.start();
        mPlayerState = PLAYER_PLAY;
        notifyPlayerState();
    }

    @Override
    public void stop() {
        mediaPlayer.stop();
        mPlayerState = PLAYER_STOP;
        notifyPlayerState();
    }

    @Override
    public void release() {
        Log.d(TAG, "release: ");
        mediaPlayer.release();
        mediaPlayer = null;
        mPlayerState = PLAYER_RELEASE;
        notifyPlayerState();
    }

    @Override
    public void duck() {
        Log.d(TAG, "duck: ");
    }

    @Override
    public void unDuck() {
        Log.d(TAG, "unDuck: ");
    }

    @Override
    public boolean isDucking() {
        Log.d(TAG, "isDucking: ");
        return false;
    }

    @Override
    public boolean canBeDuck() {
        Log.d(TAG, "canBeDuck: ");
        return false;
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }


    @Override
    public int getPlayerState() {
        return mPlayerState;
    }


}
