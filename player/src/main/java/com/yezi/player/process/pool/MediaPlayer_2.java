package com.yezi.player.process.pool;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yezi.player.process.BaseMediaPlayerService;

/**
 * @author : yezi
 * @date : 2020/3/30 16:02
 * desc   :
 * version: 1.0
 */
public class MediaPlayer_2 extends BaseMediaPlayerService {

    @NonNull
    @Override
    protected String getTag() {
        return "MediaPlayer_2";
    }
}
