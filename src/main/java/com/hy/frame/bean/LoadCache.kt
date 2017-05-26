package com.hy.frame.bean

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView

import com.hy.frame.R

/**
 * LoadCache

 * @author HeYan
 * *
 * @time 2017/5/23 10:51
 */
class LoadCache {
    var llyLoad: LinearLayout? = null
    var txtMessage: TextView? = null
    var imgMessage: ImageView? = null
    var proLoading: ProgressBar? = null

    fun showLoading(msg: String) {
        llyLoad!!.visibility = View.VISIBLE
        proLoading!!.visibility = View.VISIBLE
        imgMessage!!.visibility = View.GONE
        txtMessage!!.visibility = View.VISIBLE
        txtMessage!!.text = msg
    }

    fun showNoData(msg: String?, drawId: Int) {
        llyLoad!!.visibility = View.VISIBLE
        proLoading!!.visibility = View.GONE
        imgMessage!!.visibility = View.VISIBLE
        txtMessage!!.visibility = View.VISIBLE
        if (msg == null)
            txtMessage!!.setText(R.string.hint_nodata)
        else
            txtMessage!!.text = msg
        imgMessage!!.setImageResource(drawId)
    }
}