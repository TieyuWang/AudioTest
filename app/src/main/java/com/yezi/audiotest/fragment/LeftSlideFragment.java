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

/**
 * @author : yezi
 * @date : 2020/3/26 16:39
 * desc   :
 * version: 1.0
 */
public class LeftSlideFragment extends Fragment {
    private final String TAG = "LeftSlideFragment";
   // private FragmentLeftBinding mFragmentLeftBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: viewModel 初始化 获取
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.left_navigation_view_header, container, false);
        /*mFragmentLeftBinding = FragmentLeftBinding.bind(view);
        final NavController navController = NavHostFragment.findNavController(this);
        Log.d(TAG, "onCreateView: "+navController);
        //TODO：mFragmentMainBinding 设置data
        mFragmentLeftBinding.leftText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_leftSlideFragment_to_mixerTestFragment);
            }
        });*/
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //TODO：viewModel 数据初始化  liveData observe
    }
}
