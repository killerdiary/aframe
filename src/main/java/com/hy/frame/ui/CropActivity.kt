package com.hy.frame.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView

import com.hy.frame.R
import com.hy.frame.common.BaseActivity
import com.hy.frame.util.CameraDocument
import com.hy.frame.util.ImageUtil
import com.hy.frame.util.MyLog
import com.hy.frame.widget.NavView
import com.hy.frame.widget.TintImageView
import com.yalantis.ucrop.util.BitmapLoadUtils
import com.yalantis.ucrop.view.CropImageView
import com.yalantis.ucrop.view.GestureCropImageView
import com.yalantis.ucrop.view.OverlayView
import com.yalantis.ucrop.view.UCropView

import java.io.OutputStream

/**
 * com.hy.frame.ui
 * author HeYan
 * time 2016/8/23 11:09
 */
class CropActivity : BaseActivity() {

    override fun initSingleLayoutId(): Int = 0
    override fun isTranslucentStatus(): Boolean = true
    override fun isPermissionDenied(): Boolean = false

    private var imgCrop: GestureCropImageView? = null
    private var vOverlay: OverlayView? = null
    private var navTurnLeft: NavView? = null
    private var navTurnRight: NavView? = null
    private var outputUri: Uri? = null
    private var quality: Int = 0
    private var isHor: Boolean = false

    override fun initLayoutId(): Int = R.layout.act_crop

    override fun initView() {
        val vUCrop = findViewById<UCropView>(R.id.crop_vUCrop)
        imgCrop = vUCrop?.cropImageView
        vOverlay = vUCrop?.overlayView
        navTurnLeft = setOnClickListener(R.id.crop_navTurnLeft)
        navTurnRight = setOnClickListener(R.id.crop_navTurnRight)
        setOnClickListener<View>(R.id.crop_txtConfirm)
        setOnClickListener<View>(R.id.crop_txtCancel)
    }

    override fun initData() {
        setHeaderLeft(R.mipmap.ic_back)
        setTitle(R.string.crop)
        val txtTitle = headerTitle as TextView
        val color = txtTitle.currentTextColor
        if (headerLeft is TintImageView) {
            val imgLeft = headerLeft as TintImageView
            imgLeft.setColorFilter(color)
        }
        initImageData()
    }

    override fun requestData() {}

    override fun updateUI() {}

    private var aspectX: Int = 0
    private var aspectY: Int = 0

    override fun onViewClick(v: View) {
        if (v.id == R.id.crop_txtCancel) {
            onLeftClick()
        } else if (v.id == R.id.crop_txtConfirm) {
            onRightClick()
        } else if (v.id == R.id.crop_navTurnLeft || v.id == R.id.crop_navTurnRight) {
            if (v.id == R.id.crop_navTurnLeft) {
                imgCrop!!.postRotate(-90f)
            } else if (v.id == R.id.crop_navTurnRight) {
                imgCrop!!.postRotate(90f)
            }
            if (aspectX == 0 || aspectY == 0) {
                if (!isHor) {
                    imgCrop!!.targetAspectRatio = CropImageView.SOURCE_IMAGE_ASPECT_RATIO_F
                } else {
                    imgCrop!!.targetAspectRatio = CropImageView.SOURCE_IMAGE_ASPECT_RATIO
                }
                isHor = !isHor
                imgCrop!!.setImageToWrapCropBounds()
            }
        }
    }

    private var isBigImage: Boolean = false
    private var maxWidth = 0
    private var maxHeight = 0

    private fun initImageData() {
        val bundle = bundle
        val inputUri = bundle!!.getParcelable<Uri>(EXTRA_INPUT_URI)
        outputUri = bundle.getParcelable<Uri>(EXTRA_OUTPUT_URI)
        if (inputUri == null || outputUri == null) {
            MyLog.e(javaClass, "地址未指定")
            finish()
            return
        }
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(CameraDocument.getPath(context!!, inputUri), options)
        if (options.outHeight >= 3000 && options.outHeight >= options.outWidth * 3) {
            isBigImage = true
            navTurnLeft!!.visibility = View.GONE
            navTurnRight!!.visibility = View.GONE
        }
        try {
            imgCrop!!.setImageUri(inputUri)
        } catch (e: Exception) {
            MyLog.e(javaClass, "图片地址错误")
            finish()
            return
        }

        aspectX = bundle.getInt(EXTRA_ASPECT_X, 0)
        aspectY = bundle.getInt(EXTRA_ASPECT_Y, 0)
        val unit = bundle.getInt(EXTRA_ASPECT_UNIT, 0)
        if (aspectX > 0 && aspectY > 0) {
            imgCrop!!.targetAspectRatio = aspectX / aspectY.toFloat()
        } else {
            imgCrop!!.targetAspectRatio = CropImageView.SOURCE_IMAGE_ASPECT_RATIO
        }
        maxWidth = aspectX * unit
        maxHeight = aspectY * unit
        imgCrop!!.setMaxResultImageSizeX(aspectX * unit)
        imgCrop!!.setMaxResultImageSizeY(aspectY * unit)
        imgCrop!!.isScaleEnabled = true
        imgCrop!!.isRotateEnabled = false
        quality = bundle.getInt(EXTRA_QUALITY, DEFAULT_COMPRESS_QUALITY)
        processOptions(bundle)
    }

