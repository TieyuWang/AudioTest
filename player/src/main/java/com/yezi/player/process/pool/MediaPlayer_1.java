package com.yezi.player.process.pool;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.yezi.player.process.BaseMediaPlayerService;

/**
 * @author : yezi
 * @date : 2020/3/30 16:00
 * desc   :
 * version: 1.0
 */
public class MediaPlayer_1 extends BaseMediaPlayerService {
    private final String TAG = "Mediaplayer_1";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        return mMediaPlayer;
    }

}
