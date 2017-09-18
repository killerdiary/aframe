package com.google.android.cameraview;

import android.support.annotation.IntDef;

/**
 * The mode for for the camera device's flash control
 */
@IntDef({Constants.FLASH_OFF, Constants.FLASH_ON, Constants.FLASH_TORCH, Constants.FLASH_AUTO, Constants.FLASH_RED_EYE})
public @interface Flash {
}