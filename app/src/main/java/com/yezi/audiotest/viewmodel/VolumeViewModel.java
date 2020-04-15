package com.yezi.audiotest.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;

import com.yezi.audiotest.model.BaseModel;

/**
 * @author : yezi
 * @date : 2020/4/15 17:54
 * desc   :
 * version: 1.0
 */
public class VolumeViewModel extends BaseViewModel<BaseModel> {
    public VolumeViewModel(@NonNull Application application) {
        super(application);
    }


    @Override
    public void initSource() {

    }
}
