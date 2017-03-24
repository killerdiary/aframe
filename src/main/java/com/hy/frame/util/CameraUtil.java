package com.hy.frame.util;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.hy.frame.common.BaseActivity;
import com.hy.frame.common.BaseFragment;
import com.hy.frame.ui.CropActivity;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 拍照工具类
 * author HeYan
 * time 2015/12/29 16:57
 */
public class CameraUtil {
    public static final int REQUEST_PERMISSION_CAMERA = 2001;
    public static final int REQUEST_PERMISSION_STORAGE = 2002;
    public static final int REQUEST_PERMISSION_LOCATION = 2003;
    public static final int REQUEST_PERMISSION_AUDIO = 2004;
    /**
     * 拍照
     */
    public static final int FLAG_UPLOAD_TAKE_PICTURE = 10;
    /**
     * 拍视频
     */
    public static final int FLAG_UPLOAD_TAKE_VIDEO = 15;
    /**
     * 选择图片
     */
    public static final int FLAG_UPLOAD_CHOOICE_IMAGE = 12;
    /**
     * 剪切
     */
    public static final int FLAG_UPLOAD_IMAGE_CUT = 13;
    private BaseActivity act;
    private BaseFragment fragment;
    private CameraDealListener listener;
    private CamerVideoListener videoListener;

    public CameraUtil(BaseActivity act, CameraDealListener listener) {
        this.act = act;
        this.listener = listener;
    }

    public CameraUtil(BaseFragment fragment, CameraDealListener listener) {
        this.fragment = fragment;
        this.listener = listener;
    }

    public void setVideoListener(CamerVideoListener videoListener) {
        this.videoListener = videoListener;
    }

    //private Uri imageUri, cacheUri;
    private static final String URI_IMAGE = "CAMERA_URI_IMAGE", URI_CACHE = "CAMERA_URI_CACHE";// URI_CONTENT = "CAMERA_URI_CONTENT";
    private static final String URI_VIDEO = "CAMERA_URI_VIDEO";


    private Context getContext() {
        if (act == null) return fragment.getContext();
        return act;
    }

    private boolean initPhotoData() {
        String path = HyUtil.getCachePathCrop(getContext());
        // 判断sd卡
        if (path == null) {
            MyToast.show(getContext(), "没有SD卡，不能拍照");
            return false;
        }
        if (!requesStoragetPermission()) return false;
        // FileUtil.delAllFile(path);
        long time = System.currentTimeMillis();
        String imagePath = path + File.separator + "pic" + time + ".jpg";
        //String cachePath = path + File.separator + "cache" + time + ".jpg";
        //MyShare.get(getContext()).putString(URI_CACHE, "file://" + cachePath);
        MyShare.get(getContext()).putString(URI_IMAGE, "file://" + imagePath);
        return true;
    }


