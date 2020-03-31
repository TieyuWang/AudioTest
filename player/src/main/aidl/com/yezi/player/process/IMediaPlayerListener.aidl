// IMediaPlayerListener.aidl
package com.yezi.player.process;

// Declare any non-default types here with import statements

interface IMediaPlayerListener {

     void onPidUpdate(int pid);

     void onPlayerStateUpdate(int state);
}
