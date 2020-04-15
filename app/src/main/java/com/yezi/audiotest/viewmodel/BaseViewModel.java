package com.yezi.audiotest.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.yezi.audiotest.model.BaseModel;


/**
 * @author : yezi
 * @date : 2020/3/28 16:26
 * desc   :
 * version: 1.0
 */
public abstract class BaseViewModel<S extends BaseModel> extends AndroidViewModel {
    public BaseViewModel(@NonNull Application application) {
        super(application);
    }

    /**
    * 初始化source
    */
    public abstract void initSource();
}
