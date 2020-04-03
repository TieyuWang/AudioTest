package com.yezi.audiotest.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;


/**
 * @author : yezi
 * @date : 2020/3/28 16:26
 * desc   :
 * version: 1.0
 */
public abstract class BaseViewModel extends AndroidViewModel {
    public BaseViewModel(@NonNull Application application) {
        super(application);
      //  initSource();
    }

 /*   *//**
     * 初始化source
     *//*
    protected abstract void initSource();*/
}
