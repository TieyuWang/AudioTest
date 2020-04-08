package com.yezi.audiotest.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yezi.audiotest.R;
import com.yezi.audiotest.databinding.FragmentEffectTestBinding;

/**
 * @author : yezi
 * @date : 2020/4/7 15:43
 * desc   :
 * version: 1.0
 */
public class EffectTestFragment extends Fragment {
    private FragmentEffectTestBinding mMixerTestBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_effect_test,container,false);
        mMixerTestBinding = FragmentEffectTestBinding.bind(view);
        return view;
    }
}
