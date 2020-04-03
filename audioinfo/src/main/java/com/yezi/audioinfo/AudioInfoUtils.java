package com.yezi.audioinfo;

import android.media.AudioAttributes;
import android.media.AudioManager;

/**
 * @author : yezi
 * @date : 2020/4/2 9:06
 * desc   :
 * version: 1.0
 */
public class AudioInfoUtils {
    /**
     *     public static final int AUDIOFOCUS_GAIN = 1;
     *     public static final int AUDIOFOCUS_GAIN_TRANSIENT = 2;
     *     public static final int AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE = 4;
     *     public static final int AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK = 3;
     *     public static final int AUDIOFOCUS_LOSS = -1;
     *     public static final int AUDIOFOCUS_LOSS_TRANSIENT = -2;
     *     public static final int AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK = -3;
     *     public static final int AUDIOFOCUS_NONE = 0;
     *     public static final int AUDIOFOCUS_REQUEST_DELAYED = 2;
     *     public static final int AUDIOFOCUS_REQUEST_FAILED = 0;
     *     public static final int AUDIOFOCUS_REQUEST_GRANTED = 1;
     *
     * @param audioFocusState
     * @return
     */
    public static String focusIdToInfo(int audioFocusState){
        switch (audioFocusState){
            case AudioManager.AUDIOFOCUS_GAIN:
                return "AUDIOFOCUS_GAIN";
            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                return "AUDIOFOCUS_GAIN_TRANSIENT";
            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE:
                return "AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE";
            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                return "AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK";
            case AudioManager.AUDIOFOCUS_LOSS:
                return "AUDIOFOCUS_LOSS";
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                return "AUDIOFOCUS_LOSS_TRANSIENT";
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                return "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK";
            default:
                return "AUDIOFOCUS_NONE";
        }
    }

    /**
     *      AOSP
     *     ** Used to identify the default audio stream volume *
     *     public static final int STREAM_DEFAULT = -1;
     *     ** Used to identify the volume of audio streams for phone calls *
     *     public static final int STREAM_VOICE_CALL = 0;
     *     ** Used to identify the volume of audio streams for system sounds *
     *     public static final int STREAM_SYSTEM = 1;
     *     ** Used to identify the volume of audio streams for the phone ring and message alerts *
     *     public static final int STREAM_RING = 2;
     *     ** Used to identify the volume of audio streams for music playback *
     *     public static final int STREAM_MUSIC = 3;
     *     ** Used to identify the volume of audio streams for alarms *
     *     public static final int STREAM_ALARM = 4;
     *     ** Used to identify the volume of audio streams for notifications *
     *     public static final int STREAM_NOTIFICATION = 5;
     *     ** Used to identify the volume of audio streams for phone calls when connected on bluetooth *
     *     public static final int STREAM_BLUETOOTH_SCO = 6;
     *     ** Used to identify the volume of audio streams for enforced system sounds in certain
     *      * countries (e.g camera in Japan) *
     *     *@UnsupportedAppUsage
     *     public static final int STREAM_SYSTEM_ENFORCED = 7;
     *     ** Used to identify the volume of audio streams for DTMF tones *
     *     public static final int STREAM_DTMF = 8;
     *
     *     public static final int STREAM_TTS = 9;
     *     ** Used to identify the volume of audio streams for accessibility prompts *
     *     public static final int STREAM_ACCESSIBILITY = 10;
     *
     * @param stream
     * @return
     */
    public static String streamIdToInfo(int stream){
        switch (stream){
            case -1:
                return "STREAM_DEFAULT";
            case AudioManager.STREAM_VOICE_CALL:
                return "STREAM_VOICE_CALL";
            case AudioManager.STREAM_SYSTEM:
                return "STREAM_SYSTEM";
            case AudioManager.STREAM_RING:
                return "STREAM_RING";
            case AudioManager.STREAM_MUSIC:
                return "STREAM_MUSIC";
            case AudioManager.STREAM_ALARM:
                return "STREAM_ALARM";
            case AudioManager.STREAM_NOTIFICATION:
                return "STREAM_NOTIFICATION";
            case 6:
                return "STREAM_BLUETOOTH_SCO";
            case 7:
                return "STREAM_SYSTEM_ENFORCED";
            case AudioManager.STREAM_DTMF:
                return "STREAM_DTMF";
            case 9:
                return "STREAM_TTS";
            case AudioManager.STREAM_ACCESSIBILITY:
                return "STREAM_ACCESSIBILITY";
            default:
                return "STREAM_"+stream;
        }
    }

