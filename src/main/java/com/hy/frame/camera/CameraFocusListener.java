package com.hy.frame.camera;

/**
 * CameraFocusListener
 *
 * @author HeYan
 * @time 2017/4/28 15:33
 */
public interface CameraFocusListener {
    void onFocusBegin(float x, float y);

    void onFocusEnd();
}
