package com.yezi.player.factory;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.media.AudioAttributes.USAGE_ASSISTANCE_SONIFICATION;
import static android.media.AudioAttributes.USAGE_NOTIFICATION;
import static android.media.AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_DELAYED;
import static android.media.AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT;
import static android.media.AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_REQUEST;
import static android.media.AudioAttributes.USAGE_NOTIFICATION_EVENT;
import static android.media.AudioAttributes.USAGE_NOTIFICATION_RINGTONE;
import static android.media.AudioAttributes.USAGE_VOICE_COMMUNICATION;
import static android.media.AudioAttributes.USAGE_VOICE_COMMUNICATION_SIGNALLING;

/**
 * @author : yezi
 * @date : 2020/3/30 16:21
 * desc   :
 * version: 1.0
 */
public class PlayerFactory {
    private static final String TAG = "PlayerFactory";
    private static PlayerFactory mInstance;
    private static List<AssetFileDescriptor> mMusicList = new ArrayList<>();
    private static List<AssetFileDescriptor> mNotificationList = new ArrayList<>();
    private static List<AssetFileDescriptor> mNavList = new ArrayList<>();
    private static List<AssetFileDescriptor> mVoiceList = new ArrayList<>();
    private static List<AssetFileDescriptor> mTtsList = new ArrayList<>();
    private static List<AssetFileDescriptor> mSystemList = new ArrayList<>();
    private static List<AssetFileDescriptor> mRingToneList = new ArrayList<>();
    private static List<AssetFileDescriptor> mAlarmList = new ArrayList<>();
    private static List<AssetFileDescriptor> mCallList = new ArrayList<>();
    private static volatile boolean hasInitResource = false;
    private static volatile Object resLock = new Object();
    private AudioManager audioManager;

    private PlayerFactory(Application application){
        initResource(application);
        audioManager = (AudioManager) application.getSystemService(Context.AUDIO_SERVICE);
    }

    private static void initResource(Application application) {
        Log.d(TAG, "initResource: "+hasInitResource);
        synchronized (resLock) {
            if (hasInitResource) {
                Log.w(TAG, "initResource: media resources already load");
                return;
            }
            AssetManager assetManager = application.getAssets();
            mMusicList = new ArrayList<>();
            mNotificationList = new ArrayList<>();
            try {
                mMusicList.add(assetManager.openFd("Music_bgm1.mp3"));
                mMusicList.add(assetManager.openFd("Music_bgm2.mp3"));
                mMusicList.add(assetManager.openFd("Music_bgm3.mp3"));
                mMusicList.add(assetManager.openFd("Music_new_year.mp3"));
                mNotificationList.add(assetManager.openFd("MSG_WaterDrop.wav"));
                mNotificationList.add(assetManager.openFd("MSG_DingDing.mp3"));
                mNavList.add(assetManager.openFd("Nav_TheNavigationStarted.mp3"));
                mVoiceList.add(assetManager.openFd("VR_ManReadsThePoem.wav"));
                mVoiceList.add(assetManager.openFd("VR_ManReadWord.wav"));
                mTtsList.add(assetManager.openFd("TTS_ChineseTts.wav"));
                mTtsList.add(assetManager.openFd("TTS_RobotTalk.mp3"));
                mRingToneList.add(assetManager.openFd("Ring_TraditionalBell.mp3"));
                mRingToneList.add(assetManager.openFd("Ring_RingTone.mp3"));
                mCallList.add(assetManager.openFd("Call_WomanTalk.mp3"));
                mAlarmList.add(assetManager.openFd("Alarm_Ling.wav"));
                mSystemList.add(assetManager.openFd("System_PopWinSound.ogg"));
                Log.d(TAG, "initResource: media resources load successful");
                hasInitResource = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    private CallPlayer mCallPlayer;

    public IPlayerController createPlayer(AudioAttributes audioAttributes) {
        Log.d(TAG, "createPlayer: "+audioAttributes);
        if(PlayerTypeHelper.isShortMedia(audioAttributes)){
            return new SoundPlayer();
        }
        if(PlayerTypeHelper.isCallMedia(audioAttributes)){
            if(mCallPlayer == null) {
                mCallPlayer = new CallPlayer(audioManager);
            }
            return mCallPlayer;
        }
        return new NormalPlayer();
    }


    private AssetFileDescriptor randomFromList(List<AssetFileDescriptor> list){
        if(list == null || list.size() == 0){
            Log.w(TAG, "randomFromList:res list has not init");
            return null;
        }
        Random random = new Random();
        int id = random.nextInt(list.size());
        Log.d(TAG, "randomFromList: id = "+id+" AFD = "+list.get(id)+" size = "+list.size());
        return list.get(id);
    }

    public AssetFileDescriptor getMediaSource(AudioAttributes audioAttributes) {
        switch (audioAttributes.getUsage()) {
            case USAGE_NOTIFICATION:
            case USAGE_NOTIFICATION_COMMUNICATION_REQUEST:
            case USAGE_NOTIFICATION_COMMUNICATION_INSTANT:
            case USAGE_NOTIFICATION_COMMUNICATION_DELAYED:
            case USAGE_NOTIFICATION_EVENT:
                return randomFromList(mNotificationList);
            case AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE:
                return randomFromList(mNavList);
            case AudioAttributes.USAGE_ASSISTANT:
                return randomFromList(mVoiceList);
            case 20:
                return randomFromList(mTtsList);
            case USAGE_NOTIFICATION_RINGTONE:
                return randomFromList(mRingToneList);
            case AudioAttributes.USAGE_ALARM:
                return randomFromList(mAlarmList);
            case USAGE_VOICE_COMMUNICATION:
            case USAGE_VOICE_COMMUNICATION_SIGNALLING:
                return randomFromList(mCallList);
            case AudioAttributes.USAGE_ASSISTANCE_SONIFICATION:
                return randomFromList(mSystemList);
            case AudioAttributes.USAGE_UNKNOWN:
            case AudioAttributes.USAGE_MEDIA:
            case AudioAttributes.USAGE_GAME:
                //USAGE_VIRTUAL_SOURCE
            case 15:
            default:
                return randomFromList(mMusicList);
        }
    }

    /**
     * @author : yezi
     * @date : 2020/4/8 17:13
     * desc   :
     * version: 1.0
     */
    public static class PlayerTypeHelper {

        public static boolean isCallMedia(AudioAttributes audioAttributes) {
            int usage = audioAttributes.getUsage();
            int stream = audioAttributes.getVolumeControlStream();
            switch (usage){
                case USAGE_VOICE_COMMUNICATION:
                case USAGE_VOICE_COMMUNICATION_SIGNALLING:
                case USAGE_NOTIFICATION_RINGTONE:
                    return true;
                default:
            }
            return stream == AudioManager.STREAM_VOICE_CALL || stream == AudioManager.STREAM_RING;
        }

        public static boolean isShortMedia(AudioAttributes audioAttributes){
            int usage = audioAttributes.getUsage();
            int stream = audioAttributes.getVolumeControlStream();
            switch (usage){
                case USAGE_NOTIFICATION:
                case USAGE_NOTIFICATION_COMMUNICATION_REQUEST:
                case USAGE_NOTIFICATION_COMMUNICATION_INSTANT:
                case USAGE_NOTIFICATION_COMMUNICATION_DELAYED:
                case USAGE_NOTIFICATION_EVENT:
                case USAGE_ASSISTANCE_SONIFICATION:
                    return true;
                default:
            }
            return stream == AudioManager.STREAM_NOTIFICATION || stream == AudioManager.STREAM_SYSTEM;
        }

    }
}
