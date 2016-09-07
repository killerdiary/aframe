package com.yalantis.ucrop.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.hy.frame.R;

/**
 * 剪切View
 */
public class UCropView extends FrameLayout {

    private GestureCropImageView mGestureCropImageView;
    private OverlayView mViewOverlay;

    public UCropView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UCropView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        int padding = getResources().getDimensionPixelSize(R.dimen.margin_normal);
        mGestureCropImageView = new GestureCropImageView(context);
        mGestureCropImageView.setPadding(padding,padding,padding,padding);
        addView(mGestureCropImageView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mViewOverlay = new OverlayView(context);
        mViewOverlay.setPadding(padding,padding,padding,padding);
        addView(mViewOverlay, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mGestureCropImageView.setCropBoundsChangeListener(new CropImageView.CropBoundsChangeListener() {
            @Override
            public void onCropBoundsChangedRotate(float cropRatio) {
                if (mViewOverlay != null) {
                    mViewOverlay.setTargetAspectRatio(cropRatio);
                    mViewOverlay.postInvalidate();
                }
            }
        });
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.UCropView);
        mViewOverlay.processStyledAttributes(a);
        mGestureCropImageView.processStyledAttributes(a);
        a.recycle();
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @NonNull
    public GestureCropImageView getCropImageView() {
        return mGestureCropImageView;
    }

    @NonNull
    public OverlayView getOverlayView() {
        return mViewOverlay;
    }

}