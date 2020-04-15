package com.yezi.audiotest.bean;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author : yezi
 * @date : 2020/4/11 11:16
 * desc   :
 * version: 1.0
 */
public class MockCallInfo {
    public final static int CALL_STATE_NORMAL = 0;
    public final static int CALL_STATE_RING = 1;
    public final static int CALL_STATE_IN_CALL = 2;
    public final static int CALL_STATE_IN_VOIP_CALL = 3;




    @IntDef({CALL_STATE_NORMAL,CALL_STATE_RING,CALL_STATE_IN_CALL,CALL_STATE_IN_VOIP_CALL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CallCmd{

    }

    private static MockCallInfo mInstance;
    int currentState;
    int lastState;
    int cmdState;
    int pid;
    int error;
    String info;
    public String getInfo() {
        return info;
    }

    private MockCallInfo(){
        currentState = CALL_STATE_NORMAL;
        info = "CALL_STATE_NORMAL";
    }

    public static MockCallInfo getCallInfo(){
        if(mInstance == null){
            synchronized (MockCallInfo.class){
                if(mInstance == null){
                    mInstance = new MockCallInfo();
                }
            }
        }
        return mInstance;
    }

    public void setCallState(int mode) {
        if(mode>=CALL_STATE_NORMAL && mode <=CALL_STATE_IN_VOIP_CALL) {
            currentState = mode;
            info = idToInfo(currentState);
        }
    }

    private String idToInfo(int currentState) {
        switch (currentState){
            case CALL_STATE_NORMAL:
                return "CALL_STATE_NORMAL";
            case CALL_STATE_RING:
                return "CALL_STATE_RING";
            case CALL_STATE_IN_CALL:
                return "CALL_STATE_IN_CALL";
            case CALL_STATE_IN_VOIP_CALL:
                return "CALL_STATE_IN_VOIP_CALL";
            default:
                return "unknown call state";
        }
    }

    public int getCallState(){
        return currentState;
    }

    public int getCmd(){
        return cmdState;
    }

    public void setCmd(@CallCmd int cmd){
        cmdState = cmd;
    }

    public void setErrorCode(int errorCode) {
        error = errorCode;
    }

}
