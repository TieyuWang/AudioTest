package com.yezi.audioinfo;

import android.media.AudioDeviceInfo;
import android.os.Build;

import java.io.Serializable;
import java.util.Arrays;

import static android.media.AudioDeviceInfo.TYPE_AUX_LINE;
import static android.media.AudioDeviceInfo.TYPE_BLUETOOTH_A2DP;
import static android.media.AudioDeviceInfo.TYPE_BLUETOOTH_SCO;
import static android.media.AudioDeviceInfo.TYPE_BUILTIN_EARPIECE;
import static android.media.AudioDeviceInfo.TYPE_BUILTIN_SPEAKER;
import static android.media.AudioDeviceInfo.TYPE_BUS;
import static android.media.AudioDeviceInfo.TYPE_DOCK;
import static android.media.AudioDeviceInfo.TYPE_FM;
import static android.media.AudioDeviceInfo.TYPE_HDMI;
import static android.media.AudioDeviceInfo.TYPE_HDMI_ARC;
import static android.media.AudioDeviceInfo.TYPE_HEARING_AID;
import static android.media.AudioDeviceInfo.TYPE_IP;
import static android.media.AudioDeviceInfo.TYPE_LINE_ANALOG;
import static android.media.AudioDeviceInfo.TYPE_LINE_DIGITAL;
import static android.media.AudioDeviceInfo.TYPE_TELEPHONY;
import static android.media.AudioDeviceInfo.TYPE_USB_ACCESSORY;
import static android.media.AudioDeviceInfo.TYPE_USB_DEVICE;
import static android.media.AudioDeviceInfo.TYPE_USB_HEADSET;
import static android.media.AudioDeviceInfo.TYPE_WIRED_HEADPHONES;
import static android.media.AudioDeviceInfo.TYPE_WIRED_HEADSET;

/**
 * @author : yezi
 * @date : 2020/4/9 14:30
 * desc   :
 * version: 1.0
 */
public class DeviceInfo implements Serializable {
    private int id;
    private String name;
    private String type;
    private String rote;
    private String channelInfo;
    private String address;
    private String encodingInfo;
    private String sampleRateInfo;
    public DeviceInfo(AudioDeviceInfo audioDeviceInfo){
        id = audioDeviceInfo.getId();
        name = audioDeviceInfo.getProductName().toString();
        type = AudioInfoUtils.deviceTypeToInfo(audioDeviceInfo.getType());
        rote = audioDeviceInfo.isSink() ? " sink ":"";
        rote += audioDeviceInfo.isSource() ? " source ":"";
        channelInfo = generateChannelInfo(audioDeviceInfo);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            address = audioDeviceInfo.getAddress();
        }else{
            address = "";
        }
        encodingInfo =generateEncodingInfo(audioDeviceInfo);
        sampleRateInfo = "sampleRates="+Arrays.toString(audioDeviceInfo.getSampleRates());

    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", rote='" + rote + '\'' +
                ", channelInfo='" + channelInfo + '\'' +
                ", address='" + address + '\'' +
                ", encodingInfo='" + encodingInfo + '\'' +
                ", sampleRateInfo='" + sampleRateInfo + '\'' +
                '}';
    }

    private String generateEncodingInfo(AudioDeviceInfo audioDeviceInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("EncodingFormat:\n");
        for(int encoding : audioDeviceInfo.getEncodings()){
            sb.append(AudioInfoUtils.encodingTypeToInfo(encoding)).append(" ,\n");
        }
        return sb.toString();
    }

    private String generateChannelInfo(AudioDeviceInfo audioDeviceInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("channelCounts=").append(Arrays.toString(audioDeviceInfo.getChannelCounts()));
        sb.append("\nchannelMasks=\n");
        for(int mask : audioDeviceInfo.getChannelMasks()){
            sb.append(AudioInfoUtils.channelMaskToInfo(mask)).append(" ,\n");
        }
        return sb.toString();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getRote() {
        return rote;
    }

    public String getChannelInfo() {
        return channelInfo;
    }

    public String getAddress() {
        return address;
    }

    public String getEncodingInfo() {
        return encodingInfo;
    }

    public String getSampleRateInfo() {
        return sampleRateInfo;
    }
}
