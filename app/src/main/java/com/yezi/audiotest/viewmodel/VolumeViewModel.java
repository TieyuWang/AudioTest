package com.yezi.audiotest.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yezi.audiotest.bean.VolumeInfo;
import com.yezi.audiotest.model.BaseModel;
import com.yezi.audiotest.model.VolumeModel;

import java.util.List;

/**
 * @author : yezi
 * @date : 2020/4/15 17:54
 * desc   :
 * version: 1.0
 */
public class VolumeViewModel extends BaseViewModel<BaseModel> {
    public VolumeViewModel(@NonNull Application application) {
        super(application);
    }

    private MutableLiveData<List<VolumeInfo>> mVolumeList = new MutableLiveData<>();
    private MutableLiveData<VolumeInfo> mVolumeChangeCmd = new MutableLiveData<>();

    @Override
    public void initSource() {
        VolumeModel volumeModel = VolumeModel.getInstance(getApplication());
        volumeModel.observerCmd(mVolumeChangeCmd);
        volumeModel.setVolumeListLiveDate(mVolumeList);
        volumeModel.init();
    }

    public MutableLiveData<List<VolumeInfo>> getVolumeListLiveData() {
        return mVolumeList;
    }

    public MutableLiveData<VolumeInfo> getVolumeChangeCmd() {
        return mVolumeChangeCmd;
    }
}
