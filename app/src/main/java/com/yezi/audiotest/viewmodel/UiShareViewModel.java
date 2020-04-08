package com.yezi.audiotest.viewmodel;

import android.app.Application;
import android.content.res.Resources;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.Navigation;

/**
 * @author : yezi
 * @date : 2020/4/7 16:19
 * desc   :
 * version: 1.0
 */
public class UiShareViewModel extends AndroidViewModel {
    public final MutableLiveData<Boolean> showLeftDrawer = new MutableLiveData<>();
    public final MutableLiveData<Integer> fragmentNavAction = new MutableLiveData<>();

    public UiShareViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