    public static final String[] STREAM_NAMES = new String[] {
            "STREAM_VOICE_CALL",
            "STREAM_SYSTEM",
            "STREAM_RING",
            "STREAM_MUSIC",
            "STREAM_ALARM",
            "STREAM_NOTIFICATION",
            "STREAM_BLUETOOTH_SCO",
            "STREAM_SYSTEM_ENFORCED",
            "STREAM_DTMF",
            "STREAM_TTS",
            "STREAM_ACCESSIBILITY"
    };


    /**
     *      * Usage value to use when the usage is unknown.
     *     public final static int USAGE_UNKNOWN = 0;
     *      * Usage value to use when the usage is media, such as music, or movie
     *      * soundtracks.
     *     public final static int USAGE_MEDIA = 1;
     *      * Usage value to use when the usage is voice communications, such as telephony
     *      * or VoIP.
     *     public final static int USAGE_VOICE_COMMUNICATION = 2;
     *      * Usage value to use when the usage is in-call signalling, such as with
     *      * a "busy" beep, or DTMF tones.
     *     public final static int USAGE_VOICE_COMMUNICATION_SIGNALLING = 3;
     *      * Usage value to use when the usage is an alarm (e.g. wake-up alarm).
     *     public final static int USAGE_ALARM = 4;
     *      * Usage value to use when the usage is notification. See other
     *      * notification usages for more specialized uses.
     *     public final static int USAGE_NOTIFICATION = 5;
     *      * Usage value to use when the usage is telephony ringtone.
     *     public final static int USAGE_NOTIFICATION_RINGTONE = 6;
     *      * Usage value to use when the usage is a request to enter/end a
     *      * communication, such as a VoIP communication or video-conference.
     *     public final static int USAGE_NOTIFICATION_COMMUNICATION_REQUEST = 7;
     *      * Usage value to use when the usage is notification for an "instant"
     *      * communication such as a chat, or SMS.
     *     public final static int USAGE_NOTIFICATION_COMMUNICATION_INSTANT = 8;
     *      * Usage value to use when the usage is notification for a
     *      * non-immediate type of communication such as e-mail.
     *     public final static int USAGE_NOTIFICATION_COMMUNICATION_DELAYED = 9;
     *      * Usage value to use when the usage is to attract the user's attention,
     *      * such as a reminder or low battery warning.
     *     public final static int USAGE_NOTIFICATION_EVENT = 10;
     *      * Usage value to use when the usage is for accessibility, such as with
     *      * a screen reader.
     *     public final static int USAGE_ASSISTANCE_ACCESSIBILITY = 11;
     *      * Usage value to use when the usage is driving or navigation directions.
     *     public final static int USAGE_ASSISTANCE_NAVIGATION_GUIDANCE = 12;
     *      * Usage value to use when the usage is sonification, such as  with user
     *      * interface sounds.
     *     public final static int USAGE_ASSISTANCE_SONIFICATION = 13;
     *      * Usage value to use when the usage is for game audio.
     *     public final static int USAGE_GAME = 14;
     *      * @hide
     *      * Usage value to use when feeding audio to the platform and replacing "traditional" audio
     *      * source, such as audio capture devices.
     *     public final static int USAGE_VIRTUAL_SOURCE = 15;
     *      * Usage value to use for audio responses to user queries, audio instructions or help
     *      * utterances.
     *     public final static int USAGE_ASSISTANT = 16;
     *      * Usage value to use when the usage is for car engine
     *     public final static int USAGE_CAR_ENGINE = 17;
     *
     * @param usage
     * @return
     */
    public static String usageIdToInfo(int usage){
        switch (usage){
            case AudioAttributes.USAGE_UNKNOWN:
                return "USAGE_UNKNOWN";
            case AudioAttributes.USAGE_MEDIA:
                return "USAGE_MEDIA";
            case AudioAttributes.USAGE_VOICE_COMMUNICATION:
                return "USAGE_VOICE_COMMUNICATION";
            case AudioAttributes.USAGE_VOICE_COMMUNICATION_SIGNALLING:
                return "USAGE_VOICE_COMMUNICATION_SIGNALLING";
            case AudioAttributes.USAGE_ALARM:
                return "USAGE_ALARM";
            case AudioAttributes.USAGE_NOTIFICATION:
                return "USAGE_NOTIFICATION";
            case AudioAttributes.USAGE_NOTIFICATION_RINGTONE:
                return "USAGE_NOTIFICATION_RINGTONE";
            case AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_REQUEST:
                return "USAGE_NOTIFICATION_COMMUNICATION_REQUEST";
            case AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT:
                return "USAGE_NOTIFICATION_COMMUNICATION_INSTANT";
            case AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_DELAYED:
                return "USAGE_NOTIFICATION_COMMUNICATION_DELAYED";
            case AudioAttributes.USAGE_NOTIFICATION_EVENT:
                return "USAGE_NOTIFICATION_EVENT";
            case AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY:
                return "USAGE_ASSISTANCE_ACCESSIBILITY";
            case AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE:
                return "USAGE_ASSISTANCE_NAVIGATION_GUIDANCE";
            case AudioAttributes.USAGE_ASSISTANCE_SONIFICATION:
                return "USAGE_ASSISTANCE_SONIFICATION";
            case AudioAttributes.USAGE_GAME:
                return "USAGE_GAME";
            case 15:
                return "USAGE_VIRTUAL_SOURCE";
            case AudioAttributes.USAGE_ASSISTANT:
                return "USAGE_ASSISTANT";
            //add in gwm car
            case 20:
                return "USAGE_CAR_TTS";
            //add in gwm car
            case 21:
                return "USAGE_CAR_ENGINE";
            default:
                return "USAGE_"+usage;
        }
    }

