package com.hy.app.ui.camera

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.hy.app.R
import com.hy.app.common.BaseActivity
import com.hy.app.ui.dialog.PictureDialog
import com.hy.frame.util.CameraUtil
import com.hy.frame.util.MyLog
import java.util.ArrayList

/**
 * 图片选择
 * @author HeYan
 * @time 2017/9/12 11:55
 */
class PictureActivity : BaseActivity(), CameraUtil.CameraDealListener, PictureDialog.ConfirmDlgListener {
    private var imgPic: ImageView? = null
    private var camera: CameraUtil? = null
        get() {
            if (field == null)
                field = CameraUtil(this, this)
            return field
        }
    private var picDlg: PictureDialog? = null
        get() {
            if (field == null)
                field = PictureDialog(getCurContext(), this)
            return field
        }
    private var cacheUri: Uri? = null

    override fun getLayoutId(): Int = R.layout.act_camera_picture

    override fun initView() {
        imgPic = setOnClickListener(R.id.camera_picture_imgPic)
        setOnClickListener<View>(R.id.camera_picture_btnOption)
    }

    override fun initData() {
        initHeaderBack(R.string.camera_picture)
        camera = CameraUtil(this, this)
    }

    private fun requestData() {}

    private fun updateUI() {}

    override fun onViewClick(v: View) {
        when (v.id) {
            R.id.camera_picture_btnOption -> picDlg?.show()
            R.id.camera_picture_imgPic -> {
                if (cacheUri != null) {
                    val datas = ArrayList<String>()
                    datas.add(cacheUri.toString())
                    startAct(MultImageActivity::class.java, MultImageActivity.newArguments(datas))
                }
            }
        }

    }

    override fun onDlgCameraClick(dlg: PictureDialog) {
        camera?.onDlgCameraClick()
    }

    override fun onDlgPhotoClick(dlg: PictureDialog) {
        camera?.onDlgPhotoClick()
    }

    override fun onDlgCancelClick(dlg: PictureDialog) {}


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        MyLog.e(javaClass, "requestCode=$requestCode|resultCode=$resultCode")
        camera?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        camera?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onCameraTakeSuccess(path: String) {
        MyLog.e("onCameraTakeSuccess: " + path)
        cacheUri = Uri.parse("file://" + path)
        camera?.cropImageUri(cacheUri, 0, 0, 1000)
    }

    override fun onCameraPickSuccess(path: String) {
        MyLog.e("onCameraPickSuccess: " + path)
        cacheUri = Uri.parse("file://" + path)
        camera?.cropImageUri(cacheUri, 0, 0, 1000)
    }

    override fun onCameraCutSuccess(path: String) {
        MyLog.e("onCameraCutSuccess: " + path)
        imgPic?.setImageURI(Uri.parse("file://" + path))
    }
}
