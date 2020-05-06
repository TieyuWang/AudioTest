package com.yezi.audiotest.model;

import android.annotation.SuppressLint;
import android.app.Application;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.yezi.audioinfo.AudioInfoUtils;
import com.yezi.audioinfo.CarAudioManagerWrapper;
import com.yezi.audiotest.bean.VolumeInfo;
import com.yezi.carp.CarP;
import com.yezi.carq.CarQ;

import java.util.ArrayList;
import java.util.List;


/**
 * @author : yezi
 * @date : 2020/4/15 18:27
 * desc   :
 * version: 1.0
 */
public class VolumeModel extends BaseModel{
    private final String TAG = "VolumeModel";
    private static VolumeModel mInstance;
    private Application mApplication;
    private int mMinGroupId;
    private int mMaxGroupId;
    private CarAudioManagerWrapper mCarAudioWrapper;
    private MutableLiveData<List<VolumeInfo>> mVolumeListLiveDate;
    private List<VolumeInfo> mVolumeInfoList;



    private VolumeModel(Application application){
        mApplication = application;
    }


    private final static int MSG_CAR_CONNECTED = 0;
    private final static int MSG_CAR_DISCONNECTED = 1;
    private final static int MSG_UPDATE_LIST = 2;
    @SuppressLint("HandlerLeak")
    Handler mCarHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "handleMessage: "+msg);
            if(msg.what == MSG_CAR_CONNECTED){
                initVolumeSource();
            }
            if(msg.what == MSG_UPDATE_LIST){
                mVolumeListLiveDate.setValue(mVolumeInfoList);
            }
        }
    };

    private void initVolumeSource() {
        mMaxGroupId = mCarAudioWrapper.getVolumeGroupCount() - 1;
        mVolumeInfoList = new ArrayList<>(mMaxGroupId);
        for(int groupId = 0; groupId <= mMaxGroupId; groupId++){
            VolumeInfo volumeInfo = new VolumeInfo(groupId);
            volumeInfo.setMax(mCarAudioWrapper.getGroupMaxVolume(groupId));
            volumeInfo.setMin(mCarAudioWrapper.getGroupMinVolume(groupId));
            volumeInfo.setCurrent(mCarAudioWrapper.getGroupVolume(groupId));
            String contextName = AudioInfoUtils.usageToCarContextInfo(
                    mCarAudioWrapper.getUsagesForVolumeGroupId(groupId)[0]);
            String name = contextName.substring(contextName.lastIndexOf('.') + 1);
            volumeInfo.setContextName(name);
            mVolumeInfoList.add(volumeInfo);
        }
        mCarHandler.sendEmptyMessage(MSG_UPDATE_LIST);
    }

    private void updateVolumeList(int groupId, int currentVolume, int flags) {
        for(VolumeInfo info : mVolumeInfoList){
            if(groupId == info.getVolumeGroupId()){
                info.setCurrent(currentVolume);
                switch (flags){
                    case AudioManager.ADJUST_LOWER:
                        Log.d(TAG, "groupId = "+groupId+" ADJUST_LOWER");
                        break;
                    case AudioManager.ADJUST_RAISE:
                        Log.d(TAG, "groupId = "+groupId+" ADJUST_RAISE");
                        break;
                    case AudioManager.ADJUST_MUTE:
                        //静音
                        info.setMute(true);
                        break;
                    case AudioManager.ADJUST_UNMUTE:
                        //取消静音
                        info.setMute(false);
                        break;
                    case AudioManager.ADJUST_TOGGLE_MUTE:
                        //物理静音键
                        Log.d(TAG, "groupId = "+groupId+" ADJUST_TOGGLE_MUTE");
                        break;
                    case AudioManager.ADJUST_SAME:
                    default:
                        break;
                }
                break;
            }
        }
        mCarHandler.sendEmptyMessage(MSG_UPDATE_LIST);
    }

    public static VolumeModel getInstance(Application application){
        if(mInstance == null){
            synchronized (VolumeModel.class){
                if(mInstance == null){
                    mInstance = new VolumeModel(application);
                }
            }
        }
        return mInstance;
    }

    public void init() {

        if(!AudioInfoUtils.isCar(mApplication)){
            Log.w(TAG, "init failed current device is not car");
            return;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            mCarAudioWrapper = CarQ.getInstance();
        }else{
            mCarAudioWrapper = CarP.getInstance();
        }
        mCarAudioWrapper.registerVolumeChangeListener(new CarAudioManagerWrapper.CarVolumeCallback() {
            @Override
            public void onGroupVolumeChange(int zoneId, int groupId, int flags) {
                int currentVolume = mCarAudioWrapper.getGroupVolume(groupId);
                updateVolumeList(groupId, currentVolume, flags);
            }

            @Override
            public void onMasterMuteChanged(int zoneId, int flags) {
                updateVolumeList(-1, -1, flags);
            }
        });
        mCarAudioWrapper.connectCar(mApplication, new CarAudioManagerWrapper.CarConnectedStateCallback() {
            @Override
            public void onConnectedStateChanger(boolean connected) {
                mCarHandler.sendEmptyMessage(connected ? MSG_CAR_CONNECTED : MSG_CAR_DISCONNECTED);
            }
        });
    }

    public void observerCmd(MutableLiveData<VolumeInfo> volumeChangeCmd) {
        volumeChangeCmd.observeForever(new Observer<VolumeInfo>() {
            @Override
            public void onChanged(VolumeInfo volumeInfo) {
                Log.d(TAG, "onChanged: "+volumeInfo);
                if(isSafeVolumeInfo(volumeInfo)){
                    Log.d(TAG, "onChanged: ");
                    mCarAudioWrapper.setGroupVolume(volumeInfo.getVolumeGroupId(),volumeInfo.getCurrent(),0);
                }
            }
        });
    }

    private boolean isSafeVolumeInfo(VolumeInfo volumeInfo) {
        int groupId = volumeInfo.getVolumeGroupId();
        int current = volumeInfo.getCurrent();
        return groupId >= 0 && groupId <= mMaxGroupId
                && current >= volumeInfo.getMin()
                && current <= volumeInfo.getMax();
    }


    public void setVolumeListLiveDate(MutableLiveData<List<VolumeInfo>> volumeList) {
        mVolumeListLiveDate = volumeList;
    }


}