    public void onDlgCameraClick() {
        if (initPhotoData()) {
            if (!requestCameraPermission()) return;
            try {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                ContentValues values = new ContentValues();
                Uri contentUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                if (contentUri == null) return;
                MyShare.get(getContext()).putString(URI_CACHE, contentUri.toString());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, FLAG_UPLOAD_TAKE_PICTURE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onDlgPhotoClick() {
        if (initPhotoData())
            try {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, FLAG_UPLOAD_CHOOICE_IMAGE);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public void onDlgVideoClick(float quality, int seconds) {
        if (initPhotoData()) {
            if (!requestCameraPermission()) return;
            try {
                //Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                ContentValues values = new ContentValues();
                Uri contentUri = getContext().getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
                if (contentUri == null) return;
                MyShare.get(getContext()).putString(URI_VIDEO, contentUri.toString());
                //intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, seconds);
                intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 5 * 1000);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NE W_TASK);
                startActivityForResult(intent, FLAG_UPLOAD_TAKE_VIDEO);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void cropImageUri(int aspectX, int aspectY, int unit) {
        cropImageUri(getCacheUri(), aspectX, aspectY, unit);
    }

    public void cropImageUri(Uri uri, int aspectX, int aspectY, int unit) {
        Uri imageUri = getImageUri();
        if (uri == null || imageUri == null) {
            MyLog.e(getClass(), "地址未初始化");
            return;
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

        Intent intent = new Intent(getContext(), CropActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(CropActivity.EXTRA_ASPECT_X, aspectX);
        bundle.putInt(CropActivity.EXTRA_ASPECT_Y, aspectY);
        bundle.putInt(CropActivity.EXTRA_ASPECT_UNIT, unit);
        bundle.putParcelable(CropActivity.EXTRA_INPUT_URI, uri);
        bundle.putParcelable(CropActivity.EXTRA_OUTPUT_URI, imageUri);
        intent.putExtra(BaseActivity.BUNDLE, bundle);
        try {
            startActivityForResult(intent, FLAG_UPLOAD_IMAGE_CUT);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void startActivityForResult(Intent intent, int requestCode) {
        if (act == null) {
            fragment.startActivityForResult(intent, requestCode);
        } else {
            act.startActivityForResult(intent, requestCode);
        }
    }

    public Uri getCacheUri() {
        String path = MyShare.get(getContext()).getString(URI_CACHE);
        return path == null ? null : Uri.parse(path);
    }

    public Uri getImageUri() {
        String path = MyShare.get(getContext()).getString(URI_IMAGE);
        return path == null ? null : Uri.parse(path);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            File f;
            Uri cacheUri;
            String path;
            try {
                switch (requestCode) {
                    case FLAG_UPLOAD_TAKE_PICTURE:
                        if (data != null && data.getData() != null) {
                            path = CameraDocument.getPath(getContext(), data.getData());
                        } else {
                            path = CameraDocument.getPath(getContext(), getCacheUri());
                        }
                        f = new File(path);
                        if (f.exists() && f.length() > 0) {
                            if (listener != null)
                                listener.onCameraTakeSuccess(path);
                        } else {
                            MyLog.e(getClass(), "拍照存储异常");
                        }
                        break;
                    case FLAG_UPLOAD_CHOOICE_IMAGE:
                        if (data != null && data.getData() != null) {
                            path = CameraDocument.getPath(getContext(), data.getData());
                            f = new File(path);
                            if (f.exists() && f.length() > 0) {
                                if (listener != null)
                                    listener.onCameraPickSuccess(path);
                            } else {
                                MyLog.e(getClass(), "选择的图片不存在");
                            }
                        }
                        break;
                    case FLAG_UPLOAD_IMAGE_CUT:
                        Uri imageUri = getImageUri();
                        f = new File(imageUri.getPath());
                        if (f.exists() && f.length() > 0) {
                            if (listener != null)
                                listener.onCameraCutSuccess(imageUri.getPath());
                        } else if (data != null && data.getData() != null) {
                            MyLog.e(getClass(), "剪切其他情况");
                            path = CameraDocument.getPath(getContext(), data.getData());
                            if (listener != null)
                                listener.onCameraCutSuccess(path);
                        } else {
                            MyLog.e(getClass(), "剪切未知情况");
                        }
                        break;
                    case FLAG_UPLOAD_TAKE_VIDEO:
                        if (data != null && data.getData() != null) {
                            path = CameraDocument.getPath(getContext(), data.getData());
                            f = new File(path);
                            if (f.exists() && f.length() > 0) {
                                if (videoListener != null)
                                    videoListener.onVideoTakeSuccess(path);
                            } else {
                                MyLog.e(getClass(), "选择的图片不存在");
                            }
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean requestCameraPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            return true;
        Activity act = null;
        if (this.act != null)
            act = this.act;
        if (this.fragment != null)
            act = this.fragment.getActivity();
        ActivityCompat.requestPermissions(act,
                new String[]{Manifest.permission.CAMERA},
                REQUEST_PERMISSION_CAMERA);
        return false;
    }

    public boolean requestAudioPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
            return true;
        Activity act = null;
        if (this.act != null)
            act = this.act;
        if (this.fragment != null)
            act = this.fragment.getActivity();
        ActivityCompat.requestPermissions(act,
                new String[]{Manifest.permission.RECORD_AUDIO},
                REQUEST_PERMISSION_AUDIO);
        return false;
    }

    public boolean requesStoragetPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return true;
        Activity act = null;
        if (this.act != null)
            act = this.act;
        if (this.fragment != null)
            act = this.fragment.getActivity();
        ActivityCompat.requestPermissions(act,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_PERMISSION_STORAGE);
        return false;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CAMERA: {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    MyToast.show(getContext(), "获取权限成功，请重试");
                } else {
                    MyToast.show(getContext(), "您没有摄像头权限，请去权限管理中心开启");
                }
            }
            break;
            case REQUEST_PERMISSION_STORAGE: {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    MyToast.show(getContext(), "获取权限成功，请重试");
                } else {
                    MyToast.show(getContext(), "您没有文件存储权限，请去权限管理中心开启");
                }
            }
            break;
            case REQUEST_PERMISSION_AUDIO: {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    MyToast.show(getContext(), "获取权限成功，请重试");
                } else {
                    MyToast.show(getContext(), "您没有录音权限，请去权限管理中心开启");
                }
            }
            break;
        }
    }

    public boolean checkAudioPermission() {
        if (requesStoragetPermission() && requestAudioPermission()) return true;
        return false;
    }

    public boolean checkVideoPermission() {
        if (requesStoragetPermission() && requestAudioPermission() && requestCameraPermission())
            return true;
        return false;
    }

    private boolean saveFile(Uri uri, File f) {
        ContentResolver resolver = getContext().getContentResolver();
        try {
            if (!f.exists())
                f.createNewFile();
            Bitmap bmp;
            String path = CameraDocument.getPath(getContext(), uri);
            bmp = BitmapFactory.decodeFile(path);
            if (bmp == null || bmp.getWidth() < 1) {
                return false;
            }
            FileOutputStream fOut = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            return true;
        } catch (Exception e) {
            MyLog.e(getClass(), "saveFile Error");
        }
        return false;
    }

    public interface CameraDealListener {
        void onCameraTakeSuccess(String path);

        void onCameraPickSuccess(String path);

        void onCameraCutSuccess(String path);
    }


    public interface CamerVideoListener {
        void onVideoTakeSuccess(String path);
    }
}
