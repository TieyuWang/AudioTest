package com.yezi.player.factory;

/**
 * @author : yezi
 * @date : 2020/3/30 10:16
 * desc   :
 * version: 1.0
 */
public interface IPlayerController {
    public final static int PLAYER_UNKNOWN = 0x100;
    public final static int PLAYER_INIT = 0x101;
    public final static int PLAYER_PLAY = 0x102;
    public final static int PLAYER_PAUSE = 0x103;
    public final static int PLAYER_STOP = 0x104;
    public final static int PLAYER_RELEASE = 0x105;

    boolean init();

    boolean play();

    boolean pause();

    boolean resume();

    boolean stop();

    boolean release();

    void setPlayerListener(PlayerListener listener);

    int getPlayerState();
}
