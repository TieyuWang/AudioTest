package com.yezi.audiotest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author GW00175635
 */
public class DeviceTestActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    //    setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        AudioDeviceInfo[] inputDevices = audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS);
        AudioDeviceInfo[] outputDevices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
        Log.d(TAG, "onCreate: inputDevices "+inputDevices);
        if(inputDevices!=null) {
            Log.d(TAG, "onCreate: inputDevices count " + inputDevices.length);
        }
        Log.d(TAG, "onCreate: outputDevices "+outputDevices);
        if(inputDevices!=null) {
            Log.d(TAG, "onCreate: outputDevices count " + outputDevices.length);
        }
        for(int i=0; i<inputDevices.length; i++){
            Log.d(TAG, "testw: "+i+" inputDevice "+inputDevices[i]);
            showDeviceInfo(inputDevices[i]);
        }
        for(int i=0; i<outputDevices.length; i++){
            Log.d(TAG, "testw: "+i+" outputDevice "+outputDevices[i]);
            showDeviceInfo(outputDevices[i]);
        }

    }

    private void showDeviceInfo(AudioDeviceInfo device) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n ProductName = ");
        stringBuilder.append(device.getProductName());
        stringBuilder.append("\n Id = ");
        stringBuilder.append(device.getId());
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.P) {
            stringBuilder.append("\n Address = ");
            stringBuilder.append(device.getAddress());
        }
        stringBuilder.append("\n Type = ");
        stringBuilder.append(device.getType());
        stringBuilder.append("\n SampleRates = ");
        stringBuilder.append(Arrays.toString(device.getSampleRates()));
        stringBuilder.append("\n ChannelCounts = ");
        stringBuilder.append(Arrays.toString(device.getChannelCounts()));
        stringBuilder.append("\n IndexMasks = ");
        stringBuilder.append(Arrays.toString(device.getChannelIndexMasks()));
        stringBuilder.append("\n Encodings = ");
        stringBuilder.append(Arrays.toString(device.getEncodings()));
        Log.d("testw", "showDeviceInfo: "+stringBuilder);
    }
}
