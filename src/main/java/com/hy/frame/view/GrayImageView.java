package com.hy.frame.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.AttributeSet;

/**
 * 可变灰色图
 * author HeYan
 * time 2015/12/31 10:06
 */
public class GrayImageView extends android.support.v7.widget.AppCompatImageView {
    public GrayImageView(Context context) {
        super(context);
    }

    public GrayImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GrayImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private boolean gray;

    public boolean isGray() {
        return gray;
    }

    public void setGray(boolean gray) {
        this.gray = gray;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        if (bm == null) return;
        if (gray) {
            int width = bm.getWidth();
            int height = bm.getHeight();
            Bitmap grayBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            Canvas c = new Canvas(grayBmp);
            Paint paint = new Paint();
            ColorMatrix cm = new ColorMatrix();
            cm.setSaturation(0);
            ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
            paint.setColorFilter(f);
            c.drawBitmap(bm, 0, 0, paint);
            super.setImageBitmap(grayBmp);
        } else
            super.setImageBitmap(bm);
    }
}
