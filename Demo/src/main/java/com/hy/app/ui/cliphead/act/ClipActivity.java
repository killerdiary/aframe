package com.hy.app.ui.cliphead.act;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.hy.app.R;
import com.hy.app.ui.cliphead.utils.FileUtils;
import com.hy.app.ui.cliphead.utils.ImageUtils;
import com.hy.app.ui.cliphead.utils.UIUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ClipActivity extends Activity implements View.OnClickListener {

    public static final int IMAGE_TAKE_PIC_RESULT = 1;
    public static final int IMAGE_PIC_LIB_RESULT = 2;
    public static final int IMAGE_CLIP_PIC_RESULT = 3;

    private ImageView mImageView;
    private File mImageFile;
    private String mClipImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mImageView = (ImageView) findViewById(R.id.image);
        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                openPhotoLib();
                break;
            case R.id.btn2:
                takePhoto();
                break;
        }
    }

    private void takePhoto() {
        mImageFile = FileUtils.getImageFile();
        Uri imageFileUri = Uri.fromFile(mImageFile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
        startActivityForResult(intent, IMAGE_TAKE_PIC_RESULT);
    }

    private void openPhotoLib() {
        Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, IMAGE_PIC_LIB_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (IMAGE_TAKE_PIC_RESULT == requestCode && Activity.RESULT_OK == resultCode) {
            takePhotoResult();
        } else if (IMAGE_PIC_LIB_RESULT == requestCode && data != null && Activity.RESULT_OK == resultCode) {
            picLibResult(data);
        } else if (IMAGE_CLIP_PIC_RESULT == requestCode && data != null) {
            clipPicResult(data);
        }
    }


    private void takePhotoResult() {
        //图片是否需要旋转
        int degree = ImageUtils.getBitmapDegree(mImageFile.getAbsolutePath());
        if (degree != 0) {
            Bitmap bitmap = ImageUtils.getScaledBitmap(mImageFile.getAbsolutePath(), UIUtils.INSTANCE.getScreenWidth(), UIUtils.INSTANCE.getScreenHeight());
            bitmap = ImageUtils.rotateBitmapByDegree(bitmap, degree);
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(mImageFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        Intent intent = new Intent(this,
                ClipPictureActivity.class);
        intent.putExtra(ClipPictureActivity.TAG_URL, mImageFile.getAbsolutePath());
        startActivityForResult(intent, IMAGE_CLIP_PIC_RESULT);
    }

    private void picLibResult(Intent data) {
        Uri selectedImage = data.getData();
        String picturePath;
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
        } else {
            picturePath = selectedImage.toString();
        }
        if (picturePath.startsWith("file://")) {
            picturePath = picturePath.substring(7);
        }
        Intent intent = new Intent(this, ClipPictureActivity.class);
        intent.putExtra(ClipPictureActivity.TAG_URL, picturePath);
        startActivityForResult(intent, IMAGE_CLIP_PIC_RESULT);
    }

    private void clipPicResult(Intent data) {
        mClipImagePath = data.getStringExtra(ClipPictureActivity.TAG_CLIPED_URL);
        Bitmap bitmap = BitmapFactory.decodeFile(mClipImagePath);
        mImageView.setImageBitmap(bitmap);
    }
}
