package com.yezi.audiotest.fragment;


import android.content.Context;
import android.media.AudioDeviceInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yezi.audioinfo.AudioInfo;
import com.yezi.audioinfo.DeviceInfo;
import com.yezi.audiotest.R;
import com.yezi.audiotest.adpter.BaseRecycleViewAdapter;
import com.yezi.audiotest.databinding.FragmentHomeBinding;
import com.yezi.audiotest.databinding.ItemDeviceInfoViewBinding;
import com.yezi.audiotest.viewmodel.HomeViewModel;

import java.io.Serializable;
import java.util.List;

import static androidx.recyclerview.widget.RecyclerView.VERTICAL;


/**
 * @author : yezi
 * @date : 2020/3/26 16:40
 * desc   :
 * version: 1.0
 */
public class HomeFragment extends BaseFragment {
    private final String TAG = "HomeFragment";
    private FragmentHomeBinding mFragmentMainBinding;
    private HomeViewModel mHomeViewModel;


    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mFragmentMainBinding = FragmentHomeBinding.bind(view);

        mHomeViewModel = getAppViewModelProvider().get(HomeViewModel.class);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //设置空白分割线
        DividerItemDecoration inputDividerItemDecoration = new DividerItemDecoration(getContext(),VERTICAL);
        inputDividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recycleview_blank_divider, null));
        DividerItemDecoration outputDividerItemDecoration = new DividerItemDecoration(getContext(),VERTICAL);
        outputDividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recycleview_blank_divider, null));

        RecyclerView inputRecyclerView = mFragmentMainBinding.fhInputDeviceList;
        inputRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),VERTICAL,false));
        DeviceInfoAdapter inputAdapter = new DeviceInfoAdapter(getContext());
        inputRecyclerView.addItemDecoration(inputDividerItemDecoration);
        inputRecyclerView.setAdapter(inputAdapter);

        RecyclerView outputRecyclerView = mFragmentMainBinding.fhOutputDeviceList;
        outputRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),VERTICAL,false));
        DeviceInfoAdapter outputAdapter = new DeviceInfoAdapter(getContext());
        outputRecyclerView.addItemDecoration(outputDividerItemDecoration);
        outputRecyclerView.setAdapter(outputAdapter);




        mHomeViewModel.inputDeviceLiveData.observe(getViewLifecycleOwner(), new Observer<List<DeviceInfo>>() {
            @Override
            public void onChanged(List<DeviceInfo> inputList) {
                Log.d(TAG, "onChanged: inputList"+inputAdapter);
                inputAdapter.updateList(inputList);
            }
        });
        mHomeViewModel.outputDeviceLiveData.observe(getViewLifecycleOwner(), new Observer<List<DeviceInfo>>() {
            @Override
            public void onChanged(List<DeviceInfo> outputList) {
                Log.d(TAG, "onChanged: outputList"+outputAdapter);
                outputAdapter.updateList(outputList);
            }
        });
        mHomeViewModel.audioInfo.observe(getViewLifecycleOwner(), new Observer<AudioInfo>() {
            @Override
            public void onChanged(AudioInfo audioInfo) {

            }
        });

        mHomeViewModel.refresh.setValue(true);
    }

    private static class DeviceInfoAdapter extends BaseRecycleViewAdapter<DeviceInfo, ItemDeviceInfoViewBinding> {
        private String TAG = "DeviceInfoAdapter";

        DeviceInfoAdapter(Context context) {
            super(context);
        }

        @Override
        protected boolean areContentsTheSame(DeviceInfo oldItem, DeviceInfo newItem) {
            return oldItem.getName().equals(newItem.getName());
        }

        @Override
        protected boolean areItemsTheSame(DeviceInfo oldItem, DeviceInfo newItem) {
            return  oldItem.getId()==newItem.getId();
        }


        @Override
        protected int getItemLayoutResId() {
            return R.layout.item_device_info_view;
        }

        @Override
        protected void onBindItem(ItemDeviceInfoViewBinding itemBinding, DeviceInfo deviceInfo) {
            Log.d(TAG, "onBindItem: "+this+deviceInfo);
            itemBinding.setDeviceInfo(deviceInfo);
        }


    }
}

