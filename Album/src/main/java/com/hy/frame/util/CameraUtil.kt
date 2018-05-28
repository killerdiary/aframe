package com.hy.frame.util

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.app.Fragment
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import java.io.File


/**
 * 拍照工具类 onRequestPermissionsResult
 * author HeYan
 * time 2015/12/29 16:57
 */
open class CameraUtil {
    protected val act: Activity
    protected val fragment: Fragment?
    protected val context: Context
        get() = act
    protected val listener: CameraDealListener
    private var videoListener: CamerVideoListener? = null
    private var imageUri: Uri? = null
    private var cacheUri: Uri? = null

    constructor(act: Activity, listener: CameraDealListener) {
        this.fragment = null
        this.act = act
        this.listener = listener
    }

    constructor(fragment: Fragment, listener: CameraDealListener) {
        this.fragment = fragment
        this.act = fragment.activity!!
        this.listener = listener
    }

    fun setVideoListener(videoListener: CamerVideoListener) {
        this.videoListener = videoListener
    }

    private val newCacheUri: Uri?
        get() {
            val path = HyUtil.getCachePathCrop(context)
            if (path.isNullOrBlank()) return null
            val time = System.currentTimeMillis()
            val imagePath = path + File.separator + "pic" + time + ".jpg"
            return Uri.parse("file://$imagePath")
        }

