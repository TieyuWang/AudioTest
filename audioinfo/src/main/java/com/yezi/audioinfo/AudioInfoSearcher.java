package com.yezi.audioinfo;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
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

    public static HashMap<Integer,String> searchAllStreamType(){
        return searchAllFiledWithPrefix(AudioManager.class,"STREAM_");
    }

    public static HashMap<Integer,String> searchAllUsageType(){
        return searchAllFiledWithPrefix(AudioAttributes.class,"USAGE_");
    }

    public static HashMap<Integer,String> searchAllDeviceType(){
        return searchAllFiledWithPrefix(AudioDeviceInfo.class,"TYPE_");
    }

    public static HashMap<Integer,String> searchAllChannelType(){
        return searchAllFiledWithPrefix(AudioFormat.class,"CHANNEL_");
    }

    public static HashMap<Integer,String> searchAllEncodingType(){
        return searchAllFiledWithPrefix(AudioFormat.class,"ENCODING_");
    }


    private static HashMap<Integer,String> searchAllFiledWithPrefix(Class clazz,String prefix){
        HashMap<Integer,String> map = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields){
            if(field.getName().startsWith(prefix)){
                try {
                    if(prefix.equals("USAGE_")){
                        Log.d(TAG, "searchAllFiledWithPrefix: "+field.getName()+" "+field.getInt(null));
                    }
                    map.put(field.getInt(null),field.getName());
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
