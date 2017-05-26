package com.hy.frame.camera

import android.content.Context
import android.media.AudioManager

object AudioUtil {
    fun setAudioManage(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true)
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true)
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, 0, 0)
        audioManager.setStreamVolume(AudioManager.STREAM_DTMF, 0, 0)
        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0)
        audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0)
    }
}
