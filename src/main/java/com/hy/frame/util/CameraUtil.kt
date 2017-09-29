package com.hy.frame.util

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import com.hy.frame.common.BaseActivity
import com.hy.frame.common.BaseFragment
import com.hy.frame.ui.CropActivity
import java.io.File
import java.io.FileOutputStream

/**
 * 拍照工具类
 * author HeYan
 * time 2015/12/29 16:57
 */
class CameraUtil {
    private var act: BaseActivity? = null
    private var fragment: BaseFragment? = null
    private var listener: CameraDealListener? = null
    private var videoListener: CamerVideoListener? = null

    constructor(act: BaseActivity, listener: CameraDealListener) {
        this.act = act
        this.listener = listener
    }

    constructor(fragment: BaseFragment, listener: CameraDealListener) {
        this.fragment = fragment
        this.listener = listener
    }

    fun setVideoListener(videoListener: CamerVideoListener) {
        this.videoListener = videoListener
    }

    private var imageUri: Uri? = null
    private var cacheUri: Uri? = null
    //private static final String URI_IMAGE = "CAMERA_URI_IMAGE";
    //private static final String URI_CACHE = "CAMERA_URI_CACHE";
    //private static final String URI_VIDEO = "CAMERA_URI_VIDEO";


    private val context: Context
        get() {
            if (act == null) return fragment!!.context
            return act!!
        }

    private fun initPhotoData(): Boolean {
        if (!requesStoragetPermission()) return false
        val path = HyUtil.getCachePathCrop(context)
        // 判断sd卡
        if (path == null) {
            MyToast.show(context, "没有SD卡，不能拍照")
            return false
        }
        // FileUtil.delAllFile(path);
        //long time = System.currentTimeMillis();
        //String imagePath = path + File.separator + "pic" + time + ".jpg";
        //String cachePath = path + File.separator + "cache" + time + ".jpg";
        //MyShare.get(getContext()).putString(URI_CACHE, "file://" + cachePath);
        //MyShare.get(getContext()).putString(URI_IMAGE, "file://" + imagePath);
        return true
    }

    private val newCacheUri: Uri
        get() {
            val path = HyUtil.getCachePathCrop(context)
            val time = System.currentTimeMillis()
            val imagePath = path + File.separator + "pic" + time + ".jpg"
            return Uri.parse("file://" + imagePath)
        }

