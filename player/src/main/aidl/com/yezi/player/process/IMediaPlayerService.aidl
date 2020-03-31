// IMediaPlayerService.aidl
package com.yezi.player.process;
import com.yezi.player.bean.PlayerInfo;
import com.yezi.player.process.IMediaPlayerListener;

// Declare any non-default types here with import statements

interface IMediaPlayerService {
    boolean init(inout PlayerInfo info);

    boolean play();

    boolean pause();

    boolean resume();

    boolean stop();

    boolean release();

    int addMediaPlayerListener(IMediaPlayerListener listener);

    void removeMediaPlayerListener(int listenerId);
}
