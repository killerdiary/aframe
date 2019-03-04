package com.hy.frame.ui

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.DrawableRes
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.hy.frame.R
import com.hy.frame.ui.dialog.LoadingDialog
import com.hy.frame.util.MyLog
import com.hy.frame.util.MyToast

/**
 * 魔板控制器，允许重写
 */
open class TemplateControl : ITemplateControl, View.OnClickListener {

    var mTemplateView: IBaseTemplateUI? = null
    var mToolbar: Toolbar? = null
    var mFlyMain: FrameLayout? = null
    var mLoadView: View? = null
    var llyLoad: LinearLayout? = null
    var txtMessage: TextView? = null
    var imgMessage: ImageView? = null
    var proLoading: View? = null
    var mLoadingDialog: ILoadingDialog? = null

    override fun init(templateView: IBaseTemplateUI, mToolbar: Toolbar?, mFlyMain: FrameLayout?) {
        this.mTemplateView = templateView
        this.mToolbar = mToolbar
        this.mFlyMain = mFlyMain
        this.mToolbar?.setTitle(R.string.empty)
    }

    override fun setTitle(title: CharSequence?) {
        if (this.mTemplateView == null || this.mToolbar == null)
            return
        if (findViewById<View>(R.id.head_vTitle, this.mToolbar) == null)
            View.inflate(getCurContext(), R.layout.in_head_title, mToolbar)
        findViewById<TextView>(R.id.head_vTitle, mToolbar)?.text = title
    }

    @SuppressLint("ResourceType")
    override fun setHeaderLeft(@DrawableRes left: Int) {
        if (mToolbar != null && left > 0) {
            if (findViewById<View>(R.id.head_vLeft, mToolbar) == null)
                View.inflate(getCurContext(), R.layout.in_head_left, mToolbar)
            val img = findViewById<ImageView>(R.id.head_vLeft, mToolbar)
            img?.setOnClickListener(this)
            img?.setImageResource(left)
        }
    }

    @SuppressLint("ResourceType")
    protected fun setHeaderLeftTxt(@StringRes left: Int) {
        setHeaderRightTxt(if (left == 0) null else getCurContext().getString(left))
    }

    override fun setHeaderLeftTxt(left: String?) {
        if (mToolbar != null) {
            if (findViewById<View>(R.id.head_vLeft, mToolbar) == null) {
                if (left.isNullOrEmpty()) return
                View.inflate(getCurContext(), R.layout.in_head_tleft, mToolbar)
            }
            val txt = findViewById<TextView>(R.id.head_vLeft, mToolbar)
            if (left.isNullOrEmpty()) {
                mToolbar?.removeView(txt)
                return
            }
            txt?.setOnClickListener(this)
            txt?.text = left
        }
    }

    @SuppressLint("ResourceType")
    override fun setHeaderRight(@DrawableRes right: Int) {
        addHeaderRight(right, null, R.id.head_vRight)
    }

    @SuppressLint("ResourceType")
    protected fun setHeaderRightTxt(@StringRes right: Int) {
        setHeaderRightTxt(if (right == 0) null else getCurContext().getString(right))
    }

    override fun setHeaderRightTxt(right: String?) {
        if (mToolbar != null) {
            if (findViewById<View>(R.id.head_vRight, mToolbar) == null) {
                if (right.isNullOrEmpty()) return
                View.inflate(getCurContext(), R.layout.in_head_tright, mToolbar)
            }
            val txt = findViewById<TextView>(R.id.head_vRight, mToolbar)
            if (right.isNullOrEmpty()) {
                mToolbar?.removeView(txt)
                return
            }
            txt?.setOnClickListener(this)
            txt?.text = right
        }
    }

    override fun addHeaderRight(@DrawableRes right: Int, @IdRes id: Int) {
        addHeaderRight(right, null, id)
    }

    override fun addHeaderRightPath(rightPath: String?, @IdRes id: Int) {
        addHeaderRight(0, rightPath, id)
    }

    @SuppressLint("ResourceType")
    private fun addHeaderRight(@DrawableRes right: Int, @DrawableRes rightPath: String?, @IdRes id: Int) {
        if (mToolbar != null) {
            var img: ImageView? = findViewById(id, mToolbar)
            if (img != null) {
                if (right == 0 && rightPath == null) {
                    mToolbar?.removeView(img)
                } else {
                    if (right != 0) {
                        img.setImageResource(right)
                    } else {
                        com.bumptech.glide.Glide.with(getCurContext()).asBitmap().apply(com.bumptech.glide.request.RequestOptions.noTransformation().placeholder(R.color.transparent).error(R.drawable.v_warn)).load(rightPath).into(img)
                    }
                }
                return
            }
            if (right == 0 && rightPath == null) return
            val v = View.inflate(getCurContext(), R.layout.in_head_right, null)
            img = findViewById(R.id.head_vRight, v)
            if (id != 0)
                img?.id = id
            val array = getCurContext().theme.obtainStyledAttributes(intArrayOf(R.attr.appHeaderHeight))
            val width = array.getDimensionPixelSize(0, 0)
            array.recycle()
            val params = Toolbar.LayoutParams(width, width)
            //params.setMargins(0, 0, width * (rightCount - 1), 0);
            params.gravity = Gravity.RIGHT
            img?.layoutParams = params
            mToolbar!!.addView(v)
            img?.setOnClickListener(this)
            if (right != 0) {
                img?.setImageResource(right)
            } else {
                com.bumptech.glide.Glide.with(getCurContext()).asBitmap().apply(com.bumptech.glide.request.RequestOptions.noTransformation().placeholder(R.color.transparent).error(R.drawable.v_warn)).load(rightPath).into(img!!)
            }
        }
    }

