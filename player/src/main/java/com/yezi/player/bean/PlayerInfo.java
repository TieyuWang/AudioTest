package com.yezi.player.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author : yezi
 * @date : 2020/3/30 10:14
 * desc   :
 * version: 1.0
 */
public class PlayerInfo implements Parcelable {

    int mediaSessionId;
    int pid;
    String name;
    int stream;
    int usage;
    int playerState;

    public PlayerInfo(String name, int stream, int usage) {
        this.name = name;
        this.stream = stream;
        this.usage = usage;
    }

    protected PlayerInfo(Parcel in) {
        mediaSessionId = in.readInt();
        pid = in.readInt();
        name = in.readString();
        stream = in.readInt();
        usage = in.readInt();
        playerState = in.readInt();
    }

    public static final Creator<PlayerInfo> CREATOR = new Creator<PlayerInfo>() {
        @Override
        public PlayerInfo createFromParcel(Parcel in) {
            return new PlayerInfo(in);
        }

        @Override
        public PlayerInfo[] newArray(int size) {
            return new PlayerInfo[size];
        }
    };

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStream() {
        return stream;
    }

    public void setStream(int stream) {
        this.stream = stream;
    }

    public int getUsage() {
        return usage;
    }

    public void setUsage(int usage) {
        this.usage = usage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mediaSessionId);
        parcel.writeInt(pid);
        parcel.writeString(name);
        parcel.writeInt(stream);
        parcel.writeInt(usage);
        parcel.writeInt(playerState);
    }

    public void readFromParcel(Parcel in) {
        mediaSessionId = in.readInt();
        pid = in.readInt();
        name = in.readString();
        stream = in.readInt();
        usage = in.readInt();
        playerState = in.readInt();
    }

    public void setPlayerState(int state) {
        this.playerState = state;
    }
    public int getPlayerState() {
        return playerState;
    }
    public int getMediaSessionId() {
        return mediaSessionId;
    }

    public void setMediaSessionId(int mediaSessionId) {
        this.mediaSessionId = mediaSessionId;
    }
}
