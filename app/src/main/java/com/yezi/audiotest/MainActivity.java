package com.yezi.audiotest;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.yezi.audioinfo.AudioInfoSearcher;
import com.yezi.audiotest.databinding.ActivityMainBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingDeque;

/**
 * @author : yezi
 * @date : 2020/3/26 16:36
 * desc   :
 * version: 1.0
 */
public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding activityMainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
     //   NavController navController = Navigation.findNavController(this,R.id.fragment_first);
        HashMap<String,Integer> map = AudioInfoSearcher.searchAllUsageType();
        for(Map.Entry<String,Integer> entry :map.entrySet()){
            Log.d(TAG, "searchAllUsageType: "+entry.getKey()+" "+entry.getValue());
        }

        HashMap<String,Integer> map2 = AudioInfoSearcher.searchAllStreamType();
        for(Map.Entry<String,Integer> entry :map2.entrySet()){
            Log.d(TAG, "searchAllStreamType: "+entry.getKey()+" "+entry.getValue());
        }
        Log.d(TAG, "onCreate: "+AudioInfoSearcher.useCarAudioService(getApplication()));

    }


}
