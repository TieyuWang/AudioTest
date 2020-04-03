package com.yezi.audioinfo;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author : yezi
 * @date : 2020/3/28 11:14
 * desc   :
 * version: 1.0
 */
public class AudioInfoSearcher {
    private final static String TAG = "AudioInfoSearcher";
    private static volatile AudioInfoSearcher mInstance;

    private AudioInfoSearcher(){

    }

    public static AudioInfoSearcher getInstance(){
        if(mInstance == null){
            synchronized (AudioInfoSearcher.class){
                if(mInstance == null){
                    mInstance = new AudioInfoSearcher();
                }
            }
        }
        return mInstance;
    }

    public static boolean useCarAudioService(@NonNull Application context){
        boolean isCar = context.getPackageManager().hasSystemFeature(
                                PackageManager.FEATURE_AUTOMOTIVE);
        Log.d(TAG, "useCarAudioService: "+isCar);
        return isCar;
    }

    public static HashMap<String,Integer> searchAllStreamType(){

        HashMap<String,Integer> map = new HashMap<>();
        Class<?> audioManagerClass = AudioManager.class;
        Field[] fields = audioManagerClass.getDeclaredFields();
        for(Field field : fields){
            if(field.getName().startsWith("STREAM_")){
                try {
                    map.put(field.getName(),field.getInt(null));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

    public static HashMap<String,Integer> searchAllUsageType(){
        HashMap<String,Integer> map = new HashMap<>();
        Class<?> audioAttributesClass = AudioAttributes.class;
        Field[] fields = audioAttributesClass.getDeclaredFields();
        for(Field field : fields){
            if(field.getName().startsWith("USAGE_")){
                try {
                    map.put(field.getName(),field.getInt(null));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }


    public static List<AudioDeviceInfo> getInputDevices(@NonNull Application context){
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if(audioManager == null){
            Log.w(TAG, "getOutputDevices: can not get AudioManger");
            return null;
        }
        AudioDeviceInfo[] inputDevices = audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS);
        if(inputDevices.length == 0){
            Log.w(TAG, "getOutputDevices: inputDevice");
            return null;
        }
        return Arrays.asList(inputDevices);
    }

    public static List<AudioDeviceInfo> getOutputDevices(@NonNull Application context){
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if(audioManager == null){
            Log.w(TAG, "getOutputDevices: can not get AudioManger");
            return null;
        }
        AudioDeviceInfo[] outputDevices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
        if(outputDevices.length == 0){
            Log.w(TAG, "getOutputDevices: outputDevice");
            return null;
        }
        return Arrays.asList(outputDevices);
    }


}
