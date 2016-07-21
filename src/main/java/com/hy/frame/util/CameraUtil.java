package com.hy.frame.util;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.hy.frame.common.BaseActivity;
import com.hy.frame.common.BaseFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 拍照工具类
 * author HeYan
 * time 2015/12/29 16:57
 */
public class CameraUtil {
    private BaseActivity act;
    private BaseFragment fragment;
    private CameraDealListener listener;
    private String cachePath;

    public CameraUtil(BaseActivity act, CameraDealListener listener, String cachePath) {
        this.act = act;
        this.listener = listener;
        this.cachePath = cachePath;
    }

    public CameraUtil(BaseFragment fragment, CameraDealListener listener, String cachePath) {
        this.fragment = fragment;
        this.listener = listener;
        this.cachePath = cachePath;
    }

    //private Uri imageUri, cacheUri;
    private static final String URI_IMAGE = "CAMERA_URI_IMAGE", URI_CACHE = "CAMERA_URI_CACHE";// URI_CONTENT = "CAMERA_URI_CONTENT";

    private Context getContext() {
        if (act == null) return fragment.getContext();
        return act;
    }

    private boolean initPhotoData() {
        // 判断sd卡
        if (cachePath == null) {
            MyToast.show(getContext(), "没有SD卡，不能拍照");
            return false;
        }
        // FileUtil.delAllFile(path);
        long time = System.currentTimeMillis();
        String imagePath = cachePath + File.separator + "pic" + time + ".jpg";
        //String cachePath = path + File.separator + "cache" + time + ".jpg";
        //MyShare.get(getContext()).putString(URI_CACHE, "file://" + cachePath);
        MyShare.get(getContext()).putString(URI_IMAGE, "file://" + imagePath);
        return true;
    }

    public void onDlgCameraClick() {
        if (initPhotoData())
            try {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                ContentValues values = new ContentValues();
                Uri contentUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                if (contentUri == null) return;
                MyShare.get(getContext()).putString(URI_CACHE, contentUri.toString());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                startActivityForResult(intent, Constant.FLAG_UPLOAD_TAKE_PICTURE);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public void onDlgPhotoClick() {
        if (initPhotoData())
            try {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, Constant.FLAG_UPLOAD_CHOOICE_IMAGE);
            } catch (Exception e) {
                e.printStackTrace();
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
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("scaleUpIfNeeded", true);//黑边
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("outputX", aspectX * unit);
        intent.putExtra("outputY", aspectX * unit);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, Constant.FLAG_UPLOAD_IMAGE_CUT);
    }

    /**
     * 压缩图片
     *
     * @param path      路径
     * @param maxKb     最大KB
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @param clistener 回调
     */
    public synchronized void compressImage(String path, int maxKb, int maxWidth, int maxHeight, CameraCompressListener clistener) {
        if (clistener == null) {
            MyLog.e("CameraCompressListener 未定义，CODE：0");
            return;
        }
        if (path == null) {
            MyLog.e("图片加载出错，CODE：0");
            clistener.onComprossError("图片加载出错");
            return;
        }
        Uri imageUri = getImageUri();
        if (imageUri == null) {
            MyLog.e(getClass(), "地址未初始化");
            clistener.onComprossError("图片存储路径未定义");
            return;
        }

        File f = new File(path);
        if (!f.exists()) {
            clistener.onComprossError("图片不存在");
            return;
        }
        double size = FileUtil.getFileOrFilesSize(path, FileUtil.SIZETYPE_KB);
        MyLog.d("File Size:" + size);
        if (maxKb > 0 && size <= maxKb) {
            clistener.onCompressSuccess(path);
            return;
        }
//            int maxWidth = 1024;
//            int maxHeight = 1024;
//            int maxWidth = 512;
//            int maxHeight = 512;
        BitmapFactory.Options oldOpts = new BitmapFactory.Options();
        oldOpts.inJustDecodeBounds = true;
        //加载属性
        BitmapFactory.decodeFile(path, oldOpts);
        int w = oldOpts.outWidth;
        int h = oldOpts.outHeight;
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 2;//be=1表示不缩放
        if (w > h && w > maxWidth) {//如果宽度大的话根据宽度固定大小缩放
            be = w / maxWidth;
        } else if (w < h && h > maxHeight) {//如果高度高的话根据宽度固定大小缩放
            be = h / maxHeight;
        }
        if (be <= 1)
            be = 2;
        MyLog.d("原尺寸：" + w + "x" + h + " | 缩放比例：" + be);
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        //newOpts.inJustDecodeBounds = true;
        Bitmap bitmap2 = BitmapFactory.decodeFile(path, newOpts);
        if (bitmap2 == null) {
            MyLog.e("图片加载出错，CODE：2");
            clistener.onComprossError("图片读取失败");
            return;
        }
        f = new File(imageUri.getPath());
        try {
            if (!f.exists() && f.createNewFile()) {
                clistener.onComprossError("文件创建失败");
                return;
            }
            FileOutputStream fOut = new FileOutputStream(f);
            bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            bitmap2.recycle();
            size = FileUtil.getFileOrFilesSize(imageUri.getPath(), FileUtil.SIZETYPE_KB);
            MyLog.d("File Size:" + size);
            clistener.onCompressSuccess(imageUri.getPath());
        } catch (IOException e) {
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
            File f = null;
            String path;
            try {
                switch (requestCode) {
                    case Constant.FLAG_UPLOAD_TAKE_PICTURE:
                        if (data != null && data.getData() != null) {
                            path = CameraDocument.getPath(getContext(), data.getData());
                        } else {
                            path = CameraDocument.getPath(getContext(), getCacheUri());
                        }
                        if (path != null)
                            f = new File(path);
                        if (f != null && f.exists() && f.length() > 0) {
                            if (listener != null)
                                listener.onCameraTakeSuccess(path);
                        } else {
                            MyLog.e(getClass(), "拍照存储异常");
                        }
                        break;
                    case Constant.FLAG_UPLOAD_CHOOICE_IMAGE:
                        if (data != null && data.getData() != null) {
                            path = CameraDocument.getPath(getContext(), data.getData());
                            if (path != null)
                                f = new File(path);
                            if (f != null && f.exists() && f.length() > 0) {
                                if (listener != null)
                                    listener.onCameraPickSuccess(path);
                            } else {
                                MyLog.e(getClass(), "选择的图片不存在");
                            }
                        }
                        break;
                    case Constant.FLAG_UPLOAD_IMAGE_CUT:
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
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean saveFile(Uri uri, File f) {
        try {
            if (!f.exists() && !f.createNewFile()) {
                return false;
            }
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
        void onCameraTakeSuccess(String uri);

        void onCameraPickSuccess(String uri);

        void onCameraCutSuccess(String uri);
    }

    public interface CameraCompressListener {
        void onCompressSuccess(String path);

        void onComprossError(String msg);
    }
}
