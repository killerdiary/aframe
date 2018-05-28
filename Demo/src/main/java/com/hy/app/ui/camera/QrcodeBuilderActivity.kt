package com.hy.app.ui.camera

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.oned.Code128Writer
import com.google.zxing.qrcode.QRCodeWriter
import com.hy.app.R
import com.hy.app.common.BaseActivity
import com.hy.frame.util.DimensionUtil
import com.hy.frame.util.FormatUtil
import com.hy.frame.util.MyToast
import java.util.*
import java.util.regex.Pattern

/**
 * 二维码生成
 * @author HeYan
 * @time 2017/9/12 10:26
 */
class QrcodeBuilderActivity : BaseActivity() {

    private var imgQrcode: ImageView? = null
    private var editContent: EditText? = null
    private var btnQrcode: Button? = null
    private var data: String? = null

    override fun getLayoutId(): Int = R.layout.act_camera_qrcode_builder

    override fun initView() {
        imgQrcode = findViewById(R.id.camera_qrcode_builder_imgQrcode)
        editContent = findViewById(R.id.camera_qrcode_builder_editContent)
        btnQrcode = setOnClickListener(R.id.camera_qrcode_builder_btnQrcode)
        setOnClickListener<View>(R.id.camera_qrcode_builder_btnBarcode)
    }

    override fun initData() {
        initHeaderBack(R.string.camera_qrcode_builder)
        data = bundle?.getString(ARG_DATA)
        if (!data.isNullOrEmpty()) {
            editContent?.setText(data)
            onViewClick(btnQrcode!!)
        }
    }

    private fun requestData() {}

    private fun updateUI() {}

    override fun onViewClick(v: View) {
        when (v.id) {
            R.id.camera_qrcode_builder_btnBarcode -> buildQrcode(false)
            R.id.camera_qrcode_builder_btnQrcode -> buildQrcode()
        }
    }

    private fun buildQrcode(isQrcode: Boolean = true) {
        val content = editContent?.text
        if (content.isNullOrEmpty()) {
            MyToast.show(getCurContext()!!, editContent?.hint.toString())
            return
        }
        if (!isQrcode && !checkBarcode(content.toString())) {
            MyToast.show(getCurContext()!!, getString(R.string.camera_qrcode_builder_bar_format_hint))
            return
        }
        val width = DimensionUtil.dip2px(300F, getCurContext())
        val bmp = createImage(content.toString(), width, width, isQrcode)
        imgQrcode?.setImageBitmap(bmp)
    }

    /**
     * 是否是英文
     */
    private fun checkBarcode(str: String?): Boolean {
        if (str == null)
            return false
        return Pattern.compile("[a-zA-Z0-9]+").matcher(str).matches()
    }

    // 生成QR图
    private fun createImage(str: String, width: Int, height: Int, isQrcode: Boolean = true): Bitmap? {
        if (FormatUtil.isEmpty(str))
            return null
        return try {
            // 需要引入core包
            val writer = if (isQrcode) QRCodeWriter() else Code128Writer()
            val hints = Hashtable<EncodeHintType, Any>()
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8")
            hints.put(EncodeHintType.MARGIN, 0)
            val bitMatrix = writer.encode(str, if (isQrcode) BarcodeFormat.QR_CODE else BarcodeFormat.CODE_128, width, height, hints)
            toBitmap(bitMatrix)
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }

    }

    private fun toBitmap(bitMatrix: BitMatrix): Bitmap {
        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
            }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

    companion object {
        private val ARG_DATA = "arg_data"

        fun newArguments(data: String): Bundle {
            val args = Bundle()
            args.putString(ARG_DATA, data)
            return args
        }
    }
}