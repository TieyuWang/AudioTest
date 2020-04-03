package com.yezi.audiotest.bean;

import com.yezi.player.factory.IPlayerController;

/**
 * @author : yezi
 * @date : 2020/4/3 8:43
 * desc   :
 * version: 1.0
 */
public class PlayerControl {

    public int cmd;
    public int sessionId;
    public int pid;

    public PlayerControl (LocalPlayerInfo localPlayerInfo){
        if(localPlayerInfo.getIsPlaying()){
            cmd = IPlayerController.PLAYER_PAUSE;
        }else {
            cmd = IPlayerController.PLAYER_PLAY;
        }
        sessionId = localPlayerInfo.getMediaSessionId();
        pid = localPlayerInfo.getPid();
    }
}
