package com.hy.frame.camera

import android.content.Context
import android.graphics.*
import android.hardware.Camera
import android.media.MediaRecorder
import android.os.Environment
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.VideoView
import com.hy.frame.R
import com.hy.frame.util.MyLog
import java.io.File
import java.io.IOException

/**
 * MCameraView
 * @author HeYan
 * @time 2017/4/28 15:33
 */
class MCameraView @JvmOverloads constructor(//private PowerManager.WakeLock wakeLock = null;
        private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(mContext, attrs, defStyleAttr), SurfaceHolder.Callback, Camera.AutoFocusCallback, CameraFocusListener {

    val TAG = "MCameraView"
    private var mVideoView: VideoView? = null
    private var imgSwitch: ImageView? = null
    private var mFoucsView: FoucsView? = null
    private var btnCapture: CaptureButton? = null

    private var iconWidth = 0
    private var iconMargin = 0
    private var iconSrc = 0

    private var saveVideoPath = ""
    private var videoFileName = ""


    private var mediaRecorder: MediaRecorder? = null
    private var mHolder: SurfaceHolder? = null
    private var mCamera: Camera? = null
    private var mParam: Camera.Parameters? = null
    private var previewWidth: Int = 0
    private var previewHeight: Int = 0
    private var pictureWidth: Int = 0
    private var pictureHeight: Int = 0

    private var autoFoucs: Boolean = false
    private var isPlay = false
    private var isRecorder = false
    private var screenProp: Float = 0.toFloat()

    private var fileName: String? = null
    private var pictureBitmap: Bitmap? = null


    private var SELECTED_CAMERA = -1
    private var CAMERA_POST_POSITION = -1
    private var CAMERA_FRONT_POSITION = -1

    private var cameraViewListener: CameraViewListener? = null

    fun setCameraViewListener(cameraViewListener: CameraViewListener) {
        this.cameraViewListener = cameraViewListener
    }

    init {
        //PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        //wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
        findAvailableCameras()
        SELECTED_CAMERA = CAMERA_POST_POSITION
        //TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.JCameraView, defStyleAttr, 0);
        iconWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 35f, resources.displayMetrics).toInt()
        iconMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15f, resources.displayMetrics).toInt()
        iconSrc = R.mipmap.ic_camera_switch

        initView()
        mHolder = mVideoView!!.holder
        mHolder!!.addCallback(this)
        btnCapture!!.setCaptureListener(object : CaptureButton.CaptureListener {
            override fun capture() {
                this@MCameraView.capture()
            }

            override fun cancel() {
                //                photoImageView.setVisibility(INVISIBLE);
                imgSwitch!!.visibility = View.INVISIBLE
                releaseCamera()
                mCamera = getCamera(SELECTED_CAMERA)
                setStartPreview(mCamera, mHolder)
            }

            override fun determine() {

                if (cameraViewListener != null) {
                    //                    FileUtil.saveBitmap(pictureBitmap);
                    //cameraViewListener.captureSuccess(pictureBitmap);
                }
                //                photoImageView.setVisibility(INVISIBLE);
                imgSwitch!!.visibility = View.VISIBLE
                releaseCamera()
                mCamera = getCamera(SELECTED_CAMERA)
                setStartPreview(mCamera, mHolder)
            }

            override fun record() {
                imgSwitch!!.visibility = View.GONE
                startRecord()
            }

            override fun rencodEnd() {
                stopRecord()
            }

            override fun getRecordResult() {
                if (cameraViewListener != null) {
                    cameraViewListener!!.recordSuccess(fileName!!)
                }
                mVideoView!!.stopPlayback()
                releaseCamera()
                mCamera = getCamera(SELECTED_CAMERA)
                setStartPreview(mCamera, mHolder)
                isPlay = false
            }

            override fun deleteRecordResult() {
                if (fileName != null) {
                    val file = File(fileName!!)
                    if (file.exists()) {
                        file.delete()
                    }
                }
                mVideoView!!.stopPlayback()
                releaseCamera()
                mCamera = getCamera(SELECTED_CAMERA)
                setStartPreview(mCamera, mHolder)
                imgSwitch!!.visibility = View.VISIBLE
                isPlay = false
            }

            override fun scale(scaleValue: Float) {
                if (scaleValue >= 0) {
                    //                    int scaleRate = (int) (scaleValue / 50);
                    //
                    //                    if (scaleRate < 10 && scaleRate >= 0 && mParam != null && mCamera != null && mParam.isSmoothZoomSupported()) {
                    //                        mParam = mCamera.getParameters();
                    //                        mParam.setZoom(scaleRate);
                    //                        mCamera.setParameters(mParam);
                    //                    }
                    //                    Log.i(TAG, "scaleValue = " + (int) scaleValue + " = scaleRate" + scaleRate);
                }
            }
        })

    }


    private fun initView() {
        setWillNotDraw(false)
        this.setBackgroundColor(Color.BLACK)

        //Surface
        mVideoView = VideoView(mContext)
        val videoViewParam = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        videoViewParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        mVideoView!!.layoutParams = videoViewParam

        val rlyControl = RelativeLayout(mContext)
        rlyControl.setBackgroundColor(Color.argb(100, 0, 0, 0))
        //float height = TypedValue.complexToDimension(120, mContext.getResources().getDisplayMetrics());


        val width = (mContext.resources.displayMetrics.widthPixels * 0.7).toInt()
        val height = width / 9 * 4
        //CaptureButton
        val btnParams = RelativeLayout.LayoutParams(width, RelativeLayout.LayoutParams.WRAP_CONTENT)
        btnParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        btnCapture = CaptureButton(mContext)
        btnCapture!!.layoutParams = btnParams


        imgSwitch = ImageView(mContext)
        Log.i("CJT", this.measuredWidth.toString() + " ==================================")
        val switchParams = RelativeLayout.LayoutParams(iconWidth, iconWidth)
        switchParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
        switchParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)
        switchParams.setMargins(0, 0, iconMargin, 0)
        imgSwitch!!.layoutParams = switchParams
        imgSwitch!!.setImageResource(iconSrc)
        imgSwitch!!.setOnClickListener {
            if (mCamera != null) {
                releaseCamera()
                if (SELECTED_CAMERA == CAMERA_POST_POSITION) {
                    SELECTED_CAMERA = CAMERA_FRONT_POSITION
                } else {
                    SELECTED_CAMERA = CAMERA_POST_POSITION
                }
                mCamera = getCamera(SELECTED_CAMERA)
                previewHeight = 0
                previewWidth = previewHeight
                pictureHeight = 0
                pictureWidth = pictureHeight
                setStartPreview(mCamera, mHolder)
            }
        }
        val controlParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (height * 1).toInt())
        controlParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        rlyControl.layoutParams = controlParams

        mFoucsView = FoucsView(mContext, 120)
        mFoucsView!!.visibility = View.INVISIBLE

        rlyControl.addView(btnCapture)
        rlyControl.addView(imgSwitch)
        this.addView(mVideoView)
        this.addView(rlyControl)
        //this.addView(imgSwitch);
        this.addView(mFoucsView)


        mVideoView!!.setOnClickListener {
            mCamera!!.autoFocus(this@MCameraView)
            //Log.i(TAG, "Touch To Focus");
        }

        //初始化为自动对焦
        autoFoucs = true
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
    }

    private val checkCameraRunnable = Runnable {
        if (isGetCamera && mCamera == null) {
            if (cameraViewListener != null)
                cameraViewListener!!.onCameraError()
        }
    }
    private var isGetCamera: Boolean = false

    //获取Camera
    private fun getCamera(position: Int): Camera? {
        MyLog.e(javaClass, "getCamera")
        var camera: Camera? = null
        try {
            removeCallbacks(checkCameraRunnable)
            postDelayed(checkCameraRunnable, (3 * 1000).toLong())
            isGetCamera = true
            camera = Camera.open(position)
            isGetCamera = false
        } catch (e: Exception) {
            e.printStackTrace()
            //throwError(new Exception("Cannot access the camera, you may need to restart your device.", e2));
            //Toast.makeText(getContext(), "Cannot access the camera, you may need to restart your device.", Toast.LENGTH_SHORT).show();
            if (cameraViewListener != null)
                cameraViewListener!!.onCameraError()
        }

        return camera
    }

    fun btnReturn() {
        setStartPreview(mCamera, mHolder)
    }


    private fun setStartPreview(camera: Camera?, holder: SurfaceHolder?) {
        MyLog.e(javaClass, "setStartPreview")
        if (camera == null) {
            //Log.i(TAG, "Camera is null");
            return
        }
        try {
            mParam = camera.parameters
            //
            val previewSize = CameraParamUtil.instance.getPreviewSize(mParam!!.supportedPreviewSizes, 1000, screenProp)
            val pictureSize = CameraParamUtil.instance.getPictureSize(mParam!!.supportedPictureSizes, 1200, screenProp)

            mParam!!.setPreviewSize(previewSize.width, previewSize.height)
            mParam!!.setPictureSize(pictureSize.width, pictureSize.height)

            if (CameraParamUtil.instance.isSupportedFocusMode(mParam!!.supportedFocusModes, Camera.Parameters.FOCUS_MODE_AUTO)) {
                mParam!!.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
            }
            if (CameraParamUtil.instance.isSupportedPictureFormats(mParam!!.supportedPictureFormats, ImageFormat.JPEG)) {
                mParam!!.pictureFormat = ImageFormat.JPEG
                mParam!!.jpegQuality = 100
            }
            camera.parameters = mParam
            mParam = camera.parameters
            camera.setPreviewDisplay(holder)
            camera.setDisplayOrientation(90)
            camera.startPreview()
        } catch (e: Exception) {
            e.printStackTrace()
            if (cameraViewListener != null)
                cameraViewListener!!.onCameraError()
        }

    }

    private fun releaseCamera() {
        MyLog.e(javaClass, "releaseCamera")
        if (mCamera != null && mHolder != null) {
            try {
                mHolder!!.removeCallback(this)
                mCamera!!.setPreviewCallback(null)
                mCamera!!.stopPreview()
                mCamera!!.release()
                mCamera = null
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }


    fun capture() {
        if (autoFoucs) {
            mCamera!!.autoFocus(this)
        } else {
            if (SELECTED_CAMERA == CAMERA_POST_POSITION) {
                mCamera!!.takePicture(null, null, Camera.PictureCallback { data, camera ->
                    var bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                    val matrix = Matrix()
                    matrix.setRotate(90f)
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                    pictureBitmap = bitmap
                    imgSwitch!!.visibility = View.INVISIBLE
                    btnCapture!!.captureSuccess()
                })
            } else if (SELECTED_CAMERA == CAMERA_FRONT_POSITION) {
                mCamera!!.takePicture(null, null, Camera.PictureCallback { data, camera ->
                    var bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                    val matrix = Matrix()
                    matrix.setRotate(270f)
                    matrix.postScale(-1f, 1f)
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                    pictureBitmap = bitmap
                    imgSwitch!!.visibility = View.INVISIBLE
                    btnCapture!!.captureSuccess()
                })
            }
        }
    }

    //自动对焦
    override fun onAutoFocus(success: Boolean, camera: Camera) {
        if (autoFoucs) {
            if (SELECTED_CAMERA == CAMERA_POST_POSITION && success) {
                mCamera!!.takePicture(null, null, Camera.PictureCallback { data, camera ->
                    var bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                    val matrix = Matrix()
                    matrix.setRotate(90f)
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                    pictureBitmap = bitmap
                    imgSwitch!!.visibility = View.INVISIBLE
                    btnCapture!!.captureSuccess()
                })
            } else if (SELECTED_CAMERA == CAMERA_FRONT_POSITION) {
                mCamera!!.takePicture(null, null, Camera.PictureCallback { data, camera ->
                    var bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                    val matrix = Matrix()
                    matrix.setRotate(270f)
                    matrix.postScale(-1f, 1f)
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                    pictureBitmap = bitmap
                    imgSwitch!!.visibility = View.INVISIBLE
                    btnCapture!!.captureSuccess()
                })
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec).toFloat()
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec).toFloat()
        screenProp = heightSize / widthSize
        //Log.i(TAG, "ScreenProp = " + screenProp + " " + widthSize + " " + heightSize);
    }


    override fun surfaceCreated(holder: SurfaceHolder) {
        mHolder = holder
        MyLog.e(javaClass, "surfaceCreated")
        if (isResume && mCamera != null)
            setStartPreview(mCamera, holder)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        mHolder = holder
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        releaseCamera()
        MyLog.e(javaClass, "surfaceDestroyed")
    }

    private var isResume: Boolean = false

    fun onResume() {
        isResume = true
        MyLog.e(javaClass, "onResume")
        try {
            mCamera = getCamera(SELECTED_CAMERA)
        } catch (e: Exception) {
            return
        }

        if (mCamera != null && mHolder != null) {
            setStartPreview(mCamera, mHolder)
            imgSwitch!!.visibility = View.VISIBLE
        } else {
            //Log.i(TAG, "Camera is null!");
        }
        //wakeLock.acquire();
    }

    fun onPause() {
        if (!isResume) return
        isResume = false
        MyLog.e(javaClass, "onPause")
        releaseCamera()
        btnCapture!!.onPause()
        //wakeLock.release();
        if (isRecorder) {
            isRecorder = false
            mediaRecorder!!.stop()
            mediaRecorder!!.release()
            mediaRecorder = null
        }
    }


    private fun startRecord() {
        if (isRecorder) {
            mediaRecorder!!.stop()
            mediaRecorder!!.release()
            mediaRecorder = null
        }
        if (mCamera == null) {
            //Log.i(TAG, "Camera is null");
            stopRecord()
            return
        }
        mCamera!!.unlock()
        if (mediaRecorder == null) {
            mediaRecorder = MediaRecorder()
        }
        mediaRecorder!!.reset()
        mediaRecorder!!.setCamera(mCamera)
        mediaRecorder!!.setVideoSource(MediaRecorder.VideoSource.CAMERA)
        //        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
        mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder!!.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        mediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        //mediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

        if (mParam == null) {
            mParam = mCamera!!.parameters
        }
        val videoSize = CameraParamUtil.instance.getPictureSize(mParam!!.supportedVideoSizes, 1000, screenProp)

        mediaRecorder!!.setVideoSize(videoSize.width, videoSize.height)
        if (SELECTED_CAMERA == CAMERA_FRONT_POSITION) {
            mediaRecorder!!.setOrientationHint(270)
        } else {
            mediaRecorder!!.setOrientationHint(90)
        }
        mediaRecorder!!.setMaxDuration(20000)
        //mediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
        mediaRecorder!!.setVideoEncodingBitRate(3 * 640 * 480)
        mediaRecorder!!.setPreviewDisplay(mHolder!!.surface)

        videoFileName = "video_" + System.currentTimeMillis() + ".mp4"
        if (saveVideoPath == "") {
            saveVideoPath = Environment.getExternalStorageDirectory().path
        }
        mediaRecorder!!.setOutputFile(saveVideoPath + "/" + videoFileName)
        try {
            mediaRecorder!!.prepare()
            mediaRecorder!!.start()
            isRecorder = true
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun stopRecord() {
        MyLog.e(javaClass, "stopRecord")
        if (mediaRecorder != null) {
            mediaRecorder!!.setOnErrorListener(null)
            mediaRecorder!!.setOnInfoListener(null)
            mediaRecorder!!.setPreviewDisplay(null)
            try {
                mediaRecorder!!.stop()
                isRecorder = false
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }

            mediaRecorder!!.release()
            mediaRecorder = null
            releaseCamera()
            fileName = saveVideoPath + "/" + videoFileName
            mVideoView!!.setVideoPath(fileName)
            mVideoView!!.start()
            mVideoView!!.setOnPreparedListener { mp ->
                isPlay = true
                mp.start()
                mp.isLooping = true
            }
            mVideoView!!
                    .setOnCompletionListener {
                        //                            mVideoView.setVideoPath(fileName);
                        //                            mVideoView.start();
                    }
        }
    }


    fun setSaveVideoPath(saveVideoPath: String) {
        this.saveVideoPath = saveVideoPath
    }

    /**
     * 获得可用的相机，并设置前后摄像机的ID
     */
    private fun findAvailableCameras() {

        val info = Camera.CameraInfo()
        val numCamera = Camera.getNumberOfCameras()
        for (i in 0..numCamera - 1) {
            Camera.getCameraInfo(i, info)
            // 找到了前置摄像头
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                CAMERA_FRONT_POSITION = info.facing
                //Log.i(TAG, "POSITION = " + CAMERA_FRONT_POSITION);
            }
            // 找到了后置摄像头
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                CAMERA_POST_POSITION = info.facing
                //Log.i(TAG, "POSITION = " + CAMERA_POST_POSITION);
            }
        }
    }


    fun setAutoFoucs(autoFoucs: Boolean) {
        this.autoFoucs = autoFoucs
    }

    override fun onFocusBegin(x: Float, y: Float) {
        if (mCamera == null) return
        //Log.d(TAG, "onFocusBegin");
        mFoucsView!!.visibility = View.VISIBLE
        //mFoucsView.setBackgroundColor(Color.RED);
        mFoucsView!!.x = x - mFoucsView!!.width / 2
        mFoucsView!!.y = y - mFoucsView!!.height / 2
        mCamera!!.autoFocus { success, camera ->
            if (success) {
                mCamera!!.cancelAutoFocus()
                onFocusEnd()
            }
        }
    }

    //手动对焦结束
    override fun onFocusEnd() {
        mFoucsView!!.visibility = View.INVISIBLE
    }

    interface CameraViewListener {

        //void captureSuccess(Bitmap bitmap);
        fun onCameraError()

        fun recordSuccess(url: String)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        //Log.d(TAG, "autoFoucs=" + autoFoucs + ",SELECTED_CAMERA=" + SELECTED_CAMERA + ",isPlay=" + isPlay);
        if (!autoFoucs && event.action == MotionEvent.ACTION_DOWN && SELECTED_CAMERA == CAMERA_POST_POSITION && !isPlay) {
            onFocusBegin(event.x, event.y)
        }
        return super.onTouchEvent(event)
    }

    fun cancelAudio() {
        AudioUtil.setAudioManage(mContext)
    }
}
