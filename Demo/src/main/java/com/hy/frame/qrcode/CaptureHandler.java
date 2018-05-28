package com.hy.frame.qrcode;

import android.os.Handler;
import android.os.Message;

import com.google.zxing.BarcodeFormat;

import java.util.Vector;


/**
 * This class handles all the messaging which comprises the state machine for capture.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class CaptureHandler extends Handler {

    private static final String TAG = CaptureHandler.class.getSimpleName();
    private final Handler mHandler;
    private final DecodeThread decodeThread;
    private State state;

    private enum State {
        PREVIEW, SUCCESS, DONE
    }

    public CaptureHandler(Handler mHandler, Vector<BarcodeFormat> decodeFormats, String characterSet) {
        this.mHandler = mHandler;
        decodeThread = new DecodeThread(mHandler, decodeFormats, characterSet, null);
        decodeThread.start();
        state = State.PREVIEW;
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
//            case QrcodeUtil.auto_focus:
//                if (state == State.PREVIEW) {
//                    CameraManager.get().requestAutoFocus(this, QrcodeUtil.auto_focus);
//                }
//                break;
//            case QrcodeUtil.restart_preview:
//                Log.d(TAG, "Got restart preview message");
//                restartPreviewAndDecode();
//                break;
//            case QrcodeUtil.decode_succeeded:
//                Log.d(TAG, "Got decode succeeded message");
//                state = State.SUCCESS;
//                Bundle bundle = message.getData();
//                Bitmap barcode = bundle == null ? null : (Bitmap) bundle.getParcelable(DecodeThread.BARCODE_BITMAP);
//                activity.handleDecode((Result) message.obj, barcode);
//                break;
//            case QrcodeUtil.decode_failed:
//                // We're decoding as fast as possible, so when one decode fails, start another.
//                state = State.PREVIEW;
//                CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), QrcodeUtil.decode);
//                break;
//            case QrcodeUtil.return_scan_result:
//                Log.d(TAG, "Got return scan result message");
//                activity.setResult(Activity.RESULT_OK, (Intent) message.obj);
//                activity.finish();
//                break;
//            case QrcodeUtil.launch_product_query:
//                Log.d(TAG, "Got product query message");
//                String url = (String) message.obj;
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//                activity.startActivity(intent);
//                break;
        }
    }

    public void quitSynchronously() {
//        state = State.DONE;
//        CameraManager.get().stopPreview();
//        Message quit = Message.obtain(decodeThread.getHandler(), QrcodeUtil.quit);
//        quit.sendToTarget();
//        try {
//            decodeThread.join();
//        } catch (InterruptedException e) {
//            // continue
//        }
//        // Be absolutely sure we don't send any queued up messages
//        removeMessages(QrcodeUtil.decode_succeeded);
//        removeMessages(QrcodeUtil.decode_failed);
    }

    private void restartPreviewAndDecode() {
        if (state == State.SUCCESS) {
            state = State.PREVIEW;

        }
    }
}
