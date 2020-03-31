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
public class Player implements IPlayerController{
    private final String TAG = "Player";
    private MediaPlayer mediaPlayer;
    private AudioAttributes audioAttributes;
    private AssetFileDescriptor sourceFD;
    private PlayerListener mListener;

    private int mPlayerState = PLAYER_UNKNOWN;

    public Player(AudioAttributes audioAttributes, AssetFileDescriptor sourceFD){
        Log.d(TAG, "Player: ");
        mediaPlayer = new MediaPlayer();
        this.sourceFD = sourceFD;
        this.audioAttributes = audioAttributes;
    }

    @Override
    public boolean init() {
        Log.d(TAG, "init: "+audioAttributes+" "+sourceFD);
        try {
          mediaPlayer.setAudioAttributes(audioAttributes);
          mediaPlayer.setDataSource(sourceFD);
          mediaPlayer.prepare();
          return true;
        } catch (IOException e) {
          e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean play() {
        Log.d(TAG, "play: ");
        mediaPlayer.start();
        Log.d(TAG, "mediaPlayer.isPlaying(): "+mediaPlayer.isPlaying());
        mPlayerState = PLAYER_PLAY;
        notifyPlayerState();
        return mediaPlayer.isPlaying();
    }

    private void notifyPlayerState() {
        mListener.onPlayerStateUpdate(mPlayerState);
    }

    @Override
    public boolean pause() {
        mediaPlayer.pause();
        mPlayerState = PLAYER_PAUSE;
        notifyPlayerState();
        return !mediaPlayer.isPlaying();
    }

    @Override
    public boolean resume() {
        mediaPlayer.start();
        mPlayerState = PLAYER_PLAY;
        notifyPlayerState();
        return mediaPlayer.isPlaying();
    }

    @Override
    public boolean stop() {
        mediaPlayer.stop();
        mPlayerState = PLAYER_STOP;
        notifyPlayerState();
        return !mediaPlayer.isPlaying();
    }

    @Override
    public boolean release() {
        mediaPlayer.release();
        mPlayerState = PLAYER_RELEASE;
        notifyPlayerState();
        return !mediaPlayer.isPlaying();
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
