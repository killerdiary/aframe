package com.hy.frame.qrcode;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.hy.app.R;

public final class ScanView extends View {

    private static final long ANIMATION_DELAY = 10L;
    private final Paint framePaint;//半透明层
    private final Paint borderPaint;//边框
    private Rect framingRect;
    private int scannerMove;
    private boolean moveUp;

    public ScanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Resources resources = context.getResources();
        framePaint = new Paint();
        framePaint.setColor(resources.getColor(R.color.viewfinder_frame));
        borderPaint = new Paint();
        borderPaint.setColor(resources.getColor(R.color.viewfinder_border));
        scannerMove = 0;
    }

    public Rect getFramingRect() {
        return framingRect;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (framingRect == null && getWidth() > 0 && getHeight() > 0) {
            int width = (int) (getWidth() * 0.69);
            //int height = width;
            int leftOffset = (getWidth() - width) / 2;
            int topOffset = (getHeight() - width) / 2;
            framingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + width);
            scannerMove = framingRect.height() / 2 - 20;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (framingRect == null || canvas.getWidth() < framingRect.width() || canvas.getHeight() < framingRect.height()) return;
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        // Draw the exterior (i.e. outside the framing rect) darkened
        canvas.drawRect(0, 0, width, framingRect.top, framePaint);
        canvas.drawRect(0, framingRect.top, framingRect.left, framingRect.bottom + 1, framePaint);
        canvas.drawRect(framingRect.right + 1, framingRect.top, width, framingRect.bottom + 1, framePaint);
        canvas.drawRect(0, framingRect.bottom + 1, width, height, framePaint);
//        // Draw a two pixel solid black border inside the framing rect
        int borderWidth = 5;
//        // 左上角
        canvas.drawRect(framingRect.left - borderWidth, framingRect.top - borderWidth, framingRect.left + 80, framingRect.top + borderWidth, borderPaint);
        canvas.drawRect(framingRect.left - borderWidth, framingRect.top - borderWidth, framingRect.left + borderWidth, framingRect.top + 80, borderPaint);
//        // 右上角
        canvas.drawRect(framingRect.right - 80, framingRect.top - borderWidth, framingRect.right + borderWidth, framingRect.top + borderWidth, borderPaint);
        canvas.drawRect(framingRect.right - borderWidth, framingRect.top - borderWidth, framingRect.right + borderWidth, framingRect.top + 80, borderPaint);
//        // 左下角
        canvas.drawRect(framingRect.left - borderWidth, framingRect.bottom - borderWidth, framingRect.left + 80, framingRect.bottom + borderWidth, borderPaint);
        canvas.drawRect(framingRect.left - borderWidth, framingRect.bottom - 80, framingRect.left + borderWidth, framingRect.bottom + borderWidth, borderPaint);
//        // 右下角
        canvas.drawRect(framingRect.right - 80, framingRect.bottom - borderWidth, framingRect.right + borderWidth, framingRect.bottom + borderWidth, borderPaint);
        canvas.drawRect(framingRect.right - borderWidth, framingRect.bottom - 80, framingRect.right + borderWidth, framingRect.bottom + borderWidth, borderPaint);
        if (moveUp) {
            scannerMove -= 5;
            if (scannerMove <= 0)
                moveUp = false;
        } else {
            scannerMove += 5;
            if (framingRect.height() - scannerMove < 20)
                moveUp = true;
        }
        try {
            Bitmap bmp = getScannerBmp(framingRect.width() - 20);
            if (bmp != null)
                canvas.drawBitmap(bmp, framingRect.left + 10, framingRect.top + scannerMove, borderPaint);
        } catch (Exception e) {
            e.printStackTrace();
        }
        postInvalidateDelayed(ANIMATION_DELAY, framingRect.left, framingRect.top, framingRect.right, framingRect.bottom);
    }

    private Bitmap scannerBmp;

    private Bitmap getScannerBmp(int width) {
        if (scannerBmp != null) return scannerBmp;
        try {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.def_scan_bg);
            Matrix matrix = new Matrix();
            float scale = (float) width / bmp.getWidth();
            matrix.postScale(scale, scale); //长和宽放大缩小的比例
            scannerBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scannerBmp;
    }
}