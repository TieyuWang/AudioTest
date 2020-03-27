package com.yezi.audiotest.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yezi.audiotest.R;
import com.yezi.audiotest.databinding.FragmentMixerTestBinding;

/**
 * @author : yezi
 * @date : 2020/3/27 17:36
 * desc   :
 * version: 1.0
 */
public class MixerTestFragment extends Fragment {
    private FragmentMixerTestBinding mMixerTestBinding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mixer_test,container,false);
        mMixerTestBinding = FragmentMixerTestBinding.bind(view);
        return view;
    }
}
