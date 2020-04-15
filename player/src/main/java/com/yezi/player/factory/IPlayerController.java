package com.yezi.player.factory;

import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;

/**
 * @author : yezi
 * @date : 2020/3/30 10:16
 * desc   :
 * version: 1.0
 */
public interface IPlayerController {
/*    int PLAYER_UNKNOWN = 0x100;
    int PLAYER_INIT = 0x101;
    int PLAYER_PLAY = 0x102;
    int PLAYER_PAUSE = 0x103;
    int PLAYER_STOP = 0x104;
    int PLAYER_RELEASE = 0x105;*/
    int PLAYER_UNKNOWN = 100;
    int PLAYER_INIT =  101;
    int PLAYER_PLAY =  102;
    int PLAYER_PAUSE =  103;
    int PLAYER_STOP =  104;
    int PLAYER_RELEASE =  105;


    boolean init(AudioAttributes audioAttributes, AssetFileDescriptor assetFd,
                 PlayerListener listener);

    void play();

    void pause();

    void resume();

    void stop();

    void release();

    void duck();

    void unDuck();

    boolean isDucking();

    boolean canBeDuck();

    boolean isPlaying();

    int getPlayerState();
}
