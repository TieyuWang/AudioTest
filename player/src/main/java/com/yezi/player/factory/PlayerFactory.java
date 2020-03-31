package com.yezi.player.factory;

import android.app.Application;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.util.Log;

import androidx.annotation.NonNull;

import com.yezi.player.bean.PlayerInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : yezi
 * @date : 2020/3/30 16:21
 * desc   :
 * version: 1.0
 */
public class PlayerFactory {
    private final String TAG = "PlayerFactory";
    private static PlayerFactory mInstance;
    List<AssetFileDescriptor> mSourceList;

    private PlayerFactory(Application application){
        mSourceList = new ArrayList<>();
        try {
            mSourceList.add(application.getAssets().openFd("bgm1.mp3"));
            mSourceList.add(application.getAssets().openFd("bgm2.mp3"));
            mSourceList.add(application.getAssets().openFd("bgm3.mp3"));
            mSourceList.add(application.getAssets().openFd("music_new_year.mp3"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PlayerFactory getInstance(@NonNull Application application){
        if(mInstance == null){
            synchronized (PlayerFactory.class){
                if(mInstance == null){
                    mInstance = new PlayerFactory(application);
                }
            }
        }
        return mInstance;
    }

    public IPlayerController createPlayer(PlayerInfo info) {
        Log.d(TAG, "createPlayer: "+info);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setLegacyStreamType(info.getStream())
                .setUsage(info.getUsage())
                .build();
        AssetFileDescriptor pathFD = getMediaSource(info);
        return new Player(audioAttributes,pathFD);
    }

    private AssetFileDescriptor getMediaSource(PlayerInfo info) {
        return mSourceList.get(0);
    }
}
