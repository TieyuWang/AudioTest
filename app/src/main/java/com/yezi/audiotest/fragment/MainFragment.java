package com.yezi.audiotest.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.yezi.audiotest.R;
import com.yezi.audiotest.databinding.FragmentMainBinding;

/**
 * @author : yezi
 * @date : 2020/3/26 16:40
 * desc   :
 * version: 1.0
 */
public class MainFragment extends Fragment {
    private final String TAG = "MainFragment";
    private FragmentMainBinding mFragmentMainBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: viewModel 初始化 获取
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mFragmentMainBinding = FragmentMainBinding.bind(view);
        final NavController navController = NavHostFragment.findNavController(this);
        Log.d(TAG, "onCreateView: "+navController);
        //TODO：mFragmentMainBinding 设置data
        mFragmentMainBinding.buttonJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_mainFragment_to_leftSlideFragment2);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //TODO：viewModel 数据初始化  liveData observe
    }


}

