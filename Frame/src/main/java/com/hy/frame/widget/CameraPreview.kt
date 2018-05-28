package com.hy.frame.widget

import android.content.Context
import android.graphics.PixelFormat
import android.hardware.Camera
import android.hardware.Camera.Size
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView

import com.hy.frame.util.MyLog

import java.io.IOException

/**
 * 相机图片预览类
 * author HeYan
 * time 2015/12/29 19:30
 */
class CameraPreview @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback {

    private var mHolder: SurfaceHolder? = null
    private var mCamera: Camera? = null
    private var mPreviewSize: Size? = null
    private var mSupportedPreviewSizes: List<Size>? = null

    init {
        init()
    }

    /**
     * 初始化工作
     */
    private fun init() {
        MyLog.d(javaClass, "initialize")
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = holder
        mHolder!!.addCallback(this)
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder!!.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)

    }

    fun setCamera(camera: Camera) {
        mCamera = camera
        if (mCamera != null) {
            mSupportedPreviewSizes = mCamera!!.parameters.supportedPreviewSizes
            requestLayout()
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        MyLog.d(javaClass, "surfaceCreated")
        // The Surface has been created, now tell the camera where to draw the
        // preview.
        try {
            if (null != mCamera) {
                mCamera!!.setPreviewDisplay(holder)
            }
        } catch (e1: IOException) {
            e1.printStackTrace()
            MyLog.d(javaClass,
                    "Error setting camera preview display: " + e1.message)
        }

        try {
            if (null != mCamera) {
                mCamera!!.startPreview()
            }

            MyLog.d(javaClass, "surfaceCreated successfully! ")
        } catch (e: Exception) {
            MyLog.d(javaClass,
                    "Error setting camera preview: " + e.message)
        }

    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int,
                                height: Int) {

        MyLog.d(javaClass, "surface changed")
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (null == mHolder!!.surface) {
            // preview surface does not exist
            return
        }

        // stop preview before making changes
        try {
            if (null != mCamera) {
                mCamera!!.stopPreview()
            }
        } catch (e: Exception) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        if (null != mCamera) {
            val parameters = mCamera!!.parameters
            parameters.pictureFormat = PixelFormat.JPEG//图片格式
            parameters.setPreviewSize(mPreviewSize!!.width, mPreviewSize!!.height)
            requestLayout()
            mCamera!!.parameters = parameters
            mCamera!!.setDisplayOrientation(90)
            MyLog.d(javaClass, "camera set parameters successfully!: " + parameters)
        }
        // 这里可以用来设置尺寸
        // start preview with new settings
        try {
            if (null != mCamera) {
                mCamera!!.setPreviewDisplay(mHolder)
                mCamera!!.startPreview()
            }
        } catch (e: Exception) {
            MyLog.d(javaClass, "Error starting camera preview: " + e.message)
        }

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        MyLog.d(javaClass, "surfaceDestroyed")
        if (null != mCamera) {
            mCamera!!.stopPreview()
            mCamera!!.release()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // We purposely disregard child measurements because act as a
        // wrapper to a SurfaceView that centers the camera preview instead
        // of stretching it.
        val width = resolveSize(suggestedMinimumWidth,
                widthMeasureSpec)
        val height = resolveSize(suggestedMinimumHeight,
                heightMeasureSpec)
        setMeasuredDimension(width, height)

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width,
                    height)
        }
    }

    private fun getOptimalPreviewSize(sizes: List<Size>?, w: Int, h: Int): Size? {
        val ASPECT_TOLERANCE = 0.1
        val targetRatio = w.toDouble() / h
        if (sizes == null)
            return null

        var optimalSize: Size? = null
        var minDiff = java.lang.Double.MAX_VALUE

        val targetHeight = h

        // Try to find an size match aspect ratio and size
        for (size in sizes) {
            val ratio = size.width.toDouble() / size.height
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size
                minDiff = Math.abs(size.height - targetHeight).toDouble()
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = java.lang.Double.MAX_VALUE
            for (size in sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size
                    minDiff = Math.abs(size.height - targetHeight).toDouble()
                }
            }
        }
        return optimalSize
    }

}