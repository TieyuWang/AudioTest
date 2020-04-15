package com.yezi.audiotest.fragment;

import android.annotation.LayoutRes;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.yezi.audiotest.AudioTestApplication;
import com.yezi.audiotest.viewmodel.BaseViewModel;

/**
 * @author : yezi
 * @date : 2020/3/30 9:53
 * desc   :
 * version: 1.0
 */
public abstract class BaseFragment<D extends ViewDataBinding,VM extends BaseViewModel> extends Fragment {
    private final String TAG = getLogTag();
    protected D mFragmentBinding;
    protected VM mFragmentViewModel;
    /**
     * 获取当前子类的log tag
     * @return tag
     */
    protected abstract String getLogTag();

    protected abstract @LayoutRes int getFragmentLayoutRes();

    protected abstract Class<VM> getViewModeClass();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: ");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentViewModel = getAppViewModelProvider().get(getViewModeClass());
        mFragmentViewModel.initSource();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getFragmentLayoutRes(),container,false);
        mFragmentBinding = DataBindingUtil.bind(view);
        return view;
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
