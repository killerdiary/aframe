package com.hy.frame.camera;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * ImageUtil
 *
 * @author HeYan
 * @time 2017/4/28 15:36
 */
public class ImageUtil {
    public static Bitmap getRotateBitmap(Bitmap bitmap, float rotateDegree) {
        Matrix matrix = new Matrix();
        matrix.setRotate(rotateDegree);
        Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        return rotateBitmap;
    }
}
