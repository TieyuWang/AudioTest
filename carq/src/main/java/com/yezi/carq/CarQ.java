package com.yezi.carq;

import android.app.Application;
import android.car.Car;
import android.car.media.CarAudioManager;
import android.util.Log;

import com.yezi.audioinfo.CarAudioManagerWrapper;

/**
 * @author : yezi
 * @date : 2020/5/6 9:46
 * desc   :
 * version: 1.0
 */
public class CarQ implements CarAudioManagerWrapper {
    private final static String TAG = "CarQ";
    private static volatile CarQ mInstance;
    private Car mCar;
    private CarAudioManager mCarAudioManager;
    private CarVolumeCallback mCarVolumeCallback;
    private boolean isSupportGwmCarAudioManager;

    private CarQ(){
    }
    public static CarQ getInstance(){
        if(mInstance == null){
            synchronized (CarQ.class){
                if(mInstance == null){
                    mInstance = new CarQ();
                }
            }
        }
        return mInstance;
    }


    @Override
    public void connectCar(Application application, final CarConnectedStateCallback carConnectedStateCallback) {
        mCar = Car.createCar(application.getApplicationContext());
        Log.d(TAG, "connectCar: ");
        Runnable waitForCarConnected = new Runnable() {
            @Override
            public void run() {
                while (mCar.isConnecting() || mCar.isConnected()){
                    Log.d(TAG, "run: ");
                    if(mCar.isConnected()){
                        carConnectedStateCallback.onConnectedStateChanger(true);
                        mCarAudioManager = (CarAudioManager) mCar.getCarManager(Car.AUDIO_SERVICE);
                        mCarAudioManager.registerCarVolumeCallback(new CarAudioManager.CarVolumeCallback() {
                            @Override
                            public void onGroupVolumeChanged(int zoneId, int groupId, int flags) {
                                mCarVolumeCallback.onGroupVolumeChange(zoneId,groupId,flags);
                            }

                            @Override
                            public void onMasterMuteChanged(int zoneId, int flags) {
                                mCarVolumeCallback.onMasterMuteChanged(zoneId,flags);
                            }
                        });
                        isSupportGwmCarAudioManager = checkGwmSupport();
                        Log.d(TAG, "init: isSupportGwmCarAudioManager "+isSupportGwmCarAudioManager);
                        break;
                    }
                }
            }
        };
        new Thread(waitForCarConnected).start();
    }

    @Override
    public int getVolumeGroupCount() {
        if(mCarAudioManager != null) {
            return mCarAudioManager.getVolumeGroupCount();
        }
        return -1;
    }

    @Override
    public int[] getUsagesForVolumeGroupId(int groupId) {
        if(mCarAudioManager != null) {
            return mCarAudioManager.getUsagesForVolumeGroupId(groupId);
        }
        return new int[1];
    }

    @Override
    public int getVolumeGroupIdForUsage(int usage) {
        if(mCarAudioManager != null) {
            return mCarAudioManager.getVolumeGroupIdForUsage(usage);
        }
        return -1;
    }

    @Override
    public int getGroupVolume(int groupId) {
        if(mCarAudioManager != null) {
            return mCarAudioManager.getGroupVolume(groupId);
        }
        return -1;
    }

    @Override
    public void setGroupVolume(int groupId, int index, int flags) {
        if(mCarAudioManager != null) {
            mCarAudioManager.setGroupVolume(groupId,index,flags);
        }
    }

    @Override
    public int getGroupMinVolume(int groupId) {
        if(mCarAudioManager != null) {
            return mCarAudioManager.getGroupMinVolume(groupId);
        }
        return -1;
    }

    @Override
    public int getGroupMaxVolume(int groupId) {
        if(mCarAudioManager != null) {
            return mCarAudioManager.getGroupMaxVolume(groupId);
        }
        return -1;
    }

    @Override
    public void registerVolumeChangeListener(CarAudioManagerWrapper.CarVolumeCallback cb) {
        mCarVolumeCallback = cb;
    }

    @Override
    public void unregisterVolumeChangeListener() {
        mCarAudioManager = null;
    }

    private boolean checkGwmSupport() {
        try {
            return Class.forName("android.car.media.GwmCarAudioManager") != null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
