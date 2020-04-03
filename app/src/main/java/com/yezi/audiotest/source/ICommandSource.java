package com.yezi.audiotest.source;

import android.os.Bundle;

/**
 * @author : yezi
 * @date : 2020/3/30 9:32
 * desc   :
 * version: 1.0
 */
public interface ICommandSource {
    /**
     * 当命令
     * @param cmd 命令
     */
    void onCommand(Bundle cmd);
}
