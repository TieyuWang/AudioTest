package com.yezi.audiotest.viewmodel;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yezi.audioinfo.AudioInfo;
import com.yezi.audioinfo.DeviceInfo;
import com.yezi.audiotest.source.HomeInfoSource;

import java.util.List;

/**
 * @author : yezi
 * @date : 2020/4/9 10:49
 * desc   :
 * version: 1.0
 */
public class HomeViewModel extends AndroidViewModel {
    public MutableLiveData<List<DeviceInfo>> inputDeviceLiveData = new MutableLiveData<>();
    public MutableLiveData<List<DeviceInfo>> outputDeviceLiveData = new MutableLiveData<>();
    public MutableLiveData<AudioInfo> audioInfo = new MutableLiveData<>();
    public MutableLiveData<Boolean> refresh = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        initSource();
    }

    private void initSource() {
        HomeInfoSource homeInfoSource = HomeInfoSource.getInstance(getApplication());
        homeInfoSource.setInputDeviceListLiveData(inputDeviceLiveData);
        homeInfoSource.setOutputDeviceListLiveData(outputDeviceLiveData);
        homeInfoSource.setAudioInfoLiveData(audioInfo);
        homeInfoSource.observerRefresh(refresh);
    }
}
