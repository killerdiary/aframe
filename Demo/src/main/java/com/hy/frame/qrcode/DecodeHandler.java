package com.hy.frame.qrcode;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.hy.frame.util.MyLog;

import java.util.Hashtable;

final class DecodeHandler extends Handler {

    private static final String TAG = DecodeHandler.class.getSimpleName();

    private final Handler mHandler;
    private final MultiFormatReader multiFormatReader;

    DecodeHandler(Handler mHandler, Hashtable<DecodeHintType, Object> hints) {
        multiFormatReader = new MultiFormatReader();
        multiFormatReader.setHints(hints);
        this.mHandler = mHandler;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case CameraScanView.FLAG_DECODE:
                MyLog.INSTANCE.d(getClass(), "Got decode message");
                decode((Bitmap) msg.obj);
                break;
            case CameraScanView.FLAG_QUIT:
                Looper looper = Looper.myLooper();
                if (looper != null)
                    looper.quit();
                break;
        }
    }

    /**
     * Decode the data within the viewfinder rectangle, and time how long it
     * took. For efficiency, reuse the same reader objects from one decode to
     * the next.
     */
    private void decode(Bitmap bmp) {
        MyLog.INSTANCE.e("decode");
        MultiFormatReader multiFormatReader = new MultiFormatReader();
        BitmapLuminanceSource source = new BitmapLuminanceSource(bmp);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        try {
            Result rawResult = multiFormatReader.decode(bitmap);
            if (rawResult != null) {
                Message message = Message.obtain(mHandler, CameraScanView.FLAG_DECODE_SUCCESS, rawResult.getText());
                message.sendToTarget();
                bmp.recycle();
                return;
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        bmp.recycle();
        Message message = Message.obtain(mHandler, CameraScanView.FLAG_DECODE_FAIL);
        message.sendToTarget();
    }
}
