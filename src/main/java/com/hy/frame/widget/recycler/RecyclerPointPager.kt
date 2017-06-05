package com.hy.frame.widget.recycler

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout

import com.hy.frame.R
import com.hy.frame.adapter.BaseRecyclerAdapter
import com.hy.frame.util.HyUtil
import com.hy.frame.util.MyLog
import com.hy.frame.widget.CircleImageView

/**
 * com.hy.frame.view.recycler
 * author HeYan
 * time 2016/8/16 14:21
 */
class RecyclerPointPager @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RelativeLayout(context, attrs, defStyle), Runnable, RecyclerViewPager.OnPageChangedListener {
    private var isOpenAuto: Boolean = false
    private var timer: Long = 0// 间隔时间
    private var scrollCount: Int = 0// 次数
    private var rcyList: RecyclerViewPager? = null
    var llyPoint: LinearLayout? = null
        private set
    //private BitmapUtils fb;
    private var adapter: BaseRecyclerAdapter<*>? = null
    //private boolean isDrag;
    private var init: Boolean = false

    init {
        init(context)
    }

    private fun init(context: Context) {
        if (init) return
        init = true
        rcyList = RecyclerViewPager(context)
        //rcyList.setOnPageChangeListener(this);
        val rlp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        rlp.alignWithParent = true
        rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
        addView(rcyList, rlp)
        // vPager.addView(llyContainer, llp);
        llyPoint = LinearLayout(context)
        val prlp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, resources.getDimensionPixelSize(R.dimen.gallery_point_height))
        prlp.alignWithParent = true
        prlp.addRule(RelativeLayout.ALIGN_BOTTOM)
        llyPoint!!.gravity = Gravity.CENTER
        val padding = HyUtil.dip2px(context, 2f)
        llyPoint!!.setPadding(padding, padding, padding, padding)
        addView(llyPoint, prlp)
        rcyList!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rcyList!!.flingFactor = 0f
        rcyList!!.addOnPageChangedListener(this)
    }

    fun setAdapter(adapter: BaseRecyclerAdapter<*>) {
        if (this.adapter != null) {
            return
        }
        this.adapter = adapter
        llyPoint!!.removeAllViews()
        val size = this.adapter!!.itemCount
        for (i in 0..size - 1) {
            addPoint()
        }
        rcyList?.setAdapter(this.adapter!!)

    }

    fun resetPoint() {
        if (this.adapter == null) {
            return
        }
        llyPoint!!.removeAllViews()
        val size = this.adapter!!.itemCount
        for (i in 0..size - 1) {
            addPoint()
        }
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
        get() = if (adapter == null) 0 else adapter!!.itemCount

    //    @Override
    //    public void onPageScrollStateChanged(int state) {
    //        if (state == ViewPager.SCROLL_STATE_DRAGGING)
    //            isDrag = true;
    //        if (state == ViewPager.SCROLL_STATE_IDLE)
    //            isDrag = false;
    //        //MyLog.e("onPageScrollStateChanged " + state);
    //    }

    //    @Override
    //    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    //        //MyLog.e("onPageScrolled " + position + "|" + positionOffset + "|" + positionOffsetPixels);
    //    }

    val postion: Int
        get() {
            if (rcyList != null) {
                return rcyList!!.currentPosition
            }
            return 0
        }

    fun onPageSelected(position: Int) {
        val isDrag = false
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
        if (listener != null) listener!!.onViewChange(adapter!!.itemCount, position + 1)
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
        if (scrollCount < 3)
            return
        if (!isOpenAuto || adapter == null)
            return
        if (!isShown || count <= 1)
            return
        val pager = rcyList!!.currentPosition
        if (rcyList!!.scrollState != RecyclerView.SCROLL_STATE_IDLE)
            return
        //MyLog.e("pager:" + pager + "| getCount:" + getCount());
        if (pager < count - 1)
            rcyList!!.smoothScrollToPosition(pager + 1)
        else
            rcyList!!.smoothScrollToPosition(0)

    }

    private var listener: IScrollListener? = null

    fun setListener(listener: IScrollListener) {
        this.listener = listener
    }

    override fun OnPageChanged(oldPosition: Int, newPosition: Int) {
        val size = llyPoint!!.childCount
        for (i in 0..size - 1) {
            llyPoint!!.getChildAt(i).isSelected = i == newPosition
        }
        if (listener != null) {
            listener!!.onViewChange(adapter!!.itemCount, newPosition)
        }
    }

    interface IScrollListener {
        fun onViewChange(all: Int, position: Int)
    }

    companion object {
        private val DEFAULT_INTERVAL = 3000// 间隔时间3秒
    }
}