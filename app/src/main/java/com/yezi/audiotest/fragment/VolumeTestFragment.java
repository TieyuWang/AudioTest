package com.yezi.audiotest.fragment;

import androidx.fragment.app.Fragment;

import com.yezi.audiotest.R;
import com.yezi.audiotest.databinding.FragmentVolumeTestBinding;
import com.yezi.audiotest.viewmodel.VolumeViewModel;

/**
 * @author : yezi
 * @date : 2020/3/26 16:39
 * desc   :
 * version: 1.0
 */
public class VolumeTestFragment extends BaseFragment<FragmentVolumeTestBinding, VolumeViewModel> {
    final static String TAG = "VolumeTestFragment";

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    protected int getFragmentLayoutRes() {
        return R.layout.fragment_volume_test;
    }

    @Override
    protected Class<VolumeViewModel> getViewModeClass() {
        return VolumeViewModel.class;
    }
}
