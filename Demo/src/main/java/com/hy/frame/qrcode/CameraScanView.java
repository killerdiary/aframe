package com.hy.frame.qrcode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.google.android.cameraview.CameraView;
import com.hy.frame.util.MyLog;

/**
 * 二维码扫描
 *
 * @author HeYan
 * @time 2017/8/18 11:17
 */
public class CameraScanView extends CameraView {

    private ScanView scanView;
    private DecodeThread decodeThread;
    private Handler mHandler;
    private boolean isScanReady;
    private int flag = FLAG_DECODE_NORMAL;
    public final static int FLAG_DECODE_NORMAL = 0;
    public final static int FLAG_DECODE_READY = 1;
    public final static int FLAG_DECODE_DECODING = 2;
    public final static int FLAG_DECODE_SUCCESS = 3;
    public final static int FLAG_DECODE_FAIL = 4;
    public final static int FLAG_DECODE = 5;
    public final static int FLAG_QUIT = 6;

    //    public static final int decode = 102;
//    public static final int decode_failed = 103;
//    public static final int decode_succeeded = 104;
//    public static final int quit = 108;

    public CameraScanView(Context context) {
        this(context, null);
    }

    public CameraScanView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraScanView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        scanView = new ScanView(context, null);
        scanView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(scanView);
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    if (listener == null) return;
                    switch (msg.what) {
                        case FLAG_DECODE_SUCCESS:
                            flag = FLAG_DECODE_SUCCESS;
                            MyLog.INSTANCE.d(CameraScanView.this.getClass(), "Got decode succeeded message");
                            listener.onScanSuccess((String) msg.obj);
                            break;
                        case FLAG_DECODE_FAIL:
                            flag = FLAG_DECODE_FAIL;
                            MyLog.INSTANCE.d(CameraScanView.this.getClass(), "Got decode failed message");
                            needPreviewCallback(200L);
                            break;
                    }
                }
            };
            addCallback(new Callback() {
                @Override
                public void onCameraOpened(CameraView cameraView) {
                    MyLog.INSTANCE.e(CameraScanView.this.getClass(), "onCameraOpened");
                    if (listener != null && (!isScanReady || flag != FLAG_DECODE_READY)) {
                        isScanReady = true;
                        needPreviewCallback(3 * 1000L);
                    }
                }

                @Override
                public void onPreviewFrame(CameraView cameraView, Bitmap bmp) {
                    if (bmp == null) {
                        needPreviewCallback(3 * 1000L);
                        return;
                    }
                    try {
                        Rect rect = scanView.getFramingRect();
                        Bitmap bitmap = Bitmap.createBitmap(bmp, rect.left, rect.top, rect.width(), rect.height());
                        bmp.recycle();
                        Message msg = new Message();
                        msg.what = FLAG_DECODE;
                        msg.obj = bitmap;
                        flag = FLAG_DECODE_DECODING;
                        decodeThread.getHandler().sendMessage(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void needPreviewCallback() {
        MyLog.INSTANCE.e(getClass(), "needPreviewCallback");
        flag = FLAG_DECODE_READY;
        super.needPreviewCallback();
    }

    public void needPreviewCallback(long delayMillis) {
        if (delayMillis > 0) {
            postDelayed(this::needPreviewCallback, delayMillis);
            return;
        }
        needPreviewCallback();
    }

    private ScanListener listener;

    public void setListener(ScanListener listener) {
        MyLog.INSTANCE.e(getClass(), "setListener");
        this.listener = listener;
        if (decodeThread == null) {
            decodeThread = new DecodeThread(mHandler, null, null, null);
            decodeThread.start();
        }
        if (!isScanReady && isCameraOpened()) {
            isScanReady = true;
            needPreviewCallback(200L);
        }
    }

    public void onDestroy() {
        if (decodeThread != null)
            decodeThread.getHandler().sendEmptyMessage(FLAG_QUIT);
    }

    public interface ScanListener {
        void onScanSuccess(String source);
    }
}
