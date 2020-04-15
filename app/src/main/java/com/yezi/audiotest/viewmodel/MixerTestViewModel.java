package com.yezi.audiotest.viewmodel;

import android.app.Application;
import android.media.AudioAttributes;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.yezi.audiotest.bean.LocalPlayerInfo;
import com.yezi.audiotest.bean.MockCallInfo;
import com.yezi.audiotest.bean.PlayerControl;
import com.yezi.audiotest.model.MixerManagerModel;

import java.util.List;

/**
 * @author : yezi
 * @date : 2020/3/28 15:51
 * desc   :
 * version: 1.0
 */
public class MixerTestViewModel extends BaseViewModel<MixerManagerModel> {
    static

    private final String TAG = "MixerTestViewModel";
    private MutableLiveData<List<LocalPlayerInfo>> mPlayersLiveData = new MutableLiveData<>();
    private MutableLiveData<MockCallInfo> mCallInfoUpdate = new MutableLiveData<>();
    private MutableLiveData<AudioAttributes> mCommand = new MutableLiveData<>();
    private MutableLiveData<PlayerControl> mControl = new MutableLiveData<>();
    private MutableLiveData<Boolean> mRelease = new MutableLiveData<>();
    private MutableLiveData<MockCallInfo> mCallCommand = new MutableLiveData<>();

    public MixerTestViewModel(Application application){
        super(application);
    }

    @Override
    public void initSource() {
        Log.d(TAG, "initSource: ");
        MixerManagerModel player = MixerManagerModel.getInstance(getApplication());
        player.init();
        player.observerCommand(mCommand);
        player.observerControl(mControl);
        player.observerRelease(mRelease);
        player.observerCall(mCallCommand);
        player.setPlayersLiveData(mPlayersLiveData);
        player.setCallInfoLiveData(mCallInfoUpdate);
        Log.d(TAG, "initSource: "+mPlayersLiveData);
    }

    @Override
    protected void onCleared() {
        Log.d(TAG, "onCleared: ");
        mRelease.setValue(true);
        super.onCleared();
    }

    public MutableLiveData<List<LocalPlayerInfo>> getPlayersLiveData() {
        Log.d(TAG, "getPlayersLiveData: "+this+mPlayersLiveData);
        return mPlayersLiveData;
    }

    public MutableLiveData<AudioAttributes> getCommandLiveData(){
        return mCommand;
    }

    public MutableLiveData<PlayerControl> getControlLiveData(){
        return mControl;
    }

    public MutableLiveData<MockCallInfo> getCallCmdLiveData(){
        return mCallCommand;
    }

    public MutableLiveData<MockCallInfo> getCallInfoLiveData(){
        return mCallInfoUpdate;
    }


}