    override fun showToast(msg: String?) {
        MyToast.show(getCurContext(), msg)
    }

    /**
     * 加载布局
     */
    protected open fun initLoadView(): Boolean {
        if (mFlyMain == null) {
            MyLog.e(javaClass, "Your layout must include 'FrameLayout',the ID must be 'base_flyMain'!")
            return false
        }
        if (mLoadView != null) return true
        mLoadView = findViewById(R.id.base_llyLoad)
        //You need to add the layout
        if (mLoadView == null) {
            if (mFlyMain!!.childCount > 0) {
                mLoadView = View.inflate(getCurContext(), R.layout.in_loading, null)
                mFlyMain!!.addView(mLoadView, 0)
            } else
                mLoadView = View.inflate(getCurContext(), R.layout.in_loading, mFlyMain)
        }
        llyLoad = findViewById(R.id.base_llyLoad)
        proLoading = findViewById(R.id.base_proLoading)
        imgMessage = findViewById(R.id.base_imgMessage)
        txtMessage = findViewById(R.id.base_txtMessage)
        return true
    }

    override fun showLoading(resId: Int) {
        showLoading(getCurContext().getString(resId))
    }

    override fun showLoading(msg: String) {
        if (initLoadView()) {
            val count = mFlyMain!!.childCount
            for (i in 0 until count) {
                val v = mFlyMain!!.getChildAt(i)
                v.visibility = View.GONE
            }
            mLoadView?.visibility = View.VISIBLE
            llyLoad?.visibility = View.VISIBLE
            proLoading?.visibility = View.VISIBLE
            imgMessage?.visibility = View.GONE
            txtMessage?.visibility = View.VISIBLE
            txtMessage?.text = msg
        }
    }

    override fun showNoData(resId: Int, drawId: Int) {
        showNoData(getCurContext().getString(resId), drawId)
    }

    override fun showNoData(msg: String, drawId: Int) {
        if (initLoadView()) {
            val count = mFlyMain!!.childCount
            for (i in 0 until count) {
                val v = mFlyMain!!.getChildAt(i)
                v.visibility = View.GONE
            }
            mLoadView?.visibility = View.VISIBLE
            llyLoad?.visibility = View.VISIBLE
            proLoading?.visibility = View.GONE
            imgMessage?.visibility = View.VISIBLE
            txtMessage?.visibility = View.VISIBLE
            txtMessage?.text = msg
            imgMessage?.setImageResource(drawId)
        }
    }

    override fun showCView() {
        if (initLoadView()) {
            val count = mFlyMain!!.childCount
            for (i in 0 until count) {
                val v = mFlyMain!!.getChildAt(i)
                v.visibility = View.VISIBLE
            }
            mLoadView?.visibility = View.GONE
        }
    }

    override fun setLoadingDialog(loadingDialog: ILoadingDialog) {
        this.mLoadingDialog = loadingDialog
    }

    override fun showLoadingDialog(resId: Int) {
        showLoadingDialog(getCurContext().getString(resId))
    }

    override fun showLoadingDialog(msg: String) {
        if (mLoadingDialog == null)
            mLoadingDialog = LoadingDialog(getCurContext(), msg)
        mLoadingDialog?.show()
        mLoadingDialog?.updateMessage(msg)
    }

    override fun hideLoadingDialog() {
        if (mLoadingDialog != null)
            mLoadingDialog?.dismiss()
    }


    override fun hideHeader() {
        if (mToolbar != null)
            mToolbar!!.visibility = View.GONE
    }

    override fun getHeader(): View? = mToolbar

    override fun getHeaderTitle(): View? = findViewById(R.id.head_vTitle)

    override fun getHeaderLeft(): View? = findViewById(R.id.head_vLeft)

    override fun getHeaderRight(): View? = findViewById(R.id.head_vRight)

    override fun getMainView(): View? = mFlyMain

    override fun onClick(v: View?) {
        if (v == null || this.mTemplateView == null || this.mTemplateView!!.isFastClick())
            return
        when (v.id) {
            R.id.head_vLeft -> this.mTemplateView?.onLeftClick()
            R.id.head_vRight -> this.mTemplateView?.onRightClick()
            R.id.base_llyLoad -> this.mTemplateView?.onLoadViewClick()
            else -> this.mTemplateView?.onViewClick(v)
        }
    }

    override fun onDestroy() {
        this.mTemplateView = null
        this.mToolbar = null
        this.mFlyMain = null
        this.mLoadView = null
        this.llyLoad = null
        this.txtMessage = null
        this.imgMessage = null
        this.proLoading = null
        this.mLoadingDialog = null
    }

    private fun getCurContext(): Context = this.mTemplateView!!.getCurContext()

    private fun <T : View> findViewById(@IdRes id: Int, parent: View? = null): T? {
        return this.mTemplateView?.findViewById(id, parent)
    }
}