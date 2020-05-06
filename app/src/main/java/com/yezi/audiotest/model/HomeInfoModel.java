package com.yezi.audiotest.model;

import android.annotation.SuppressLint;
import android.app.Application;
import android.media.AudioDeviceInfo;
import android.os.AsyncTask;
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
    private final String TAG = "HomeInfoModel";
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
                    new DeviceInfoTask(false).execute();
                    new DeviceInfoTask(true).execute();
                }
            }
        });
    }

    class DeviceInfoTask extends AsyncTask<Void,Void,List<DeviceInfo>>{
        boolean isOutPut;

        DeviceInfoTask(boolean isOutput){
            this.isOutPut = isOutput;
        }

        @Override
        protected List<DeviceInfo> doInBackground(Void... voids) {
            return getDeviceList(this.isOutPut);
        }
        @Override
        protected void onPostExecute(List<DeviceInfo> deviceInfos) {
            super.onPostExecute(deviceInfos);
            MutableLiveData<List<DeviceInfo>> liveData = isOutPut ? mOutputLiveData : mInputLiveData;
            if(liveData != null)
                liveData.setValue(deviceInfos);
        }
    }

    /*@SuppressLint("StaticFieldLeak")
    AsyncTask<Void,Void,List<DeviceInfo>> inputDeviceTask = new AsyncTask<Void, Void, List<DeviceInfo>>() {

        @Override
        protected List<DeviceInfo> doInBackground(Void... voids) {
            return getDeviceList(false);
        }

        @Override
        protected void onPostExecute(List<DeviceInfo> deviceInfos) {
            super.onPostExecute(deviceInfos);
            if(mInputLiveData != null)
                mInputLiveData.setValue(deviceInfos);
        }
    };

    @SuppressLint("StaticFieldLeak")
    AsyncTask<Void,Void,List<DeviceInfo>> outputDeviceTask = new AsyncTask<Void, Void, List<DeviceInfo>>() {

        @Override
        protected List<DeviceInfo> doInBackground(Void... voids) {
            return getDeviceList(true);
        }

        @Override
        protected void onPostExecute(List<DeviceInfo> deviceInfos) {
            super.onPostExecute(deviceInfos);
            if(mOutputLiveData != null)
                mOutputLiveData.setValue(deviceInfos);
        }
    };*/

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
