package com.hy.frame.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.hy.frame.R;
import com.hy.frame.util.MyLog;

import java.io.File;
import java.io.IOException;

/**
 * MCameraView
 *
 * @author HeYan
 * @time 2017/4/28 15:33
 */
public class MCameraView extends RelativeLayout implements SurfaceHolder.Callback, Camera.AutoFocusCallback, CameraFocusListener {

    public final String TAG = "MCameraView";

    //private PowerManager.WakeLock wakeLock = null;
    private Context mContext;
    private VideoView mVideoView;
    private ImageView imgSwitch;
    private FoucsView mFoucsView;
    private CaptureButton btnCapture;

    private int iconWidth = 0;
    private int iconMargin = 0;
    private int iconSrc = 0;

    private String saveVideoPath = "";
    private String videoFileName = "";


    private MediaRecorder mediaRecorder;
    private SurfaceHolder mHolder = null;
    private Camera mCamera;
    private Camera.Parameters mParam;
    private int previewWidth;
    private int previewHeight;
    private int pictureWidth;
    private int pictureHeight;

    private boolean autoFoucs;
    private boolean isPlay = false;
    private boolean isRecorder = false;
    private float screenProp;

    private String fileName;
    private Bitmap pictureBitmap;


    private int SELECTED_CAMERA = -1;
    private int CAMERA_POST_POSITION = -1;
    private int CAMERA_FRONT_POSITION = -1;

    private CameraViewListener cameraViewListener;

    public void setCameraViewListener(CameraViewListener cameraViewListener) {
        this.cameraViewListener = cameraViewListener;
    }

    public MCameraView(Context context) {
        this(context, null);
    }

