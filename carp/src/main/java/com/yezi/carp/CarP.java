package com.yezi.carp;

import android.app.Application;
import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.media.CarAudioManager;
import android.car.media.ICarVolumeCallback;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.yezi.audioinfo.CarAudioManagerWrapper;

/**
 * @author : yezi
 * @date : 2020/5/6 8:52
 * desc   :
 * version: 1.0
 */
public class CarP implements CarAudioManagerWrapper {
    private static volatile CarP mInstance;
    private Car mCar;
    private CarAudioManager mCarAudioManager;
    private CarVolumeCallback mCarVolumeCallback;
    private CarConnectedStateCallback mCarConnectedStateCallback;

    private CarP(){
    }
    public static CarP getInstance(){
        if(mInstance == null){
            synchronized (CarP.class){
                if(mInstance == null){
                    mInstance = new CarP();
                }
            }
        }
        return mInstance;
    }


    @Override
    public void connectCar(Application application, CarConnectedStateCallback carConnectedStateCallback) {
        mCar = Car.createCar(application.getApplicationContext(),mServiceConnection);
        mCar.connect();
        mCarConnectedStateCallback = carConnectedStateCallback;
    }

    @Override
    public int getVolumeGroupCount() {
        if(mCarAudioManager != null) {
            try {
                return mCarAudioManager.getVolumeGroupCount();
            } catch (CarNotConnectedException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    @Override
    public int[] getUsagesForVolumeGroupId(int groupId) {
        if(mCarAudioManager != null) {
            try {
                return mCarAudioManager.getUsagesForVolumeGroupId(groupId);
            } catch (CarNotConnectedException e) {
                e.printStackTrace();
            }
        }
        return new int[1];
    }

    @Override
    public int getVolumeGroupIdForUsage(int usage) {
        if(mCarAudioManager != null) {
            try {
                return mCarAudioManager.getVolumeGroupIdForUsage(usage);
            } catch (CarNotConnectedException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    @Override
    public int getGroupVolume(int groupId) {
        if(mCarAudioManager != null) {
            try {
                return mCarAudioManager.getGroupVolume(groupId);
            } catch (CarNotConnectedException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    @Override
    public void setGroupVolume(int groupId, int index, int flags) {
        if(mCarAudioManager != null) {
            try {
                mCarAudioManager.setGroupVolume(groupId,index,flags);
            } catch (CarNotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getGroupMinVolume(int groupId) {
        if(mCarAudioManager != null) {
            try {
                return mCarAudioManager.getGroupMinVolume(groupId);
            } catch (CarNotConnectedException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    @Override
    public int getGroupMaxVolume(int groupId) {
        if(mCarAudioManager != null) {
            try {
                return mCarAudioManager.getGroupMaxVolume(groupId);
            } catch (CarNotConnectedException e) {
                e.printStackTrace();
            }
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

    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCarConnectedStateCallback.onConnectedStateChanger(true);
            try {
                mCarAudioManager = (CarAudioManager) mCar.getCarManager(Car.AUDIO_SERVICE);
                mCarAudioManager.registerVolumeCallback(new ICarVolumeCallback.Stub() {
                    @Override
                    public void onGroupVolumeChanged(int groupId, int flags) throws RemoteException {
                        if(mCarVolumeCallback != null)
                            mCarVolumeCallback.onGroupVolumeChange(0,groupId,flags);
                    }

                    @Override
                    public void onMasterMuteChanged(int flags) throws RemoteException {
                        if(mCarVolumeCallback != null)
                            mCarVolumeCallback.onMasterMuteChanged(0,flags);
                    }
                });
            } catch (CarNotConnectedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mCarConnectedStateCallback.onConnectedStateChanger(false);
            unregisterVolumeChangeListener();
        }
    };
}