    fun onDlgCameraClick() {
        if (initPhotoData()) {
            if (!requestCameraPermission()) return
            try {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val values = ContentValues()
                cacheUri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                if (cacheUri == null) return
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cacheUri)
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, FLAG_UPLOAD_TAKE_PICTURE)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    fun onDlgPhotoClick(multiple: Boolean = false) {
        if (initPhotoData())
            try {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, multiple)
                }
                startActivityForResult(intent, FLAG_UPLOAD_CHOOICE_IMAGE)
            } catch (e: Exception) {
                e.printStackTrace()
            }

    }

    fun onDlgVideoClick(quality: Int, seconds: Int) {
        if (initPhotoData()) {
            if (!requestCameraPermission()) return
            try {
                //Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                val values = ContentValues()
                val contentUri = context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values) ?: return
//MyShare.get(getContext()).putString(URI_VIDEO, contentUri.toString());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, quality)
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, seconds)
                intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 480 * 800)
                startActivityForResult(intent, FLAG_UPLOAD_TAKE_VIDEO)
                MyLog.e(javaClass, "onDlgVideoClick 1")
            } catch (e: Exception) {
                e.printStackTrace()
                MyLog.e(javaClass, "onDlgVideoClick 2")
            }

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
        //        Intent intent = new Intent("com.android.camera.action.CROP");
        //        intent.setDataAndType(uri, "image/*");
        //        intent.putExtra("scaleUpIfNeeded", true);//黑边
        //        intent.putExtra("crop", "true");
        //        intent.putExtra("aspectX", aspectX);
        //        intent.putExtra("aspectY", aspectY);
        //        intent.putExtra("outputX", aspectX * unit);
        //        intent.putExtra("outputY", aspectX * unit);
        //        intent.putExtra("scale", true);
        //        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        //        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        //        intent.putExtra("noFaceDetection", true); // no face detection
        //        startActivityForResult(intent, Constant.FLAG_UPLOAD_IMAGE_CUT);

        //        UCrop.Options options = new UCrop.Options();
        //        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        //        options.setCompressionQuality(70);//质量
        //        options.setStatusBarColor(Color.parseColor("#D81B60"));
        //        options.setToolbarColor(Color.parseColor("#E91E63"));
        //        UCrop uCrop = UCrop.of(uri, imageUri);
        //        if (aspectX > 0 && aspectY > 0) {
        //            //uCrop = uCrop.withAspectRatio(aspectX, aspectY);
        //            //uCrop = uCrop.withAspectRatio(aspectX, aspectY);
        //        } else {
        //            uCrop = uCrop.useSourceImageAspectRatio();
        //        }
        //        uCrop.withOptions(options);
        //        uCrop.start(act == null ? fragment.getActivity() : act);

        val intent = Intent(context, CropActivity::class.java)
        val bundle = Bundle()
        bundle.putInt(CropActivity.EXTRA_ASPECT_X, aspectX)
        bundle.putInt(CropActivity.EXTRA_ASPECT_Y, aspectY)
        bundle.putInt(CropActivity.EXTRA_ASPECT_UNIT, unit)
        bundle.putParcelable(CropActivity.EXTRA_INPUT_URI, cacheUri)
        bundle.putParcelable(CropActivity.EXTRA_OUTPUT_URI, imageUri)
        intent.putExtra(BaseActivity.BUNDLE, bundle)
        try {
            startActivityForResult(intent, FLAG_UPLOAD_IMAGE_CUT)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun startActivityForResult(intent: Intent, requestCode: Int) {
        if (act == null) {
            fragment!!.startActivityForResult(intent, requestCode)
        } else {
            act!!.startActivityForResult(intent, requestCode)
        }
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

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        MyLog.e(javaClass, "onActivityResult 4")
        if (resultCode == Activity.RESULT_OK) {
            val f: File
            val path: String?
            try {
                when (requestCode) {
                    FLAG_UPLOAD_TAKE_PICTURE -> {
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
                            if (listener != null)
                                listener!!.onCameraTakeSuccess(path)
                        } else {
                            MyLog.e(javaClass, "拍照存储异常")
                        }
                    }
                    FLAG_UPLOAD_CHOOICE_IMAGE -> if (data != null && data.data != null) {
                        cacheUri = data.data
                        path = CameraDocument.getPath(context, cacheUri!!)
                        if (path == null) return
                        f = File(path)
                        if (f.exists() && f.length() > 0) {
                            if (listener != null)
                                listener!!.onCameraPickSuccess(path)
                        } else {
                            MyLog.e(javaClass, "选择的图片不存在")
                        }
                    }
                    FLAG_UPLOAD_IMAGE_CUT -> {
                        if (imageUri == null) return
                        f = File(imageUri!!.path)
                        if (f.exists() && f.length() > 0) {
                            if (listener != null)
                                listener!!.onCameraCutSuccess(imageUri!!.path)
                        } else if (data != null && data.data != null) {
                            MyLog.e(javaClass, "剪切其他情况")
                            path = CameraDocument.getPath(context, data.data)
                            if (listener != null)
                                listener!!.onCameraCutSuccess(path!!)
                        } else {
                            MyLog.e(javaClass, "剪切未知情况")
                        }
                    }
                    FLAG_UPLOAD_TAKE_VIDEO -> {
                        MyLog.e(javaClass, "onActivityResult 5")
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
                MyLog.e(javaClass, "onActivityResult 6")
            }

        }
    }

    fun requestCameraPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            return true
        if (this.act != null)
            act!!.requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSION_CAMERA)
        if (this.fragment != null)
            fragment!!.requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSION_CAMERA)
        return false
    }

    fun requestAudioPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
            return true
        if (this.act != null)
            act!!.requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_PERMISSION_AUDIO)
        if (this.fragment != null)
            fragment!!.requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_PERMISSION_AUDIO)
        return false
    }

    fun requesStoragetPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return true
        if (this.act != null)
            act!!.requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION_STORAGE)
        if (this.fragment != null)
            fragment!!.requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION_STORAGE)
        return false
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION_CAMERA -> {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    MyToast.show(context, "获取权限成功，请重试")
                } else {
                    MyToast.show(context, "您没有摄像头权限，请去权限管理中心开启")
                }
            }
            REQUEST_PERMISSION_STORAGE -> {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    MyToast.show(context, "获取权限成功，请重试")
                } else {
                    MyToast.show(context, "您没有文件存储权限，请去权限管理中心开启")
                }
            }
            REQUEST_PERMISSION_AUDIO -> {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    MyToast.show(context, "获取权限成功，请重试")
                } else {
                    MyToast.show(context, "您没有录音权限，请去权限管理中心开启")
                }
            }
        }
    }

    fun checkAudioPermission(): Boolean {
        if (requesStoragetPermission() && requestAudioPermission()) return true
        return false
    }

    fun checkVideoPermission(): Boolean {
        if (requesStoragetPermission() && requestAudioPermission() && requestCameraPermission())
            return true
        return false
    }

    private fun saveFile(uri: Uri, f: File): Boolean {
        val resolver = context.contentResolver
        try {
            if (!f.exists())
                f.createNewFile()
            val bmp: Bitmap?
            val path = CameraDocument.getPath(context, uri)
            bmp = BitmapFactory.decodeFile(path)
            if (bmp == null || bmp.width < 1) {
                return false
            }
            val fOut = FileOutputStream(f)
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
            fOut.flush()
            fOut.close()
            return true
        } catch (e: Exception) {
            MyLog.e(javaClass, "saveFile Error")
        }

        return false
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
        val REQUEST_PERMISSION_CAMERA = 201
        val REQUEST_PERMISSION_STORAGE = 202
        val REQUEST_PERMISSION_AUDIO = 204
        /**
         * 拍照
         */
        val FLAG_UPLOAD_TAKE_PICTURE = 10
        /**
         * 拍视频
         */
        val FLAG_UPLOAD_TAKE_VIDEO = 15
        /**
         * 选择图片
         */
        val FLAG_UPLOAD_CHOOICE_IMAGE = 12
        /**
         * 剪切
         */
        val FLAG_UPLOAD_IMAGE_CUT = 13
    }
}
