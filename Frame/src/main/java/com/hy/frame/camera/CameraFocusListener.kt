package com.hy.frame.camera

/**
 * CameraFocusListener
 * @author HeYan
 * @time 2017/4/28 15:33
 */
interface CameraFocusListener {
    fun onFocusBegin(x: Float, y: Float)

    fun onFocusEnd()
}
