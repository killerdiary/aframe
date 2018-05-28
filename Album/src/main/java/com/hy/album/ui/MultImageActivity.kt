package com.hy.album.ui

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.hy.album.R
import com.hy.frame.adapter.ViewPagerAdapter
import com.hy.frame.app.BaseActivity
import com.hy.frame.bean.ResultInfo
import com.hy.frame.mvp.IBasePresenter
import com.hy.http.IMyHttpListener

/**
 * 多图浏览
 * @author HeYan
 * @time 2017/7/6 16:22
 */
class MultImageActivity : BaseActivity<IBasePresenter>(), ViewPager.OnPageChangeListener {

    private var llyPoint: LinearLayout? = null
    private var vPager: ViewPager? = null
    private var datas: ArrayList<String>? = null
    private var adapter: ViewPagerAdapter? = null
    private var views: MutableList<View>? = null
    private var position: Int = 0
    override fun isPortrait(): Boolean = false
    override fun isSingleLayout(): Boolean = true
    override fun isTranslucentStatus(): Boolean = true
    override fun isPermissionDenied(): Boolean = false
    override fun getLayoutId(): Int = R.layout.act_image_mult

    override fun initView() {
        vPager = findViewById(R.id.image_mult_vPager)
        llyPoint = findViewById(R.id.image_mult_llyPoint)
        vPager?.addOnPageChangeListener(this)
    }

    override fun initData() {
        if (bundle == null || !bundle!!.containsKey(ARG_DATA)) {
            finish()
            return
        }
        datas = bundle!!.getStringArrayList(ARG_DATA) ?: return
        position = bundle!!.getInt(ARG_POSITION, 0)
        views = ArrayList()
        llyPoint?.removeAllViews()
        val itemWidth = resources.getDimensionPixelSize(R.dimen.vpager_point_width)
        val leftMargin = resources.getDimensionPixelSize(R.dimen.vpager_point_margin_left)
        for (item in datas!!) {
            val v = View.inflate(getCurContext(), R.layout.item_image_mult, null)
            val img = findViewById<PhotoView>(R.id.image_mult_i_imgShow, v)!!
            Glide.with(getCurContext()).load(item).into(img)
            v.layoutParams = ViewGroup.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
            img.setOnClickListener(this)
            views?.add(v)
            val vp: View = View.inflate(getCurContext(), R.layout.item_vpager_point, null)
            val llp = LinearLayout.LayoutParams(itemWidth, itemWidth)
            llp.leftMargin = leftMargin
            llyPoint!!.addView(vp, llp)
        }
        llyPoint?.getChildAt(0)?.isSelected = true
        adapter = ViewPagerAdapter(views)
        vPager?.adapter = adapter
        if (position > 0) {
            vPager?.currentItem = position
            llyPoint?.getChildAt(position)?.isSelected = true
        }
        if (views!!.size <= 1)
            llyPoint?.visibility = View.GONE
    }

    override fun onViewClick(v: View) {
        finish()
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        for (i in 0 until views!!.size) {
            llyPoint?.getChildAt(i)?.isSelected = false
        }
        llyPoint?.getChildAt(position)?.isSelected = true
    }

    override fun getRequestListener(): IMyHttpListener? = null

    override fun buildPresenter(): IBasePresenter? = null

    companion object {
        private const val ARG_DATA = "ARG_DATA"
        private const val ARG_POSITION = "arg_position"

        fun newArguments(datas: ArrayList<String>, position: Int = 0): Bundle {
            val args = Bundle()
            args.putStringArrayList(ARG_DATA, datas)
            args.putInt(ARG_POSITION, position)
            return args
        }
    }
}