package com.hy.app.ui.dialog

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.hy.app.R
import com.hy.frame.app.BaseDialog

/**
 * 头像修改
 * @author HeYan
 * @time 2015-8-24 下午3:22:42
 */
class PictureDialog(context: Context, private val confirmDlgListener: PictureDialog.ConfirmDlgListener) : BaseDialog(context) {

    override fun getLayoutId(): Int = R.layout.dlg_picture

    override fun initWindow() {
        windowDeploy(WindowManager.LayoutParams.MATCH_PARENT.toFloat(), WindowManager.LayoutParams.WRAP_CONTENT.toFloat(), Gravity.BOTTOM)
        window!!.setWindowAnimations(R.style.animBottomInBottomOutStyle)
        setCancelable(true)
        setCanceledOnTouchOutside(true)
    }

    override fun initView() {
        setOnClickListener<View>(R.id.picture_btnCamera)
        setOnClickListener<View>(R.id.picture_btnAlbum)
        setOnClickListener<View>(R.id.picture_btnCancel)
    }

    override fun initData() {}

    override fun onViewClick(v: View) {
        this.dismiss()
        when (v.id) {
            R.id.picture_btnCamera -> confirmDlgListener.onDlgCameraClick(this)
            R.id.picture_btnAlbum -> confirmDlgListener.onDlgPhotoClick(this)
            R.id.picture_btnCancel -> confirmDlgListener.onDlgCancelClick(this)
        }

    }

    interface ConfirmDlgListener {
        fun onDlgCameraClick(dlg: PictureDialog)

        fun onDlgPhotoClick(dlg: PictureDialog)

        fun onDlgCancelClick(dlg: PictureDialog)
    }
}