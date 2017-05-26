package com.hy.frame.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hy.frame.R;
import com.hy.frame.common.BaseActivity;
import com.hy.frame.util.CameraDocument;
import com.hy.frame.util.ImageUtil;
import com.hy.frame.util.MyLog;
import com.hy.frame.view.NavView;
import com.hy.frame.view.TintImageView;
import com.yalantis.ucrop.util.BitmapLoadUtils;
import com.yalantis.ucrop.view.CropImageView;
import com.yalantis.ucrop.view.GestureCropImageView;
import com.yalantis.ucrop.view.OverlayView;
import com.yalantis.ucrop.view.UCropView;

import java.io.OutputStream;

/**
 * com.hy.frame.ui
 * author HeYan
 * time 2016/8/23 11:09
 */
public class CropActivity extends BaseActivity {
    public static final int DEFAULT_COMPRESS_QUALITY = 90;
    public static final Bitmap.CompressFormat DEFAULT_COMPRESS_FORMAT = Bitmap.CompressFormat.JPEG;
    public static final String EXTRA_INPUT_URI = "INPUT_URI";
    public static final String EXTRA_OUTPUT_URI = "OUTPUT_URI";
    public static final String EXTRA_ASPECT_X = "ASPECT_X";
    public static final String EXTRA_ASPECT_Y = "ASPECT_Y";
    public static final String EXTRA_ASPECT_UNIT = "ASPECT_UNIT";
    public static final String EXTRA_QUALITY = "QUALITY";
    private GestureCropImageView imgCrop;
    private OverlayView vOverlay;
    private NavView navTurnLeft, navTurnRight;
    private Uri outputUri;
    private int quality;
    private boolean isHor;

    @Override
    public int initLayoutId() {
        return R.layout.act_crop;
    }

    @Override
    public void initView() {
        UCropView vUCrop = getView(R.id.crop_vUCrop);
        imgCrop = vUCrop.getCropImageView();
        vOverlay = vUCrop.getOverlayView();
        navTurnLeft = getViewAndClick(R.id.crop_navTurnLeft);
        navTurnRight = getViewAndClick(R.id.crop_navTurnRight);
        setOnClickListener(R.id.crop_txtConfirm);
        setOnClickListener(R.id.crop_txtCancel);
    }

    @Override
    public void initData() {
        setHeaderLeft(R.mipmap.ico_back);
        setTitle(R.string.crop);
        TextView txtTitle = (TextView) getHeaderTitle();
        int color = txtTitle.getCurrentTextColor();
        if (getHeaderLeft() instanceof TintImageView) {
            TintImageView imgLeft = (TintImageView) getHeaderLeft();
            imgLeft.setColorFilter(color);
        }
        initImageData();
    }

    @Override
    public void requestData() {

    }

    @Override
    public void updateUI() {

    }

    private int aspectX, aspectY;

    @Override
    public void onViewClick(View v) {
        if (v.getId() == R.id.crop_txtCancel) {
            onLeftClick();
        } else if (v.getId() == R.id.crop_txtConfirm) {
            onRightClick();
        } else if (v.getId() == R.id.crop_navTurnLeft || v.getId() == R.id.crop_navTurnRight) {
            if (v.getId() == R.id.crop_navTurnLeft) {
                imgCrop.postRotate(-90);
            } else if (v.getId() == R.id.crop_navTurnRight) {
                imgCrop.postRotate(90);
            }
            if (aspectX == 0 || aspectY == 0) {
                if (!isHor) {
                    imgCrop.setTargetAspectRatio(CropImageView.SOURCE_IMAGE_ASPECT_RATIO_F);
                } else {
                    imgCrop.setTargetAspectRatio(CropImageView.SOURCE_IMAGE_ASPECT_RATIO);
                }
                isHor = !isHor;
                imgCrop.setImageToWrapCropBounds();
            }
        }
    }

    private boolean isBigImage;
    private int maxWidth = 0, maxHeight = 0;

