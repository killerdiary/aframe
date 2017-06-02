package com.hy.frame.widget

import android.content.Context
import android.support.v4.view.ViewPager
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import android.widget.LinearLayout
import android.widget.RelativeLayout

import com.bumptech.glide.Glide
import com.hy.frame.R
import com.hy.frame.adapter.ViewPagerAdapter
import com.hy.frame.util.HyUtil
import com.hy.frame.util.MyLog

import java.util.ArrayList

/**
 * 显示ViewPager功能

 * @author HeYan
 *
 * @time 2014年9月4日 下午2:37:10
 */
class MyScrollView  constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RelativeLayout(context, attrs, defStyle), OnPageChangeListener, Runnable {
    private var isOpenAuto: Boolean = false
    private var timer: Long = 0// 间隔时间
    private var scrollCount: Int = 0// 次数
    var viewPager: ViewPager? = null
        private set
    var llyPoint: LinearLayout? = null
        private set
    private var views: MutableList<View>? = null
    private var adapter: ViewPagerAdapter? = null
    private var isDrag: Boolean = false
    private var init: Boolean = false

    init {
        init(context)
    }

    private fun init(context: Context) {
        if (init) return
        init = true
        viewPager = ViewPager(context)
        viewPager!!.setOnPageChangeListener(this)
        val rlp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        rlp.alignWithParent = true
        rlp.addRule(RelativeLayout.ALIGN_TOP)
        addView(viewPager, rlp)
        // vPager.addView(llyContainer, llp);
        llyPoint = LinearLayout(context)
        val prlp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, resources.getDimensionPixelSize(R.dimen.gallery_point_height))
        prlp.alignWithParent = true
        prlp.addRule(RelativeLayout.ALIGN_BOTTOM)
        llyPoint!!.gravity = Gravity.CENTER
        llyPoint!!.setPadding(HyUtil.dip2px(context, 2f), HyUtil.dip2px(context, 2f), HyUtil.dip2px(context, 2f), HyUtil.dip2px(context, 2f))
        addView(llyPoint, prlp)
    }

    fun clear() {
        if (views != null) {
            views!!.clear()
            adapter!!.refresh(views!!)
        }
    }

    fun addImage(drawId: Int) {
        val img = ImageView(context)
        img.scaleType = ScaleType.FIT_XY
        img.setImageResource(drawId)
        img.layoutParams = ViewGroup.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        img.setOnClickListener(clickListener)
        addPage(img)

    }

    fun addImage(path: String?) {
        if (path == null)
            return
        val img = ImageView(context)
        img.scaleType = ScaleType.FIT_XY
        img.layoutParams = ViewGroup.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        Glide.with(context).load(path).into(img)
        img.setOnClickListener(clickListener)
        addPage(img)
    }

    private var clickListener: View.OnClickListener? = null

    override fun setOnClickListener(l: View.OnClickListener?) {
        this.clickListener = l
    }

    /**
     * 添加子Page 显示时需要调用show

     * @param v
     */
    fun addPage(v: View) {
        if (views == null)
            views = ArrayList<View>()
        views!!.add(v)
        addPoint()
    }

     fun show(views: MutableList<View>) {
        this.views = views
        if (adapter == null) {
            adapter = ViewPagerAdapter(views)
            viewPager!!.adapter = adapter
        } else
            adapter!!.refresh(views)
    }

    private var pointResId: Int = 0

    fun setPointResId(pointResId: Int) {
        this.pointResId = pointResId
    }

    private fun addPoint() {
        val img = CircleImageView(context)
        val width = HyUtil.dip2px(context, 8f)
        val llp = LinearLayout.LayoutParams(width, width)
        if (pointResId != 0)
            img.setBackgroundResource(pointResId)
        else
            img.setBackgroundResource(R.drawable.btn_circle_selector)
        val padding = HyUtil.dip2px(context, 4f)
        llp.setMargins(padding, padding, padding, padding)
        if (llyPoint!!.childCount == 0) {
            img.isSelected = true
        }
        llyPoint!!.addView(img, llp)
    }

    fun setPointGravity(gravity: Int) {
        if (llyPoint != null)
            llyPoint!!.gravity = gravity
    }

    /**
     * 隐藏后不再显示
     */
    fun hidePoint() {
        if (llyPoint != null)
            llyPoint!!.visibility = View.GONE
    }

    private val count: Int
        get() = if (views == null) 0 else views!!.size

    override fun onPageScrollStateChanged(state: Int) {
        if (state == ViewPager.SCROLL_STATE_DRAGGING)
            isDrag = true
        if (state == ViewPager.SCROLL_STATE_IDLE)
            isDrag = false
        //MyLog.e("onPageScrollStateChanged " + state);
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        //MyLog.e("onPageScrolled " + position + "|" + positionOffset + "|" + positionOffsetPixels);
    }

    val postion: Int
        get() {
            if (viewPager != null) {
                return viewPager!!.currentItem
            }
            return 0
        }

    override fun onPageSelected(position: Int) {
        isDrag = false
        val size = llyPoint!!.childCount
        for (i in 0..size - 1) {
            val v = llyPoint!!.getChildAt(i)
            if (position == i)
                v.isSelected = true
            else
                v.isSelected = false
        }
        if (scrollCount >= 3)
            scrollCount = 2
        if (listener != null) listener!!.onViewChange(views!!.size, position + 1)
    }

    /**
     * 开启倒计时

     * @param interval 倒计时时间(秒)
     */
     fun startAuto(interval: Int = DEFAULT_INTERVAL) {
        if (adapter == null) {
            MyLog.e("NO CALLED SHOW!")
            return
        }
        // 防止重复开启
        if (isOpenAuto)
            return
        isOpenAuto = true
        timer = interval.toLong()
        run()
    }

    fun closeAuto() {
        isOpenAuto = false
    }

    override fun run() {
        // MyLog.e("isActivated:"+isActivated());
        // MyLog.e("isAttachedToWindow:"+isAttachedToWindow());
        // MyLog.e("isTransitionGroup:"+isTransitionGroup());
        // MyLog.e("isShown:" + isShown());
        // MyLog.e("isEnabled:" + isEnabled());
        // MyLog.e("isOpaque:" + isOpaque());
        // MyLog.e("isHovered:"+isHovered());
        // MyLog.e("isInLayout:"+isInLayout());
        scrollCount++
        postDelayed(this, timer)
        if (isDrag)
            return
        if (scrollCount < 3)
            return
        if (!isOpenAuto || viewPager == null)
            return
        if (!isShown || count <= 1)
            return
        val pager = viewPager!!.currentItem
        //MyLog.e("pager:" + pager + "| getCount:" + getCount());
        if (pager < count - 1)
            viewPager!!.currentItem = pager + 1
        else
            viewPager!!.currentItem = 0
    }

    private var listener: IScrollListener? = null

    fun setListener(listener: IScrollListener) {
        this.listener = listener
    }

    interface IScrollListener {
        fun onViewChange(all: Int, position: Int)
    }

    companion object {
        private val DEFAULT_INTERVAL = 3000// 间隔时间3秒
    }
}
/**
 * 开启倒计时
 */