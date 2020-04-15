package com.yezi.audiotest.viewmodel;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.yezi.audioinfo.AudioInfo;
import com.yezi.audioinfo.DeviceInfo;
import com.yezi.audiotest.model.HomeInfoModel;

import java.util.List;



/**
 * @author : yezi
 * @date : 2020/4/9 10:49
 * desc   :
 * version: 1.0
 */
public class HomeViewModel extends BaseViewModel<HomeInfoModel> {
    public MutableLiveData<List<DeviceInfo>> inputDeviceLiveData = new MutableLiveData<>();
    public MutableLiveData<List<DeviceInfo>> outputDeviceLiveData = new MutableLiveData<>();
    public MutableLiveData<AudioInfo> audioInfo = new MutableLiveData<>();
    public MutableLiveData<Boolean> refresh = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    public void initSource() {
        HomeInfoModel homeInfoSource = HomeInfoModel.getInstance(getApplication());
        homeInfoSource.setInputDeviceListLiveData(inputDeviceLiveData);
        homeInfoSource.setOutputDeviceListLiveData(outputDeviceLiveData);
        homeInfoSource.setAudioInfoLiveData(audioInfo);
        homeInfoSource.observerRefresh(refresh);
    }
}
