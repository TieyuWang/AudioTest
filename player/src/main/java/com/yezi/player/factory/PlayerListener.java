package com.yezi.player.factory;

/**
 * @author : yezi
 * @date : 2020/3/31 16:08
 * desc   :
 * version: 1.0
 */
public interface PlayerListener {
    /**
     * 更新player回调
     * @param state player状态
     */
    void onPlayerStateUpdate(int state);
}
