package com.google.android.cameraview;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Direction the camera faces relative to device screen.
 */
@IntDef({Constants.FACING_BACK, Constants.FACING_FRONT})
@Retention(RetentionPolicy.SOURCE)
public @interface Facing {
}