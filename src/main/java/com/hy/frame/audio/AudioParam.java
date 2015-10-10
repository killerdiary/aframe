package com.hy.frame.audio;

public class AudioParam {

    private int mFrequency; // 采样率

    private int mChannel; // 声道

    private int mSampBit; // 采样精度

    /**
     * 采样率
     */
    public int getmFrequency() {
        return mFrequency;
    }

    /**
     * 采样率
     */
    public void setmFrequency(int mFrequency) {
        this.mFrequency = mFrequency;
    }

    /**
     * 声道
     */
    public int getmChannel() {
        return mChannel;
    }

    /**
     * 声道
     */
    public void setmChannel(int mChannel) {
        this.mChannel = mChannel;
    }

    /**
     * 采样精度
     */
    public int getmSampBit() {
        return mSampBit;
    }

    /**
     * 采样精度
     */
    public void setmSampBit(int mSampBit) {
        this.mSampBit = mSampBit;
    }

}