    /**
     *         USAGE_TO_CONTEXT.put(AudioAttributes.USAGE_UNKNOWN, ContextNumber.MUSIC);
     *         USAGE_TO_CONTEXT.put(AudioAttributes.USAGE_MEDIA, ContextNumber.MUSIC);
     *         USAGE_TO_CONTEXT.put(AudioAttributes.USAGE_VOICE_COMMUNICATION, ContextNumber.CALL);
     *         USAGE_TO_CONTEXT.put(AudioAttributes.USAGE_VOICE_COMMUNICATION_SIGNALLING,
     *                 ContextNumber.CALL);
     *         USAGE_TO_CONTEXT.put(AudioAttributes.USAGE_ALARM, ContextNumber.ALARM);
     *         USAGE_TO_CONTEXT.put(AudioAttributes.USAGE_NOTIFICATION, ContextNumber.NOTIFICATION);
     *         USAGE_TO_CONTEXT.put(AudioAttributes.USAGE_NOTIFICATION_RINGTONE, ContextNumber.CALL_RING);
     *         USAGE_TO_CONTEXT.put(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_REQUEST,
     *                 ContextNumber.NOTIFICATION);
     *         USAGE_TO_CONTEXT.put(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT,
     *                 ContextNumber.NOTIFICATION);
     *         USAGE_TO_CONTEXT.put(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_DELAYED,
     *                 ContextNumber.NOTIFICATION);
     *         USAGE_TO_CONTEXT.put(AudioAttributes.USAGE_NOTIFICATION_EVENT, ContextNumber.NOTIFICATION);
     *         USAGE_TO_CONTEXT.put(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY,
     *                 ContextNumber.VOICE_COMMAND);
     *         USAGE_TO_CONTEXT.put(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE,
     *                 ContextNumber.NAVIGATION);
     *         USAGE_TO_CONTEXT.put(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION,
     *                 ContextNumber.SYSTEM_SOUND);
     *         USAGE_TO_CONTEXT.put(AudioAttributes.USAGE_GAME, ContextNumber.MUSIC);
     *         USAGE_TO_CONTEXT.put(AudioAttributes.USAGE_VIRTUAL_SOURCE, ContextNumber.INVALID);
     *         USAGE_TO_CONTEXT.put(AudioAttributes.USAGE_ASSISTANT, ContextNumber.VOICE_COMMAND);
     *
     * @param usage
     * @return
     */
    public static String usageToCarContextInfo(int usage){
        switch (usage){
            case AudioAttributes.USAGE_UNKNOWN:
            case AudioAttributes.USAGE_MEDIA:
            case AudioAttributes.USAGE_GAME:
                return "ContextNumber.MUSIC";
            case AudioAttributes.USAGE_NOTIFICATION:
            case AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_REQUEST:
            case AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT:
            case AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_DELAYED:
            case AudioAttributes.USAGE_NOTIFICATION_EVENT:
                return "ContextNumber.NOTIFICATION";
            case AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE:
                return "ContextNumber.NAVIGATION";
            case AudioAttributes.USAGE_ASSISTANT:
            case AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY:
                return "ContextNumber.VOICE_COMMAND";
            case 15:
                return "ContextNumber.INVALID";
            case AudioAttributes.USAGE_NOTIFICATION_RINGTONE:
                return "ContextNumber.CALL_RING";
            case AudioAttributes.USAGE_ALARM:
                return "ContextNumber.ALARM";
            case AudioAttributes.USAGE_VOICE_COMMUNICATION:
            case AudioAttributes.USAGE_VOICE_COMMUNICATION_SIGNALLING:
                return "ContextNumber.CALL";
            case AudioAttributes.USAGE_ASSISTANCE_SONIFICATION:
                return "ContextNumber.SYSTEM_SOUND";
            default:
                return "unKnownUsageContext";
        }
    }
}