    private fun processOptions(optionsBundle: Bundle?) {
        if (optionsBundle != null) {

            //Crop image view options
            imgCrop!!.maxBitmapSize = CropImageView.DEFAULT_MAX_BITMAP_SIZE
            imgCrop!!.setMaxScaleMultiplier(CropImageView.DEFAULT_MAX_SCALE_MULTIPLIER)
            imgCrop!!.setImageToWrapCropBoundsAnimDuration(CropImageView.DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION.toLong())

            // Overlay view options
            vOverlay!!.setDimmedColor(resources.getColor(R.color.ucrop_color_default_dimmed))
            vOverlay!!.setOvalDimmedLayer(OverlayView.DEFAULT_OVAL_DIMMED_LAYER)
            //vOverlay.setOvalDimmedLayer( OverlayView.DEFAULT_OVAL_DIMMED_LAYER);

            vOverlay!!.setShowCropFrame(OverlayView.DEFAULT_SHOW_CROP_FRAME)
            vOverlay!!.setCropFrameColor(resources.getColor(R.color.crop_red_tran))
            vOverlay!!.setCropFrameStrokeWidth(resources.getDimensionPixelSize(R.dimen.ucrop_default_crop_frame_stoke_width))

            vOverlay!!.setShowCropGrid(OverlayView.DEFAULT_SHOW_CROP_GRID)
            vOverlay!!.setCropGridRowCount(OverlayView.DEFAULT_CROP_GRID_ROW_COUNT)
            vOverlay!!.setCropGridColumnCount(OverlayView.DEFAULT_CROP_GRID_COLUMN_COUNT)
            vOverlay!!.setCropGridColor(resources.getColor(R.color.ucrop_color_default_crop_grid))
            vOverlay!!.setCropGridStrokeWidth(resources.getDimensionPixelSize(R.dimen.ucrop_default_crop_grid_stoke_width))
        }
    }

    private fun setAngleText(angle: Float) {
        //        if (mTextViewRotateAngle != null) {
        //            mTextViewRotateAngle.setText(String.format("%.1f°", angle));
        //        }
    }

    private fun setScaleText(scale: Float) {
        //        if (mTextViewScalePercent != null) {
        //            mTextViewScalePercent.setText(String.format("%d%%", (int) (scale * 100)));
        //        }
    }

    override fun onRightClick() {
        if (isBigImage) {
            if (ImageUtil.compressByPath(CameraDocument.getPath(context!!, imgCrop!!.imageUri!!)!!, outputUri!!.path, quality, maxWidth, maxHeight)) {
                setResult(RESULT_OK, Intent().setData(outputUri))
            }
            finish()
        } else {
            var outputStream: OutputStream? = null
            try {
                val croppedBitmap = imgCrop!!.cropImage()
                if (croppedBitmap != null) {
                    outputStream = contentResolver.openOutputStream(outputUri!!)
                    croppedBitmap.compress(DEFAULT_COMPRESS_FORMAT, quality, outputStream)
                    croppedBitmap.recycle()
                    setResult(RESULT_OK, Intent().setData(outputUri))
                    finish()
                } else {
                    MyLog.e(javaClass, "剪切失败")
                    finish()
                }
            } catch (e: Exception) {
                finish()
            } finally {
                BitmapLoadUtils.close(outputStream)
            }
        }
    }


    override fun onStop() {
        super.onStop()
        if (imgCrop != null) {
            imgCrop!!.cancelAllAnimations()
        }
    }

    companion object {
        val DEFAULT_COMPRESS_QUALITY = 90
        val DEFAULT_COMPRESS_FORMAT: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
        val EXTRA_INPUT_URI = "INPUT_URI"
        val EXTRA_OUTPUT_URI = "OUTPUT_URI"
        val EXTRA_ASPECT_X = "ASPECT_X"
        val EXTRA_ASPECT_Y = "ASPECT_Y"
        val EXTRA_ASPECT_UNIT = "ASPECT_UNIT"
        val EXTRA_QUALITY = "QUALITY"
    }
}
