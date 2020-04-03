package com.yezi.audiotest.source;

import android.annotation.SuppressLint;
import android.app.Application;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.yezi.audiotest.bean.LocalPlayerInfo;
import com.yezi.audiotest.bean.PlayerAttributes;
import com.yezi.audiotest.bean.PlayerControl;
import com.yezi.player.bean.PlayerInfo;
import com.yezi.player.PlayerManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * @author : yezi
 * @date : 2020/3/28 16:16
 * desc   :
 * version: 1.0
 */
public class PlayerManagerProxy {
    private final String TAG = "PlayerManager_proxy";
    private static PlayerManagerProxy mInstance;
    private MutableLiveData<List<LocalPlayerInfo>> mPlayersLiveData;
    private List<LocalPlayerInfo> mPlayerList;
    private PlayerManager mPlayerManager;

    private final static int MSG_UPDATE_PLAYER_LIST = 0x201;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == MSG_UPDATE_PLAYER_LIST){
                mPlayersLiveData.setValue(mPlayerList);
            }
/*            switch (msg.what){
                case MSG_UPDATE_PLAYER_LIST:
                    try {
                        mPlayersLiveData.setValue(mPlayerList);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                default:
            }*/
        }
    };

    private void updateList(int mediaSession,int pos,List<PlayerInfo> list) {
        Log.d(TAG, "updateList: "+mediaSession+"  "+pos+" "+ list.size());
        if(mPlayerList == null){
            mPlayerList = new ArrayList<>();
        }
                   
        if (mPlayerList.size() == list.size()) {
            if (pos >= 0 && pos < mPlayerList.size()) {
                PlayerInfo update = list.get(pos);
                LocalPlayerInfo posLocalPlayerInfo = mPlayerList.get(pos);
                if (isAssociatedInfo(posLocalPlayerInfo, update)) {
                    posLocalPlayerInfo.updatePlayStateInfo(update);
                    posLocalPlayerInfo.updateAudioFocusInfo(update);
                    Log.v(TAG, "updateList: size same and find in local info by list pos");
                    setListLiveDateInUIThread();
                    mHandler.sendEmptyMessage(MSG_UPDATE_PLAYER_LIST);
                    return;
                } else {
                    for (LocalPlayerInfo localInfo : mPlayerList) {
                        if (isAssociatedInfo(localInfo, update)) {
                            localInfo.updatePlayStateInfo(update);
                            localInfo.updateAudioFocusInfo(update);
                            Log.v(TAG, "updateList: size same and find in local info by for each");
                            mHandler.sendEmptyMessage(MSG_UPDATE_PLAYER_LIST);
                            return;
                        }
                    }
                }
            }
        } else if (mPlayerList.size() < list.size()) {
            PlayerInfo update = list.get(pos);
            mPlayerList.add(new LocalPlayerInfo(update));
            Log.v(TAG, "updateList: add local info");
            mHandler.sendEmptyMessage(MSG_UPDATE_PLAYER_LIST);
        } else {
            for(int index = 0;index<mPlayerList.size();index++){
                LocalPlayerInfo localPlayerInfo = mPlayerList.get(index);
                if(localPlayerInfo.getMediaSessionId() == mediaSession){
                    mPlayerList.remove(index);
                    Log.v(TAG, "updateList: remove local info by mediaSession = "
                            +mediaSession+" for each");
                    mHandler.sendEmptyMessage(MSG_UPDATE_PLAYER_LIST);
                }
            }
        }
        
    }

    private void setListLiveDateInUIThread() {
        if(mHandler.hasMessages(MSG_UPDATE_PLAYER_LIST)) {
            Log.d(TAG, "setListLiveDateInUIThread: remove old update cmd");
            mHandler.removeMessages(MSG_UPDATE_PLAYER_LIST);
        }
        mHandler.sendEmptyMessage(MSG_UPDATE_PLAYER_LIST);
    }

    private boolean isAssociatedInfo(LocalPlayerInfo localPlayerInfo,PlayerInfo playerInfo){
        return localPlayerInfo.getMediaSessionId() == playerInfo.getMediaSessionId()
                && localPlayerInfo.getPid() == playerInfo.getPid();
    }

    private PlayerManagerProxy(Application application){
        mPlayerManager = PlayerManager.getInstance(application);
    }

    public static PlayerManagerProxy getInstance(Application application){
        if(mInstance == null){
            synchronized (PlayerManagerProxy.class){
                if(mInstance == null){
                    mInstance = new PlayerManagerProxy(application);
                }
            }
        }
        return mInstance;
    }

    public void init(){
        mPlayerManager.registerPlayerListListener(new PlayerManager.PlayerManagerCallback() {
            @Override
            public void onPlayerInfoUpdate(int mediaSessionId, int listPos,ArrayList<PlayerInfo> list) {
                Log.d(TAG, "onPlayerInfoUpdate: mediaSessionId "+mediaSessionId
                        +" list size "+list.size());
                /**
                 * TODO：setValue fatal
                 * 问题：java.lang.IllegalStateException: Cannot invoke setValue on a background thread
                 * 原因：子线程使用liveData setValue
                 * 解决问题 在PlayerManagerProxy的监听里使用handler回到主线程
                 *
                 */
             //   mPlayersLiveData.setValue(mPlayerList);
                updateList(mediaSessionId,listPos,list);
            }
        });
    }

    public void setPlayersLiveData(MutableLiveData<List<LocalPlayerInfo>> players) {
        Log.d(TAG, "setPlayersLiveData: ");
        mPlayersLiveData = players;
        mPlayerList = new ArrayList<>();
    }

    public void observerCommand(MutableLiveData<PlayerAttributes> addPlayerCommand) {
        addPlayerCommand.observeForever(new Observer<PlayerAttributes>() {
            @Override
            public void onChanged(PlayerAttributes playerAttributes) {
                Log.d(TAG, "addPlayerCommand: "+playerAttributes);
                PlayerInfo info = new PlayerInfo("test", playerAttributes.stream, playerAttributes.usage);
                int mediaSessionId = mPlayerManager.addPlayer(info);

                if(mediaSessionId != -1){
                    Log.d(TAG, "add player success mediaSessionId " + mediaSessionId);
                }else {
                    Log.w(TAG, "add player filed !");
                }

            }
        });
    }

    public void observerControl(MutableLiveData<PlayerControl> control) {
        control.observeForever(new Observer<PlayerControl>() {
            @Override
            public void onChanged(PlayerControl playerControl) {
                Log.d(TAG, "playerControl: mediaSessionId = "+playerControl.sessionId
                        +" cmd = "+playerControl.cmd);
                mPlayerManager.playerControl(playerControl.sessionId,playerControl.cmd);
            }
        });
    }
}
