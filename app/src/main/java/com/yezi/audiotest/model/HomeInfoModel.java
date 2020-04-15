package com.yezi.audiotest.model;

import android.app.Application;
import android.media.AudioDeviceInfo;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.yezi.audioinfo.AudioInfo;
import com.yezi.audioinfo.AudioInfoSearcher;
import com.yezi.audioinfo.DeviceInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : yezi
 * @date : 2020/4/9 15:52
 * desc   :
 * version: 1.0
 */
public class HomeInfoModel extends BaseModel {
    private final String TAG = "HomeInfoSource";
    private static HomeInfoModel mInstance;
    private Application mApplication;
    private List<DeviceInfo> mInputList;
    private List<DeviceInfo> mOutputList;
    private MutableLiveData<List<DeviceInfo>> mInputLiveData;
    private MutableLiveData<List<DeviceInfo>> mOutputLiveData;


    private HomeInfoModel(Application application){
        mApplication = application;
    }

    public static HomeInfoModel getInstance(Application application){
        if(mInstance == null){
            synchronized (HomeInfoModel.class){
                if(mInstance == null){
                    mInstance = new HomeInfoModel(application);
                }
            }
        }
        return mInstance;
    }

    public void observerRefresh(MutableLiveData<Boolean> refresh) {
        refresh.observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean needRefresh) {
                if(needRefresh){
                    mInputList = getDeviceList(false);
                    mOutputList = getDeviceList(true);
                    Log.d(TAG, "onChanged: input "+mInputList);
                    Log.d(TAG, "onChanged: output "+mOutputList);
                    if(mInputLiveData != null)
                        mInputLiveData.setValue(mInputList);
                    if(mOutputLiveData != null)
                        mOutputLiveData.setValue(mOutputList);
                }
            }
        });
    }

    private List<DeviceInfo> getDeviceList(boolean isOutput) {
        List<AudioDeviceInfo> audioDeviceInfoList;
        if(isOutput) {
            audioDeviceInfoList = AudioInfoSearcher.getOutputDevices(mApplication);
        }else{
            audioDeviceInfoList = AudioInfoSearcher.getInputDevices(mApplication);
        }
        if(audioDeviceInfoList == null){
            Log.w(TAG, "getDeviceList: device list is null");
            return new ArrayList<DeviceInfo>();
        }
        List<DeviceInfo> resList = new ArrayList<>(audioDeviceInfoList.size());
        for(AudioDeviceInfo audioDeviceInfo : audioDeviceInfoList){
            DeviceInfo deviceInfo = new DeviceInfo(audioDeviceInfo);
            resList.add(deviceInfo);
        }
        return resList;
    }

    public void setInputDeviceListLiveData(MutableLiveData<List<DeviceInfo>> inputDeviceLiveData) {
        mInputLiveData = inputDeviceLiveData;
    }

    public void setOutputDeviceListLiveData(MutableLiveData<List<DeviceInfo>> outputDeviceLiveData) {
        mOutputLiveData = outputDeviceLiveData;
    }


    public void setAudioInfoLiveData(MutableLiveData<AudioInfo> audioInfo) {
    }
}