    private void initImageData() {
        Bundle bundle = getBundle();
        Uri inputUri = bundle.getParcelable(EXTRA_INPUT_URI);
        outputUri = bundle.getParcelable(EXTRA_OUTPUT_URI);
        if (inputUri == null || outputUri == null) {
            MyLog.INSTANCE.e(getClass(), "地址未指定");
            finish();
            return;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(CameraDocument.getPath(context, inputUri), options);
        if (options.outHeight >= 3000 && options.outHeight >= options.outWidth * 3) {
            isBigImage = true;
            navTurnLeft.setVisibility(View.GONE);
            navTurnRight.setVisibility(View.GONE);
        }
        try {
            imgCrop.setImageUri(inputUri);
        } catch (Exception e) {
            MyLog.INSTANCE.e(getClass(), "图片地址错误");
            finish();
            return;
        }

        aspectX = bundle.getInt(EXTRA_ASPECT_X, 0);
        aspectY = bundle.getInt(EXTRA_ASPECT_Y, 0);
        int unit = bundle.getInt(EXTRA_ASPECT_UNIT, 0);
        if (aspectX > 0 && aspectY > 0) {
            imgCrop.setTargetAspectRatio(aspectX / (float) aspectY);
        } else {
            imgCrop.setTargetAspectRatio(CropImageView.SOURCE_IMAGE_ASPECT_RATIO);
        }
        maxWidth = aspectX * unit;
        maxHeight = aspectY * unit;
        imgCrop.setMaxResultImageSizeX(aspectX * unit);
        imgCrop.setMaxResultImageSizeY(aspectY * unit);
        imgCrop.setScaleEnabled(true);
        imgCrop.setRotateEnabled(false);
        quality = bundle.getInt(EXTRA_QUALITY, DEFAULT_COMPRESS_QUALITY);
        processOptions(bundle);
    }

    private void processOptions(Bundle optionsBundle) {
        if (optionsBundle != null) {

            //Crop image view options
            imgCrop.setMaxBitmapSize(CropImageView.DEFAULT_MAX_BITMAP_SIZE);
            imgCrop.setMaxScaleMultiplier(CropImageView.DEFAULT_MAX_SCALE_MULTIPLIER);
            imgCrop.setImageToWrapCropBoundsAnimDuration(CropImageView.DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION);

            // Overlay view options
            vOverlay.setDimmedColor(getResources().getColor(R.color.ucrop_color_default_dimmed));
            vOverlay.setOvalDimmedLayer(OverlayView.DEFAULT_OVAL_DIMMED_LAYER);
            //vOverlay.setOvalDimmedLayer( OverlayView.DEFAULT_OVAL_DIMMED_LAYER);

            vOverlay.setShowCropFrame(OverlayView.DEFAULT_SHOW_CROP_FRAME);
            vOverlay.setCropFrameColor(getResources().getColor(R.color.crop_red_tran));
            vOverlay.setCropFrameStrokeWidth(getResources().getDimensionPixelSize(R.dimen.ucrop_default_crop_frame_stoke_width));

            vOverlay.setShowCropGrid(OverlayView.DEFAULT_SHOW_CROP_GRID);
            vOverlay.setCropGridRowCount(OverlayView.DEFAULT_CROP_GRID_ROW_COUNT);
            vOverlay.setCropGridColumnCount(OverlayView.DEFAULT_CROP_GRID_COLUMN_COUNT);
            vOverlay.setCropGridColor(getResources().getColor(R.color.ucrop_color_default_crop_grid));
            vOverlay.setCropGridStrokeWidth(getResources().getDimensionPixelSize(R.dimen.ucrop_default_crop_grid_stoke_width));
        }
    }

    private void setAngleText(float angle) {
//        if (mTextViewRotateAngle != null) {
//            mTextViewRotateAngle.setText(String.format("%.1f°", angle));
//        }
    }

    private void setScaleText(float scale) {
//        if (mTextViewScalePercent != null) {
//            mTextViewScalePercent.setText(String.format("%d%%", (int) (scale * 100)));
//        }
    }

    @Override
    public void onRightClick() {
        if (isBigImage) {
            if (ImageUtil.compressByPath(CameraDocument.getPath(context, imgCrop.getImageUri()), outputUri.getPath(), quality, maxWidth, maxHeight)) {
                setResult(RESULT_OK, new Intent().setData(outputUri));
            }
            finish();
        } else {
            OutputStream outputStream = null;
            try {
                final Bitmap croppedBitmap = imgCrop.cropImage();
                if (croppedBitmap != null) {
                    outputStream = getContentResolver().openOutputStream(outputUri);
                    croppedBitmap.compress(DEFAULT_COMPRESS_FORMAT, quality, outputStream);
                    croppedBitmap.recycle();
                    setResult(RESULT_OK, new Intent().setData(outputUri));
                    finish();
                } else {
                    MyLog.INSTANCE.e(getClass(), "剪切失败");
                    finish();
                }
            } catch (Exception e) {
                finish();
            } finally {
                BitmapLoadUtils.close(outputStream);
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (imgCrop != null) {
            imgCrop.cancelAllAnimations();
        }
    }
}