    fun onDlgCameraClick() {
        if (fragment != null && !PermissionUtil.requestPhotographPermission(fragment)) return
        if (fragment == null && !PermissionUtil.requestPhotographPermission(act)) return
        try {
            PictureSelector.create(act).openCamera(PictureMimeType.ofImage()).compress(true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var multiple: Int = 1

    open fun onDlgPhotoClick(multiple: Int = 1) {
        this.multiple = multiple
        if (fragment != null && !PermissionUtil.requesStoragetPermission(fragment)) return
        if (fragment == null && !PermissionUtil.requesStoragetPermission(act)) return
        try {
            if (multiple <= 1) {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                startActForResult(intent, FLAG_CHOOICE_IMAGE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var quality: Int = 1
    private var seconds: Int = 60

    fun onDlgVideoClick(quality: Int = 1, seconds: Int = 60) {
        this.quality = quality
        this.seconds = seconds
        if (fragment != null && !PermissionUtil.requestVideoRecordPermission(fragment)) return
        if (fragment == null && !PermissionUtil.requestVideoRecordPermission(act)) return
        try {
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            val values = ContentValues()
            val contentUri = context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
                    ?: return
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, quality)
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, seconds)
            intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 480 * 800)
            startActForResult(intent, FLAG_TAKE_VIDEO)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cropImageUri(aspectX: Int, aspectY: Int, unit: Int) {
        cropImageUri(cacheUri, aspectX, aspectY, unit)
    }

    fun cropImageUri(cacheUri: Uri?, aspectX: Int, aspectY: Int, unit: Int) {
        imageUri = newCacheUri
        if (cacheUri == null || imageUri == null) {
            MyLog.e(javaClass, "地址未初始化")
            return
        }
//        val intent = Intent("com.android.camera.action.CROP")
//        intent.setDataAndType(cacheUri, "image/*")
//        intent.putExtra("scaleUpIfNeeded", true)//黑边
//        intent.putExtra("crop", "true")
//        intent.putExtra("aspectX", aspectX)
//        intent.putExtra("aspectY", aspectY)
//        intent.putExtra("outputX", aspectX * unit)
//        intent.putExtra("outputY", aspectX * unit)
//        intent.putExtra("scale", true)
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
//        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
//        intent.putExtra("noFaceDetection", true) // no face detection
//        startActForResult(intent, FLAG_IMAGE_CUT)

//        val intent = Intent(context, CropActivity::class.java)
//        val bundle = Bundle()
//        bundle.putInt(CropActivity.EXTRA_ASPECT_X, aspectX)
//        bundle.putInt(CropActivity.EXTRA_ASPECT_Y, aspectY)
//        bundle.putInt(CropActivity.EXTRA_ASPECT_UNIT, unit)
//        bundle.putParcelable(CropActivity.EXTRA_INPUT_URI, cacheUri)
//        bundle.putParcelable(CropActivity.EXTRA_OUTPUT_URI, imageUri)
//        intent.putExtra(BaseActivity.BUNDLE, bundle)
//        try {
//            startActForResult(intent, FLAG_IMAGE_CUT)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
    }

    protected fun startActForResult(intent: Intent, requestCode: Int) {
        if (fragment != null)
            fragment.startActivityForResult(intent, requestCode)
        else
            act.startActivityForResult(intent, requestCode)
    }

//    public Uri getCacheUri() {
//        String path = MyShare.get(getContext()).getString(URI_CACHE);
//        return path == null ? null : Uri.parse(path);
//    }
//
//    public Uri getImageUri() {
//        String path = MyShare.get(getContext()).getString(URI_IMAGE);
//        return path == null ? null : Uri.parse(path);
//    }

    open fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val f: File
            val path: String?
            try {
                when (requestCode) {
                    FLAG_TAKE_PICTURE -> {
                        if (data != null && data.data != null) {
                            cacheUri = data.data
                            path = CameraDocument.getPath(context, cacheUri!!)
                        } else {
                            if (cacheUri == null) return
                            path = CameraDocument.getPath(context, cacheUri!!)
                        }
                        if (path == null) return
                        f = File(path)
                        if (f.exists() && f.length() > 0) {
                            listener.onCameraTakeSuccess(path)
                        } else {
                            MyLog.e(javaClass, "拍照存储异常")
                        }
                    }
                    FLAG_CHOOICE_IMAGE -> if (data != null && data.data != null) {
                        cacheUri = data.data
                        path = CameraDocument.getPath(context, cacheUri!!)
                        if (path == null) return
                        f = File(path)
                        if (f.exists() && f.length() > 0) {
                            listener.onCameraPickSuccess(path)
                        } else {
                            MyLog.e(javaClass, "选择的图片不存在")
                        }
                    }
                    FLAG_IMAGE_CUT -> {
                        if (imageUri == null) return
                        f = File(imageUri!!.path)
                        if (f.exists() && f.length() > 0) {
                            listener.onCameraCutSuccess(imageUri!!.path)
                        } else if (data != null && data.data != null) {
                            MyLog.e(javaClass, "剪切其他情况")
                            path = CameraDocument.getPath(context, data.data)
                            listener.onCameraCutSuccess(path!!)
                        } else {
                            MyLog.e(javaClass, "剪切未知情况")
                        }
                    }
                    FLAG_TAKE_VIDEO -> {
                        if (data != null && data.data != null) {
                            path = CameraDocument.getPath(context, data.data)
                            if (path == null) return
                            f = File(path)
                            if (f.exists() && f.length() > 0) {
                                if (videoListener != null)
                                    videoListener!!.onVideoTakeSuccess(path)
                            } else {
                                MyLog.e(javaClass, "选择的图片不存在")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    open fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        PermissionUtil.onRequestPermissionsResult(context, requestCode, permissions, grantResults, object : PermissionUtil.IRequestPermissionListener {
            override fun onRequestPermissionSuccess(requestCode: Int) {
                when (requestCode) {
                    PermissionUtil.REQUEST_PERMISSIONS_PHOTOGRAPH -> onDlgCameraClick()
                    PermissionUtil.REQUEST_PERMISSION_STORAGE -> onDlgPhotoClick(multiple)
                    PermissionUtil.REQUEST_PERMISSIONS_VIDEO_RECORD -> onDlgVideoClick(quality, seconds)
                }
            }

            override fun onRequestPermissionFail(requestCode: Int) {

            }
        })

    }

    interface CameraDealListener {
        fun onCameraTakeSuccess(path: String)

        fun onCameraPickSuccess(path: String)

        fun onCameraCutSuccess(path: String)
    }


    interface CamerVideoListener {
        fun onVideoTakeSuccess(path: String)
    }

    companion object {
        /**
         * 拍照
         */
        val FLAG_TAKE_PICTURE = 10
        /**
         * 拍视频
         */
        val FLAG_TAKE_VIDEO = 15
        /**
         * 选择图片
         */
        val FLAG_CHOOICE_IMAGE = 12
        /**
         * 剪切
         */
        val FLAG_IMAGE_CUT = 13
    }
}
