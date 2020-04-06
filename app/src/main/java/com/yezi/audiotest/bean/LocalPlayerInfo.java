package com.yezi.audiotest.bean;

import android.media.AudioManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.yezi.audioinfo.AudioInfoUtils;
import com.yezi.player.bean.PlayerInfo;
import com.yezi.player.factory.IPlayerController;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author : yezi
 * @date : 2020/4/2 16:20
 * desc   :
 * version: 1.0
 */
public class LocalPlayerInfo implements Serializable,Parcelable,Cloneable {
    int mediaSessionId;
    int pid;
    String playerBaseInfo;
    String playStateInfo;
    String audioFocusInfo;
    boolean isPlaying;
    boolean hasAudioFocus;

    public LocalPlayerInfo(PlayerInfo info) {
        initBaseInfo(info);
        updatePlayStateInfo(info);
        updateAudioFocusInfo(info);
    }

    @NonNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

       @Override
    public String toString() {
        return "LocalPlayerInfo{" +
                "mediaSessionId=" + mediaSessionId +
                ", pid=" + pid +
                ", playerBaseInfo='" + playerBaseInfo + '\'' +
                ", playStateInfo='" + playStateInfo + '\'' +
                ", audioFocusInfo='" + audioFocusInfo + '\'' +
                ", isPlaying=" + isPlaying +
                ", hasAudioFocus=" + hasAudioFocus +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalPlayerInfo that = (LocalPlayerInfo) o;
        return mediaSessionId == that.mediaSessionId &&
                pid == that.pid &&
                isPlaying == that.isPlaying &&
                hasAudioFocus == that.hasAudioFocus &&
                Objects.equals(playerBaseInfo, that.playerBaseInfo) &&
                Objects.equals(playStateInfo, that.playStateInfo) &&
                Objects.equals(audioFocusInfo, that.audioFocusInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mediaSessionId, pid, playerBaseInfo, playStateInfo, audioFocusInfo, isPlaying, hasAudioFocus);
    }

    protected LocalPlayerInfo(Parcel in) {
        mediaSessionId = in.readInt();
        pid = in.readInt();
        playerBaseInfo = in.readString();
        playStateInfo = in.readString();
        audioFocusInfo = in.readString();
        isPlaying = in.readByte() != 0;
        hasAudioFocus = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mediaSessionId);
        dest.writeInt(pid);
        dest.writeString(playerBaseInfo);
        dest.writeString(playStateInfo);
        dest.writeString(audioFocusInfo);
        dest.writeByte((byte) (isPlaying ? 1 : 0));
        dest.writeByte((byte) (hasAudioFocus ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LocalPlayerInfo> CREATOR = new Creator<LocalPlayerInfo>() {
        @Override
        public LocalPlayerInfo createFromParcel(Parcel in) {
            return new LocalPlayerInfo(in);
        }

        @Override
        public LocalPlayerInfo[] newArray(int size) {
            return new LocalPlayerInfo[size];
        }
    };

    public void updateAudioFocusInfo(PlayerInfo info) {
        int focus = info.getAudioFocusState();
        if(focus>= AudioManager.AUDIOFOCUS_GAIN){
            hasAudioFocus = true;
        }else {
            hasAudioFocus = false;
        }
        audioFocusInfo = AudioInfoUtils.focusIdToInfo(focus);
    }


    public void updatePlayStateInfo(PlayerInfo info) {
        isPlaying = info.getPlayerState() == IPlayerController.PLAYER_PLAY;
        String state;
        switch (info.getPlayerState()){
            case IPlayerController.PLAYER_INIT:
                state = "PLAYER_INIT";
                break;
            case IPlayerController.PLAYER_PLAY:
                state = "PLAYER_PLAY";
                break;
            case IPlayerController.PLAYER_PAUSE:
                state = "PLAYER_PAUSE";
                break;
            case IPlayerController.PLAYER_STOP:
                state = "PLAYER_STOP";
                break;
            case IPlayerController.PLAYER_RELEASE:
                state = "PLAYER_RELEASE";
                break;
            default:
                state = "PLAYER_UNKNOWN";
                break;
        }
        playStateInfo = "state:"+state;
    }

    private void initBaseInfo(PlayerInfo info) {
        mediaSessionId = info.getMediaSessionId();
        pid = info.getPid();
        StringBuilder sb = new StringBuilder();
        sb.append("pid:");
        sb.append(pid);
        sb.append(" mediaSession:");
        sb.append(mediaSessionId);
        sb.append("\n");
        sb.append(AudioInfoUtils.streamIdToInfo(info.getStream()));
        sb.append("\n");
        sb.append(AudioInfoUtils.usageIdToInfo(info.getUsage()));
        playerBaseInfo = sb.toString();
    }

    public int getMediaSessionId() {
        return mediaSessionId;
    }

    public void setMediaSessionId(int mediaSessionId) {
        this.mediaSessionId = mediaSessionId;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getPlayStateInfo() {
        return playStateInfo;
    }

    public void setPlayStateInfo(String playStateInfo) {
        this.playStateInfo = playStateInfo;
    }

    public String getPlayerBaseInfo() {
        return playerBaseInfo;
    }

    public void setPlayerBaseInfo(String playerBaseInfo) {
        this.playerBaseInfo = playerBaseInfo;
    }

    public String getAudioFocusInfo() {
        return audioFocusInfo;
    }

    public void setAudioFocusInfo(String audioFocusInfo) {
        this.audioFocusInfo = audioFocusInfo;
    }

    /**
     * dataBinding命名方式 方便获取
     * @return
     */
    public boolean getIsPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean getHasAudioFocus() {
        return hasAudioFocus;
    }

    public void setHasAudioFocus(boolean hasAudioFocus) {
        this.hasAudioFocus = hasAudioFocus;
    }

    public void readFromParcel(Parcel in){
        mediaSessionId = in.readInt();
        pid = in.readInt();
        playerBaseInfo = in.readString();
        playStateInfo = in.readString();
        audioFocusInfo = in.readString();
        isPlaying = in.readByte() != 0;
        hasAudioFocus = in.readByte() != 0;
    }
}
