package com.google.android.cameraview;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;



/**
 * The mode for for the camera device's flash control
 */
@IntDef({Constants.FLASH_OFF, Constants.FLASH_ON, Constants.FLASH_TORCH, Constants.FLASH_AUTO, Constants.FLASH_RED_EYE})
@Retention(RetentionPolicy.SOURCE)
public @interface Flash {
}