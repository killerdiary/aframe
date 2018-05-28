package com.hy.frame.widget.recycler

import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.hy.frame.R
import com.hy.frame.widget.AVLoadingIndicatorView

/**
 * LoadMoreView
 * @author HeYan
 * @time 2017/9/27 10:40
 */
class LoadMoreView(var v: View) {
    private val rlyLoadMore = v.findViewById<RelativeLayout>(R.id.recycler_loadmore_i_rlyLoadMore)
    private val txtMessage = v.findViewById<TextView>(R.id.recycler_loadmore_i_txtMessage)
    private val vIndicator = v.findViewById<AVLoadingIndicatorView>(R.id.recycler_loadmore_i_vIndicator)
    private val imgIndicator = v.findViewById<ImageView>(R.id.recycler_loadmore_i_imgIndicator)

    private var flag = FLAG_NORMAL
    private var listener: ILoadMoreViewListener? = null
    private var isBindClickListener = false

    init {
        loadComplete()
    }

    fun onViewAttachedToWindow() {
        if (flag == FLAG_NORMAL || flag == FLAG_COMPLETE)
            loading()
    }

    private fun loading() {
        flag = FLAG_LOADING
        txtMessage?.setText(R.string.refresh_load_ing)
        if (vIndicator?.visibility == View.GONE)
            vIndicator.show()
        imgIndicator?.visibility = View.GONE
        listener?.onLoading()
    }

    fun isLoading(): Boolean = flag == FLAG_LOADING

    fun loadComplete() {
        flag = FLAG_COMPLETE
        txtMessage?.setText(R.string.refresh_load_more)
        if (vIndicator?.visibility == View.VISIBLE)
            vIndicator.hide()
        imgIndicator?.visibility = View.GONE
    }

    fun loadError() {
        flag = FLAG_ERROR
        txtMessage?.setText(R.string.refresh_load_error)
        if (vIndicator?.visibility == View.VISIBLE)
            vIndicator.hide()
        imgIndicator?.visibility = View.VISIBLE
        if (!isBindClickListener) {
            isBindClickListener = true
            rlyLoadMore?.setOnClickListener({ loading() })
        }
    }

    fun close() {
        flag = FLAG_CLOSE
        txtMessage?.setText(R.string.refresh_load_close)
        if (vIndicator?.visibility == View.VISIBLE)
            vIndicator.hide()
        imgIndicator?.visibility = View.GONE
    }

    fun setLoadMoreViewListener(listener: ILoadMoreViewListener) {
        this.listener = listener
    }

    fun setBackgroundResource(@DrawableRes resId: Int) {
        rlyLoadMore?.setBackgroundResource(resId)
    }

    fun setBackgroundColor(@ColorInt color: Int) {
        rlyLoadMore?.setBackgroundColor(color)
    }

    interface ILoadMoreViewListener {
        fun onLoading()
    }

    companion object {
        val FLAG_NORMAL = 0
        val FLAG_LOADING = 1
        val FLAG_COMPLETE = 2
        val FLAG_ERROR = 3
        val FLAG_CLOSE = 4
    }
}