package com.hy.frame.audio

class AudioParam {

    private var mFrequency: Int = 0 // 采样率

    private var mChannel: Int = 0 // 声道

    private var mSampBit: Int = 0 // 采样精度

    /**
     * 采样率
     */
    fun getmFrequency(): Int {
        return mFrequency
    }

    /**
     * 采样率
     */
    fun setmFrequency(mFrequency: Int) {
        this.mFrequency = mFrequency
    }

    /**
     * 声道
     */
    fun getmChannel(): Int {
        return mChannel
    }

    /**
     * 声道
     */
    fun setmChannel(mChannel: Int) {
        this.mChannel = mChannel
    }

    /**
     * 采样精度
     */
    fun getmSampBit(): Int {
        return mSampBit
    }

    /**
     * 采样精度
     */
    fun setmSampBit(mSampBit: Int) {
        this.mSampBit = mSampBit
    }

}