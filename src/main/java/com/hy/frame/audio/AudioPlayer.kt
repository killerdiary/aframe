package com.hy.frame.audio

import android.media.AudioManager
import android.media.AudioTrack
import android.os.Handler
import android.util.Log

class AudioPlayer : IPlayComplete {
    private var mHandler: Handler? = null
    private var mAudioParam: AudioParam? = null // 音频参数
    private var mData: ByteArray? = null // 音频数据
    private var mAudioTrack: AudioTrack? = null // AudioTrack对象
    private var mBReady = false // 播放源是否就绪
    private var mPlayAudioThread: PlayAudioThread? = null // 播放线程

    constructor(handler: Handler) {
        mHandler = handler
    }

    constructor(handler: Handler, audioParam: AudioParam) {
        mHandler = handler
        setAudioParam(audioParam)
    }

    /*
     * 设置音频参数
     */
    fun setAudioParam(audioParam: AudioParam) {
        mAudioParam = audioParam
    }

    /*
     * 设置音频源
     */
    fun setDataSource(data: ByteArray) {
        mData = data
    }

    /*
     * 就绪播放源
     */
    fun prepare(): Boolean {
        if (mData == null || mAudioParam == null) {
            return false
        }
        if (mBReady == true) {
            return true
        }
        try {
            createAudioTrack()
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        mBReady = true
        setPlayState(PlayState.MPS_PREPARE)
        return true
    }

    /*
     * 释放播放源
     */
    fun release(): Boolean {
        stop()
        releaseAudioTrack()
        mBReady = false
        setPlayState(PlayState.MPS_UNINIT)
        return true
    }

    /*
     * 播放
     */
    fun play(): Boolean {
        if (mBReady == false) {
            return false
        }
        when (mPlayState) {
            PlayState.MPS_PREPARE -> {
                mPlayOffset = 0
                setPlayState(PlayState.MPS_PLAYING)
                startThread()
            }
            PlayState.MPS_PAUSE -> {
                setPlayState(PlayState.MPS_PLAYING)
                startThread()
            }
        }
        return true
    }

    /*
     * 暂停
     */
    fun pause(): Boolean {
        if (mBReady == false) {
            return false
        }
        if (mPlayState == PlayState.MPS_PLAYING) {
            setPlayState(PlayState.MPS_PAUSE)
            stopThread()
        }
        return true
    }

    /*
     * 停止
     */
    fun stop(): Boolean {
        if (mBReady == false) {
            return false
        }
        setPlayState(PlayState.MPS_PREPARE)
        stopThread()
        return true
    }

    @Synchronized private fun setPlayState(state: Int) {
        mPlayState = state
        if (mHandler != null) {
            val msg = mHandler!!.obtainMessage(STATE_MSG_ID)
            msg.obj = mPlayState
            msg.sendToTarget()
        }
    }

    @Throws(Exception::class)
    private fun createAudioTrack() {

        // 获得构建对象的最小缓冲区大小
        val minBufSize = AudioTrack.getMinBufferSize(mAudioParam!!.getmFrequency(), mAudioParam!!.getmChannel(), mAudioParam!!.getmSampBit())

        mPrimePlaySize = minBufSize * 2
        Log.d(TAG, "mPrimePlaySize = " + mPrimePlaySize)

        // STREAM_ALARM：警告声
        // STREAM_MUSCI：音乐声，例如music等
        // STREAM_RING：铃声
        // STREAM_SYSTEM：系统声音
        // STREAM_VOCIE_CALL：电话声音
        mAudioTrack = AudioTrack(AudioManager.STREAM_MUSIC, mAudioParam!!.getmFrequency(), mAudioParam!!.getmChannel(), mAudioParam!!.getmSampBit(), minBufSize,
                AudioTrack.MODE_STREAM)
        // AudioTrack中有MODE_STATIC和MODE_STREAM两种分类。
        // STREAM的意思是由用户在应用程序通过write方式把数据一次一次得写到audiotrack中。
        // 这个和我们在socket中发送数据一样，应用层从某个地方获取数据，例如通过编解码得到PCM数据，然后write到audiotrack。
        // 这种方式的坏处就是总是在JAVA层和Native层交互，效率损失较大。
        // 而STATIC的意思是一开始创建的时候，就把音频数据放到一个固定的buffer，然后直接传给audiotrack，
        // 后续就不用一次次得write了。AudioTrack会自己播放这个buffer中的数据。
        // 这种方法对于铃声等内存占用较小，延时要求较高的声音来说很适用。

    }

    private fun releaseAudioTrack() {
        if (mAudioTrack != null) {
            if (mAudioTrack!!.state != AudioTrack.STATE_UNINITIALIZED) {
                if (mAudioTrack!!.playState != AudioTrack.PLAYSTATE_STOPPED) {

                    try {
                        mAudioTrack!!.stop()
                    } catch (e: IllegalStateException) {
                        e.printStackTrace()
                    }

                }
                mAudioTrack!!.release()
            }
            mAudioTrack = null
        }

    }

    private fun startThread() {
        if (mPlayAudioThread == null) {
            mThreadExitFlag = false
            mPlayAudioThread = PlayAudioThread()
            mPlayAudioThread!!.start()
        }
    }

    private fun stopThread() {
        if (mPlayAudioThread != null) {
            mThreadExitFlag = true
            mPlayAudioThread = null
        }
    }

    private var mThreadExitFlag = false // 线程退出标志
    private var mPrimePlaySize = 0 // 较优播放块大小
    private var mPlayOffset = 0 // 当前播放位置
    private var mPlayState = 0 // 当前播放状态

    /*
     * 播放音频的线程
     */
    internal inner class PlayAudioThread : Thread() {

        override fun run() {
            Log.d(TAG, "PlayAudioThread run mPlayOffset = " + mPlayOffset)
            mAudioTrack!!.play()
            while (true) {
                if (mThreadExitFlag == true) {
                    break
                }
                try {
                    // int size = mAudioTrack.write(mData, mPlayOffset, mPrimePlaySize);
                    mAudioTrack!!.write(mData!!, mPlayOffset, mPrimePlaySize)
                    mPlayOffset += mPrimePlaySize
                } catch (e: Exception) {
                    e.printStackTrace()
                    this@AudioPlayer.onPlayComplete()
                    break
                }

                if (mPlayOffset >= mData!!.size) {
                    this@AudioPlayer.onPlayComplete()
                    break
                }

            }
            if (mAudioTrack != null && mAudioTrack!!.state != AudioTrack.STATE_UNINITIALIZED) {
                if (mAudioTrack!!.playState != AudioTrack.PLAYSTATE_STOPPED) {

                    try {
                        mAudioTrack!!.stop()
                    } catch (e: IllegalStateException) {
                        e.printStackTrace()
                    }

                }
                mAudioTrack!!.release()
            }
            Log.d(TAG, "PlayAudioThread complete...")
        }
    }

    override fun onPlayComplete() {
        mPlayAudioThread = null
        if (mPlayState != PlayState.MPS_PAUSE) {
            setPlayState(PlayState.MPS_PREPARE)
        }

    }

    companion object {

        private val TAG = "AudioPlayer"
        val STATE_MSG_ID = 0x0010
    }

}
