package com.yezi.audiotest.fragment;



import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yezi.audiotest.R;
import com.yezi.audiotest.adpter.BaseRecycleViewAdapter;
import com.yezi.audiotest.bean.VolumeInfo;
import com.yezi.audiotest.databinding.FragmentVolumeTestBinding;
import com.yezi.audiotest.databinding.ItemVolumeControllerBinding;
import com.yezi.audiotest.viewmodel.VolumeViewModel;

import java.util.List;


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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = mFragmentBinding.volumeTestList;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        VolumeListAdapter adapter = new VolumeListAdapter(getContext());
        recyclerView.setAdapter(adapter);

        mFragmentViewModel.getVolumeListLiveData().observe(getViewLifecycleOwner(), new Observer<List<VolumeInfo>>() {
            @Override
            public void onChanged(List<VolumeInfo> volumeInfoList) {
                adapter.updateList(volumeInfoList);
                mFragmentBinding.executePendingBindings();
            }
        });
    }

    class VolumeListAdapter extends BaseRecycleViewAdapter<VolumeInfo,ItemVolumeControllerBinding> {
        public VolumeListAdapter(Context context) {
            super(context);
        }

        @Override
        protected boolean areContentsTheSame(VolumeInfo oldItem, VolumeInfo newItem) {
            return oldItem.getVolumeGroupId() == newItem.getVolumeGroupId()
                    && oldItem.getCurrent() == newItem.getCurrent();
        }

        @Override
        protected boolean areItemsTheSame(VolumeInfo oldItem, VolumeInfo newItem) {
            return oldItem.getVolumeGroupId() == newItem.getVolumeGroupId();
        }

        @Override
        protected int getItemLayoutResId() {
            return R.layout.item_volume_controller;
        }

        @Override
        protected void onBindItem(ItemVolumeControllerBinding itemBinding, VolumeInfo volumeInfo) {
            itemBinding.setVolumeInfo(volumeInfo);
            itemBinding.volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(fromUser){
                        VolumeInfo volumeInfoCmd = volumeInfo.deepCopy();
                        volumeInfoCmd.setCurrent(progress);
                        mFragmentViewModel.getVolumeChangeCmd().setValue(volumeInfoCmd);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
    }
}
