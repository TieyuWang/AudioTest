package com.yezi.audioinfo;

import android.app.Application;

/**
 * @author : yezi
 * @date : 2020/5/6 9:28
 * desc   : CarAudioManger P Q API不同不兼容 使用此类用于同一管理
 * version: 1.0
 */
public interface CarAudioManagerWrapper{

    void connectCar (Application application,CarConnectedStateCallback carConnectedStateCallback);

    int getVolumeGroupCount();

    int[] getUsagesForVolumeGroupId(int groupId);

    int getVolumeGroupIdForUsage(int usage);

    int getGroupVolume(int groupId);

    void setGroupVolume(int groupId, int index, int flags);

    int getGroupMinVolume(int groupId);

    int getGroupMaxVolume(int groupId);

    void registerVolumeChangeListener(CarVolumeCallback cb);

    void unregisterVolumeChangeListener();

    interface CarVolumeCallback{
        void onGroupVolumeChange(int zoneId, int groupId, int flags);
        void onMasterMuteChanged(int zoneId, int flags);
    }

    interface CarConnectedStateCallback{
        void onConnectedStateChanger(boolean connected);
    }
}