    public MCameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MCameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        //PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        //wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
        findAvailableCameras();
        SELECTED_CAMERA = CAMERA_POST_POSITION;
        //TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.JCameraView, defStyleAttr, 0);
        iconWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 35, getResources().getDisplayMetrics());
        iconMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics());
        iconSrc = R.mipmap.ic_camera_switch;

        initView();
        mHolder = mVideoView.getHolder();
        mHolder.addCallback(this);
        btnCapture.setCaptureListener(new CaptureButton.CaptureListener() {
            @Override
            public void capture() {
                MCameraView.this.capture();
            }

            @Override
            public void cancel() {
//                photoImageView.setVisibility(INVISIBLE);
                imgSwitch.setVisibility(INVISIBLE);
                releaseCamera();
                mCamera = getCamera(SELECTED_CAMERA);
                setStartPreview(mCamera, mHolder);
            }

            @Override
            public void determine() {

                if (cameraViewListener != null) {
//                    FileUtil.saveBitmap(pictureBitmap);
                    //cameraViewListener.captureSuccess(pictureBitmap);
                }
//                photoImageView.setVisibility(INVISIBLE);
                imgSwitch.setVisibility(VISIBLE);
                releaseCamera();
                mCamera = getCamera(SELECTED_CAMERA);
                setStartPreview(mCamera, mHolder);
            }

            @Override
            public void record() {
                imgSwitch.setVisibility(GONE);
                startRecord();
            }

            @Override
            public void rencodEnd() {
                stopRecord();
            }

            @Override
            public void getRecordResult() {
                if (cameraViewListener != null) {
                    cameraViewListener.recordSuccess(fileName);
                }
                mVideoView.stopPlayback();
                releaseCamera();
                mCamera = getCamera(SELECTED_CAMERA);
                setStartPreview(mCamera, mHolder);
                isPlay = false;
            }

            @Override
            public void deleteRecordResult() {
                if (fileName != null) {
                    File file = new File(fileName);
                    if (file.exists()) {
                        file.delete();
                    }
                }
                mVideoView.stopPlayback();
                releaseCamera();
                mCamera = getCamera(SELECTED_CAMERA);
                setStartPreview(mCamera, mHolder);
                imgSwitch.setVisibility(VISIBLE);
                isPlay = false;
            }

            @Override
            public void scale(float scaleValue) {
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
        });

    }


    private void initView() {
        setWillNotDraw(false);
        this.setBackgroundColor(Color.BLACK);

        //Surface
        mVideoView = new VideoView(mContext);
        LayoutParams videoViewParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        videoViewParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        mVideoView.setLayoutParams(videoViewParam);

        RelativeLayout rlyControl = new RelativeLayout(mContext);
        rlyControl.setBackgroundColor(Color.argb(100, 0, 0, 0));
        //float height = TypedValue.complexToDimension(120, mContext.getResources().getDisplayMetrics());


        int width = (int) (mContext.getResources().getDisplayMetrics().widthPixels * 0.7);
        int height = (width / 9) * 4;
        //CaptureButton
        LayoutParams btnParams = new LayoutParams(width, LayoutParams.WRAP_CONTENT);
        btnParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        btnCapture = new CaptureButton(mContext);
        btnCapture.setLayoutParams(btnParams);


        imgSwitch = new ImageView(mContext);
        Log.i("CJT", this.getMeasuredWidth() + " ==================================");
        LayoutParams switchParams = new LayoutParams(iconWidth, iconWidth);
        switchParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        switchParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        switchParams.setMargins(0, 0, iconMargin, 0);
        imgSwitch.setLayoutParams(switchParams);
        imgSwitch.setImageResource(iconSrc);
        imgSwitch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCamera != null) {
                    releaseCamera();
                    if (SELECTED_CAMERA == CAMERA_POST_POSITION) {
                        SELECTED_CAMERA = CAMERA_FRONT_POSITION;
                    } else {
                        SELECTED_CAMERA = CAMERA_POST_POSITION;
                    }
                    mCamera = getCamera(SELECTED_CAMERA);
                    previewWidth = previewHeight = 0;
                    pictureWidth = pictureHeight = 0;
                    setStartPreview(mCamera, mHolder);
                }
            }
        });
        LayoutParams controlParams = new LayoutParams(LayoutParams.MATCH_PARENT, (int) (height * 1));
        controlParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlyControl.setLayoutParams(controlParams);

        mFoucsView = new FoucsView(mContext, 120);
        mFoucsView.setVisibility(INVISIBLE);

        rlyControl.addView(btnCapture);
        rlyControl.addView(imgSwitch);
        this.addView(mVideoView);
        this.addView(rlyControl);
        //this.addView(imgSwitch);
        this.addView(mFoucsView);


        mVideoView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.autoFocus(MCameraView.this);
                //Log.i(TAG, "Touch To Focus");
            }
        });

        //初始化为自动对焦
        autoFoucs = true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    private Runnable checkCameraRunnable = new Runnable() {
        @Override
        public void run() {
            if (isGetCamera && mCamera == null) {
                if (cameraViewListener != null)
                    cameraViewListener.onCameraError();
            }
        }
    };
    private boolean isGetCamera;

    //获取Camera
    private Camera getCamera(int position) {
        MyLog.e(getClass(), "getCamera");
        Camera camera = null;
        try {
            removeCallbacks(checkCameraRunnable);
            postDelayed(checkCameraRunnable, 3 * 1000);
            isGetCamera = true;
            camera = Camera.open(position);
            isGetCamera = false;
        } catch (Exception e) {
            e.printStackTrace();
            //throwError(new Exception("Cannot access the camera, you may need to restart your device.", e2));
            //Toast.makeText(getContext(), "Cannot access the camera, you may need to restart your device.", Toast.LENGTH_SHORT).show();
            if (cameraViewListener != null)
                cameraViewListener.onCameraError();
        }
        return camera;
    }

    public void btnReturn() {
        setStartPreview(mCamera, mHolder);
    }


    private void setStartPreview(Camera camera, SurfaceHolder holder) {
        MyLog.e(getClass(), "setStartPreview");
        if (camera == null) {
            //Log.i(TAG, "Camera is null");
            return;
        }
        try {
            mParam = camera.getParameters();
//
            Camera.Size previewSize = CameraParamUtil.getInstance().getPreviewSize(mParam.getSupportedPreviewSizes(), 1000, screenProp);
            Camera.Size pictureSize = CameraParamUtil.getInstance().getPictureSize(mParam.getSupportedPictureSizes(), 1200, screenProp);

            mParam.setPreviewSize(previewSize.width, previewSize.height);
            mParam.setPictureSize(pictureSize.width, pictureSize.height);

            if (CameraParamUtil.getInstance().isSupportedFocusMode(mParam.getSupportedFocusModes(), Camera.Parameters.FOCUS_MODE_AUTO)) {
                mParam.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
            if (CameraParamUtil.getInstance().isSupportedPictureFormats(mParam.getSupportedPictureFormats(), ImageFormat.JPEG)) {
                mParam.setPictureFormat(ImageFormat.JPEG);
                mParam.setJpegQuality(100);
            }
            camera.setParameters(mParam);
            mParam = camera.getParameters();
            camera.setPreviewDisplay(holder);
            camera.setDisplayOrientation(90);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void releaseCamera() {
        MyLog.e(getClass(), "releaseCamera");
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }


    public void capture() {
        if (autoFoucs) {
            mCamera.autoFocus(this);
        } else {
            if (SELECTED_CAMERA == CAMERA_POST_POSITION) {
                mCamera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        Matrix matrix = new Matrix();
                        matrix.setRotate(90);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                        pictureBitmap = bitmap;
                        imgSwitch.setVisibility(INVISIBLE);
                        btnCapture.captureSuccess();
                    }
                });
            } else if (SELECTED_CAMERA == CAMERA_FRONT_POSITION) {
                mCamera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        Matrix matrix = new Matrix();
                        matrix.setRotate(270);
                        matrix.postScale(-1, 1);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                        pictureBitmap = bitmap;
                        imgSwitch.setVisibility(INVISIBLE);
                        btnCapture.captureSuccess();
                    }
                });
            }
        }
    }

    //自动对焦
    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        if (autoFoucs) {
            if (SELECTED_CAMERA == CAMERA_POST_POSITION && success) {
                mCamera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        Matrix matrix = new Matrix();
                        matrix.setRotate(90);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                        pictureBitmap = bitmap;
                        imgSwitch.setVisibility(INVISIBLE);
                        btnCapture.captureSuccess();
                    }
                });
            } else if (SELECTED_CAMERA == CAMERA_FRONT_POSITION) {
                mCamera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        Matrix matrix = new Matrix();
                        matrix.setRotate(270);
                        matrix.postScale(-1, 1);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                        pictureBitmap = bitmap;
                        imgSwitch.setVisibility(INVISIBLE);
                        btnCapture.captureSuccess();
                    }
                });
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float widthSize = MeasureSpec.getSize(widthMeasureSpec);
        float heightSize = MeasureSpec.getSize(heightMeasureSpec);
        screenProp = heightSize / widthSize;
        //Log.i(TAG, "ScreenProp = " + screenProp + " " + widthSize + " " + heightSize);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mHolder = holder;
        MyLog.e(getClass(), "surfaceCreated");
        if (isResume && mCamera != null)
            setStartPreview(mCamera, holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mHolder = holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
        MyLog.e(getClass(), "surfaceDestroyed");
    }

    private boolean isResume;

    public void onResume() {
        isResume = true;
        MyLog.e(getClass(), "onResume");
        try {
            mCamera = getCamera(SELECTED_CAMERA);
        } catch (Exception e) {
            return;
        }
        if (mCamera != null && mHolder != null) {
            setStartPreview(mCamera, mHolder);
            imgSwitch.setVisibility(VISIBLE);
        } else {
            //Log.i(TAG, "Camera is null!");
        }
        //wakeLock.acquire();
    }

    public void onPause() {
        isResume = false;
        MyLog.e(getClass(), "onPause");
        releaseCamera();
        btnCapture.onPause();
        //wakeLock.release();
        if (isRecorder) {
            isRecorder = false;
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }


    private void startRecord() {
        if (isRecorder) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
        if (mCamera == null) {
            //Log.i(TAG, "Camera is null");
            stopRecord();
            return;
        }
        mCamera.unlock();
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
        }
        mediaRecorder.reset();
        mediaRecorder.setCamera(mCamera);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
//        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        if (mParam == null) {
            mParam = mCamera.getParameters();
        }
        Camera.Size videoSize = CameraParamUtil.getInstance().getPictureSize(mParam.getSupportedVideoSizes(), 1000, screenProp);

        mediaRecorder.setVideoSize(videoSize.width, videoSize.height);
        if (SELECTED_CAMERA == CAMERA_FRONT_POSITION) {
            mediaRecorder.setOrientationHint(270);
        } else {
            mediaRecorder.setOrientationHint(90);
        }
        mediaRecorder.setMaxDuration(20000);
        //mediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
        mediaRecorder.setVideoEncodingBitRate(3 * 640 * 480);
        mediaRecorder.setPreviewDisplay(mHolder.getSurface());

        videoFileName = "video_" + System.currentTimeMillis() + ".mp4";
        if (saveVideoPath.equals("")) {
            saveVideoPath = Environment.getExternalStorageDirectory().getPath();
        }
        mediaRecorder.setOutputFile(saveVideoPath + "/" + videoFileName);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecorder = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecord() {
        MyLog.e(getClass(), "stopRecord");
        if (mediaRecorder != null) {
            mediaRecorder.setOnErrorListener(null);
            mediaRecorder.setOnInfoListener(null);
            mediaRecorder.setPreviewDisplay(null);
            try {
                mediaRecorder.stop();
                isRecorder = false;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            mediaRecorder.release();
            mediaRecorder = null;
            releaseCamera();
            fileName = saveVideoPath + "/" + videoFileName;
            mVideoView.setVideoPath(fileName);
            mVideoView.start();
            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    isPlay = true;
                    mp.start();
                    mp.setLooping(true);
                }
            });
            mVideoView
                    .setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
