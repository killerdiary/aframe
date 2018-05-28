package com.hy.app.ui.camera

import android.media.MediaPlayer
import android.view.View
import com.hy.app.R
import com.hy.app.common.BaseActivity
import com.hy.frame.qrcode.CameraScanView
import com.hy.frame.util.PermissionUtil

/**
 * 二维码扫描
 * @author HeYan
 * @time 2017/9/11 16:16
 */
class ScanActivity : BaseActivity(), PermissionUtil.IRequestPermissionListener {

    private var cameraView: CameraScanView? = null
    override fun isSingleLayout(): Boolean = true
    override fun getLayoutId(): Int = R.layout.act_camera_scan

    override fun initView() {
        cameraView = findViewById(R.id.camera_scan_cameraView)
    }

    override fun initData() {
        initHeaderBack(R.string.camera_scan)
        cameraView?.setListener { source ->
            if (!source.isNullOrEmpty()) {
                playHint()
                startAct(ScanResultActivity::class.java, ScanResultActivity.newArguments(source))
            }
        }
    }

    private fun requestData() {}

    private fun updateUI() {}

    override fun onViewClick(v: View) {}

    override fun onResume() {
        super.onResume()
        if (PermissionUtil.requestCameraPermission(this)) {
            cameraView?.start()
        }
    }

    override fun onPause() {
        super.onPause()
        cameraView?.stop()
    }

    override fun onDestroy() {
        cameraView?.onDestroy()
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtil.onRequestPermissionsResult(getCurContext()!!, requestCode, permissions, grantResults, this)
    }

    override fun onRequestPermissionSuccess(requestCode: Int) {
        cameraView?.start()
    }

    override fun onRequestPermissionFail(requestCode: Int) {
        finish()
    }

    private fun playHint() {
        val mp = MediaPlayer.create(getCurContext(), R.raw.scan_success)
        try {
            mp?.prepare()
            mp?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}