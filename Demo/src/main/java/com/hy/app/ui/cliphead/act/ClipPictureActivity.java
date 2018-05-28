package com.hy.app.ui.cliphead.act;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.hy.app.R;
import com.hy.app.ui.cliphead.utils.FileUtils;
import com.hy.app.ui.cliphead.utils.ImageUtils;
import com.hy.app.ui.cliphead.utils.UIUtils;
import com.hy.app.ui.cliphead.view.ClipView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * 实现思路：
 * 截取屏幕的截图，然后在该截图里截取矩形框里的区域
 */
public class ClipPictureActivity extends Activity implements OnClickListener{
	public static final String TAG_URL = "image_url";
	public static final String TAG_CLIPED_URL = "clip_image_url";

	private ImageView srcPic;
	private ClipView clipview;

	int statusBarHeight = 0;
	int titleBarHeight = 0;
	private String mImageUrl;

	// These matrices will be used to move and zoom image
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();

	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	private TextView tv_bottom_ok;
	int mode = NONE;

	// Remember some things for zooming
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_clip_photo);

		mImageUrl = getIntent().getStringExtra(TAG_URL);
		srcPic = (ImageView) findViewById(R.id.src_pic);
		tv_bottom_ok =(TextView) findViewById(R.id.tv_bottom_ok);

		Bitmap srcBitmap = ImageUtils.getScaledBitmap(mImageUrl, UIUtils.INSTANCE.getScreenWidth(), UIUtils.INSTANCE.getScreenHeight());

		srcPic.setImageBitmap(srcBitmap);

		tv_bottom_ok.setOnClickListener(this);
	}


	public void onClick(View v)
	{
		switch (v.getId()) {
		case R.id.tv_bottom_ok:
			Bitmap clipedBitmap = getClipedBitmap();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			clipedBitmap.compress(CompressFormat.JPEG, 100, baos);
			byte[] bitmapByte = baos.toByteArray();
			Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
			saveBitmap(bitmap, new File(mImageUrl).getName());
		   break;

		default:
			break;
		}
	}

	public void saveBitmap(Bitmap bitmap, String fileName) {
		File file = new File(FileUtils.getPicClipDir(), System.currentTimeMillis()+"_"+fileName);
		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
			bitmap.compress(CompressFormat.JPEG, 100, out);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Intent data = new Intent();
		data.putExtra(TAG_CLIPED_URL, file.getAbsolutePath());
		setResult(-1, data);
		finish();
	}


	private Bitmap getClipedBitmap()
	{
		getBarHeight();
		Bitmap screenShoot = takeScreenShot();
		clipview = (ClipView)this.findViewById(R.id.clipview);
		Bitmap finalBitmap = Bitmap.createBitmap(screenShoot,clipview.getTopX() , clipview.getTopY()+ titleBarHeight + statusBarHeight, clipview.VIEW_WIDTH ,clipview.VIEW_HEIGHT);

		return finalBitmap;
	}

	private void getBarHeight()
	{
		Rect frame = new Rect();
		this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		statusBarHeight = frame.top;

		int contenttop = this.getWindow()
				.findViewById(Window.ID_ANDROID_CONTENT).getTop();
		titleBarHeight = contenttop - statusBarHeight;

	}

	private Bitmap takeScreenShot()
	{
		View view = this.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		return view.getDrawingCache();
	}

}