//                            mVideoView.setVideoPath(fileName);
//                            mVideoView.start();
                        }
                    });
        }
    }


    public void setSaveVideoPath(String saveVideoPath) {
        this.saveVideoPath = saveVideoPath;
    }

    /**
     * 获得可用的相机，并设置前后摄像机的ID
     */
    private void findAvailableCameras() {

        Camera.CameraInfo info = new Camera.CameraInfo();
        int numCamera = Camera.getNumberOfCameras();
        for (int i = 0; i < numCamera; i++) {
            Camera.getCameraInfo(i, info);
            // 找到了前置摄像头
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                CAMERA_FRONT_POSITION = info.facing;
                //Log.i(TAG, "POSITION = " + CAMERA_FRONT_POSITION);
            }
            // 找到了后置摄像头
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                CAMERA_POST_POSITION = info.facing;
                //Log.i(TAG, "POSITION = " + CAMERA_POST_POSITION);
            }
        }
    }


    public void setAutoFoucs(boolean autoFoucs) {
        this.autoFoucs = autoFoucs;
    }

    @Override
    public void onFocusBegin(float x, float y) {
        if (mCamera == null) return;
        //Log.d(TAG, "onFocusBegin");
        mFoucsView.setVisibility(VISIBLE);
        //mFoucsView.setBackgroundColor(Color.RED);
        mFoucsView.setX(x - mFoucsView.getWidth() / 2);
        mFoucsView.setY(y - mFoucsView.getHeight() / 2);
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    mCamera.cancelAutoFocus();
                    onFocusEnd();
                }
            }
        });
    }

    //手动对焦结束
    @Override
    public void onFocusEnd() {
        mFoucsView.setVisibility(INVISIBLE);
    }

    public interface CameraViewListener {

        //void captureSuccess(Bitmap bitmap);
        void onCameraError();

        void recordSuccess(String url);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        //Log.d(TAG, "autoFoucs=" + autoFoucs + ",SELECTED_CAMERA=" + SELECTED_CAMERA + ",isPlay=" + isPlay);
        if (!autoFoucs && event.getAction() == MotionEvent.ACTION_DOWN && SELECTED_CAMERA == CAMERA_POST_POSITION && !isPlay) {
            onFocusBegin(event.getX(), event.getY());
        }
        return super.onTouchEvent(event);
    }

    public void cancelAudio() {
        AudioUtil.setAudioManage(mContext);
    }
}
