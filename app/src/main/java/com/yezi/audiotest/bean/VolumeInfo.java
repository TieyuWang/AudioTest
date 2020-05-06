package com.yezi.audiotest.bean;

import java.io.Serializable;

/**
 * @author : yezi
 * @date : 2020/5/5 8:52
 * desc   :
 * version: 1.0
 */
public class VolumeInfo implements Serializable {
    int volumeGroupId;
    int contextId;
    String contextName;
    String volumeName;
    int current;
    int min;
    int max;
    boolean isMute;
    boolean isMastMute;

    public VolumeInfo(int volumeGroupId){
        this.volumeGroupId = volumeGroupId;
    }

    public VolumeInfo deepCopy(){
        VolumeInfo copy = new VolumeInfo(volumeGroupId);
        copy.setCurrent(current);
        copy.setContextName(contextName);
        copy.setMax(max);
        copy.setMin(min);
        copy.setMastMute(isMastMute);
        copy.setMute(isMute);
        copy.setVolumeName(volumeName);
        copy.setContextId(contextId);
        return copy;
    }

    public int getVolumeGroupId() {
        return volumeGroupId;
    }

    public void setVolumeGroupId(int volumeGroupId) {
        this.volumeGroupId = volumeGroupId;
    }

    public int getContextId() {
        return contextId;
    }

    public void setContextId(int contextId) {
        this.contextId = contextId;
    }

    public String getContextName() {
        return contextName;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    public String getVolumeName() {
        return volumeName;
    }

    public void setVolumeName(String volumeName) {
        this.volumeName = volumeName;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public boolean isMute() {
        return isMute;
    }

    public void setMute(boolean mute) {
        isMute = mute;
    }

    public boolean isMastMute() {
        return isMastMute;
    }

    public void setMastMute(boolean mastMute) {
        isMastMute = mastMute;
    }

    @Override
    public String toString() {
        return "VolumeInfo{" +
                "volumeGroupId=" + volumeGroupId +
                ", contextId=" + contextId +
                ", contextName='" + contextName + '\'' +
                ", volumeName='" + volumeName + '\'' +
                ", current=" + current +
                ", min=" + min +
                ", max=" + max +
                ", isMute=" + isMute +
                ", isMastMute=" + isMastMute +
                '}';
    }
}
