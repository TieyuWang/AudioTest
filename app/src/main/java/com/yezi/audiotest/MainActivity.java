package com.yezi.audiotest;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.yezi.audiotest.databinding.ActivityMainBinding;

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
    }


}
