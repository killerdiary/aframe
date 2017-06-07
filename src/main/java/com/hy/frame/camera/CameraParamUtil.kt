package com.hy.frame.camera

import android.hardware.Camera
import android.hardware.Camera.Size
import android.util.Log
import java.util.*

/**
 * CameraParamUtil
 * @author HeYan
 * @time 2017/4/28 15:33
 */
class CameraParamUtil private constructor() {
    private val sizeComparator = CameraSizeComparator()

    fun getPreviewSize(list: List<Camera.Size>, th: Int, rate: Float): Size {
        Collections.sort(list, sizeComparator)
        var i = 0
        for (s in list) {
            if (s.width > th && equalRate(s, rate)) {
                Log.i(TAG, "MakeSure Preview :w = " + s.width + " h = " + s.height)
                break
            }
            i++
        }
        if (i == list.size) {
            return getBestSize(list, rate)
        } else {
            return list[i]
        }
    }

    fun getPictureSize(list: List<Camera.Size>, th: Int, rate: Float): Size {
        Collections.sort(list, sizeComparator)

        var i = 0
        for (s in list) {
            if (s.width > th && equalRate(s, rate)) {
                Log.i(TAG, "MakeSure Picture :w = " + s.width + " h = " + s.height)
                break
            }
            i++
        }
        if (i == list.size) {
            return getBestSize(list, rate)
        } else {
            return list[i]
        }
    }

    fun getBestSize(list: List<Camera.Size>, rate: Float): Size {
        var previewDisparity = 100f
        var index = 0
        for (i in list.indices) {
            val cur = list[i]
            val prop = cur.width.toFloat() / cur.height.toFloat()
            if (Math.abs(rate - prop) < previewDisparity) {
                previewDisparity = Math.abs(rate - prop)
                index = i
            }
        }
        return list[index]
    }


    fun equalRate(s: Size, rate: Float): Boolean {
        val r = s.width.toFloat() / s.height.toFloat()
        if (Math.abs(r - rate) <= 0.2) {
            return true
        } else {
            return false
        }
    }

    fun isSupportedFocusMode(focusList: List<String>, focusMode: String): Boolean {
        for (i in focusList.indices) {
            if (focusMode == focusList[i]) {
                Log.i(TAG, "FocusMode supported " + focusMode)
                return true
            }
        }
        Log.i(TAG, "FocusMode not supported " + focusMode)
        return false
    }

    fun isSupportedPictureFormats(supportedPictureFormats: List<Int>, jpeg: Int): Boolean {
        for (i in supportedPictureFormats.indices) {
            if (jpeg == supportedPictureFormats[i]) {
                Log.i(TAG, "Formats supported " + jpeg)
                return true
            }
        }
        Log.i(TAG, "Formats not supported " + jpeg)
        return false
    }

    inner class CameraSizeComparator : Comparator<Size> {
        override fun compare(lhs: Size, rhs: Size): Int {
            if (lhs.width == rhs.width) {
                return 0
            } else if (lhs.width > rhs.width) {
                return 1
            } else {
                return -1
            }
        }

    }

    companion object {
        private val TAG = "JCameraView"
        private var cameraParamUtil: CameraParamUtil? = null

        val instance: CameraParamUtil
            get() {
                if (cameraParamUtil == null) {
                    cameraParamUtil = CameraParamUtil()
                    return cameraParamUtil as CameraParamUtil
                } else {
                    return cameraParamUtil as CameraParamUtil
                }
            }
    }
}
