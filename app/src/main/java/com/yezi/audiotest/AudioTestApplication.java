package com.yezi.audiotest;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

/**
 * @author : yezi
 * @date : 2020/3/10 10:45
 * desc   :
 * version: 1.0
 */
public class AudioTestApplication extends Application implements ViewModelStoreOwner {

    private ViewModelStore mViewModelStore;
    private ViewModelProvider.Factory mFactory;

    @Override
    public void onCreate() {
        super.onCreate();
        mViewModelStore = new ViewModelStore();
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return mViewModelStore;
    }

    public ViewModelProvider getAppViewModelProvider(Activity activity){
        return new ViewModelProvider(checkApplication(activity),getAppFactory(activity));
    }

    private ViewModelProvider.Factory getAppFactory(Activity activity) {
        if(mFactory == null){
            mFactory = ViewModelProvider.AndroidViewModelFactory
                    .getInstance(checkApplication(activity));
        }
        return mFactory;
    }

    private AudioTestApplication checkApplication(Activity activity){
        AudioTestApplication application = (AudioTestApplication) activity.getApplication();
        if (application == null) {
            throw new IllegalStateException("Your activity/fragment is not yet attached to "
                    + "Application. You can't request ViewModel before onCreate call.");
        }
        return application;
    }

}
