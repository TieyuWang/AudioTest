package com.yezi.audiotest.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.yezi.audiotest.AudioTestApplication;

/**
 * @author : yezi
 * @date : 2020/3/30 9:53
 * desc   :
 * version: 1.0
 */
public abstract class BaseFragment extends Fragment {
    private final String TAG = getLogTag();

    /**
     * 获取当前子类的log tag
     * @return tag
     */
    protected abstract String getLogTag();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: ");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected ViewModelProvider getAppViewModelProvider(){
        Activity host = getActivity();
        if(host != null) {
            AudioTestApplication audioTestApplication = (AudioTestApplication) host.getApplication();
            return audioTestApplication.getAppViewModelProvider(host);
        }
        return null;
    }
}
