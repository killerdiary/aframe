package com.hy.frame.audio

interface PlayState {
    companion object {

        val MPS_UNINIT = 0                // 未就绪

        val MPS_PREPARE = 1            // 准备就绪(停止)

        val MPS_PLAYING = 2            // 播放中

        val MPS_PAUSE = 3                // 暂停
    }
}
