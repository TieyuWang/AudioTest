package com.yezi.audiotest;

import android.app.Application;
import android.util.Log;

/**
 * @author : yezi
 * @date : 2020/3/10 10:45
 * desc   :
 * version: 1.0
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("test", "onCreate: ");
    }
}
