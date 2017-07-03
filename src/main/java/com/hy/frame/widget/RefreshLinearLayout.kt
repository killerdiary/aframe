package com.hy.frame.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.*
import com.hy.frame.R
import com.hy.frame.util.HyUtil
import com.hy.frame.util.MyLog

/**
 * 带刷新的LinearLayout
 * @author HeYan
 * @time 2014年12月31日 下午12:29:10
 */
class RefreshLinearLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private var scroller: Scroller? = null

    private var headView: View? = null
    private var imgHeadArrow: ImageView? = null

    private var proHead: ProgressBar? = null
    private var txtHeadHint: TextView? = null
    private var txtHeadUpdateTime: TextView? = null
    private var headMarginTop: Int = 0

    private var animation: RotateAnimation? = null
    private var reverseAnimation: RotateAnimation? = null

    private var listener: RefreshListener? = null

    private val refreshTime: Long? = null
    private val lastX: Int = 0
    private var lastY: Int = 0
    // 拉动标记
    private val isDragging = false
    // 是否可刷新标记
    private var isRefreshEnabled = true
    // 在刷新中标记
    private var isRefreshing = false

    init {
        init(context)
        initAnim()
    }

    private fun init(context: Context) {
        orientation = LinearLayout.VERTICAL
        scroller = Scroller(context)
        headView = LayoutInflater.from(context).inflate(R.layout.in_lv_header, null) as LinearLayout
        imgHeadArrow = HyUtil.findView(R.id.lv_imgHeadArrow, headView)
        proHead = HyUtil.findView(R.id.lv_proHead, headView)
        txtHeadHint = HyUtil.findView(R.id.lv_txtHeadHint, headView)
        txtHeadUpdateTime = HyUtil.findView(R.id.lv_txtHeadUpdateTime, headView)
        // measureView(headView);
        // headerHeight = headView.getMeasuredHeight();
        val headerHeight = resources.getDimensionPixelSize(R.dimen.lv_heigth)
        headMarginTop = -headerHeight
        // headView.setPadding(0, -1 * headerHeight, 0, 0);
        headView!!.invalidate()
        // addHeaderView(headView, null, false);
        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, -headerHeight)
        lp.topMargin = headMarginTop
        lp.gravity = Gravity.CENTER
        addView(headView, lp)
    }

    private fun initAnim() {
        animation = RotateAnimation(0f, -180f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f)
        animation!!.interpolator = LinearInterpolator()
        animation!!.duration = 250
        animation!!.fillAfter = true
        reverseAnimation = RotateAnimation(-180f, 0f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f)
        reverseAnimation!!.interpolator = LinearInterpolator()
        reverseAnimation!!.duration = 200
        reverseAnimation!!.fillAfter = true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        val y = event.rawY.toInt()

        when (event.action) {
            MotionEvent.ACTION_DOWN ->
                // 记录下y坐标
                lastY = y

            MotionEvent.ACTION_MOVE -> {
                MyLog.i("ACTION_MOVE")
                // y移动坐标
                val m = y - lastY
                if (m < 6 && m > -1 || !isDragging) {
                    doMovement(m)
                }
                // 记录下此刻y坐标
                this.lastY = y
            }

            MotionEvent.ACTION_UP -> {
                MyLog.i("ACTION_UP")

                fling()
            }
        }
        return true
    }

    /**
     * up事件处理
     */
    private fun fling() {
        val lp = headView!!.layoutParams as LinearLayout.LayoutParams
        MyLog.i("fling()" + lp.topMargin)
        if (lp.topMargin > 0) {// 拉到了触发可刷新事件
            refresh()
        } else {
            returnInitState()
        }
    }

    private fun returnInitState() {
        val lp = this.headView!!.layoutParams as LinearLayout.LayoutParams
        val i = lp.topMargin
        scroller!!.startScroll(0, i, 0, headMarginTop)
        invalidate()
    }

    private fun refresh() {
        val lp = this.headView!!.layoutParams as LinearLayout.LayoutParams
        val i = lp.topMargin
        imgHeadArrow!!.clearAnimation()
        imgHeadArrow!!.visibility = View.GONE
        proHead!!.visibility = View.VISIBLE
        txtHeadHint!!.setText(R.string.refresh_doing)
        scroller!!.startScroll(0, i, 0, 0 - i)
        invalidate()
        if (listener != null) {
            listener!!.onRefresh(this)
            isRefreshing = true
        }
    }

    override fun computeScroll() {
        if (scroller!!.computeScrollOffset()) {
            val i = this.scroller!!.currY
            val lp = this.headView!!.layoutParams as LinearLayout.LayoutParams
            val k = Math.max(i, headMarginTop)
            lp.topMargin = k
            this.headView!!.layoutParams = lp
            this.headView!!.invalidate()
            invalidate()
        }
    }

    /**
     * 下拉move事件处理

     * @param moveY
     */
    private fun doMovement(moveY: Int) {
        val lp = headView!!.layoutParams as LinearLayout.LayoutParams
        if (moveY > 0) {
            // 获取view的上边距
            val f1 = lp.topMargin.toFloat()
            val f2 = moveY * 0.3f
            val i = (f1 + f2).toInt()
            // 修改上边距
            lp.topMargin = i
            // 修改后刷新
            headView!!.layoutParams = lp
            headView!!.invalidate()
            invalidate()
        }
        proHead!!.visibility = View.GONE
        imgHeadArrow!!.visibility = View.VISIBLE
        if (lp.topMargin > 0) {
            txtHeadHint!!.setText(R.string.refresh_release_text)
            imgHeadArrow!!.setImageResource(R.mipmap.refresh_arrow_top)
        } else {
            txtHeadHint!!.setText(R.string.refresh_down_text)
            imgHeadArrow!!.clearAnimation()
            // imgHeadArrow.setImageResource(R.drawable.refresh_arrow_get);
            imgHeadArrow!!.startAnimation(animation)

        }

    }

    fun setRefreshEnabled(b: Boolean) {
        this.isRefreshEnabled = b
    }

    fun setListener(listener: RefreshListener) {
        this.listener = listener
        proHead!!.visibility = View.GONE
        txtHeadUpdateTime!!.text = resources.getString(R.string.refresh_last_time) + HyUtil.nowTime
    }

    // /**
    // * 刷新时间
    // *
    // * @param refreshTime2
    // */
    // private void setRefreshTime(Long time) {
    //
    // }

    /**
     * 结束刷新事件
     */
    fun finishRefresh() {
        MyLog.i("执行了=====finishRefresh")
        imgHeadArrow!!.visibility = View.VISIBLE
        proHead!!.visibility = View.GONE
        txtHeadUpdateTime!!.text = resources.getString(R.string.refresh_last_time) + HyUtil.nowTime
        val lp = this.headView!!.layoutParams as LinearLayout.LayoutParams
        val i = lp.topMargin
        scroller!!.startScroll(0, i, 0, headMarginTop)
        invalidate()
        isRefreshing = false
    }

    /*
     * 该方法一般和ontouchEvent 一起用 (non-Javadoc)
     *
     * @see android.view.ViewGroup#onInterceptTouchEvent(android.view.MotionEvent)
     */
    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {

        val action = e.action
        val y = e.rawY.toInt()
        when (action) {
            MotionEvent.ACTION_DOWN -> lastY = y

            MotionEvent.ACTION_MOVE -> {
                // y移动坐标
                val m = y - lastY

                // 记录下此刻y坐标
                this.lastY = y
                if (m > 6 && canScroll()) {
                    return true
                }
            }
            MotionEvent.ACTION_UP -> {
            }

            MotionEvent.ACTION_CANCEL -> {
            }
        }
        return false
    }

    private fun canScroll(): Boolean {
        val childView: View
        if (childCount > 1) {
            childView = this.getChildAt(1)
            if (childView is ListView) {
                val top = childView.getChildAt(0).top
                val pad = childView.listPaddingTop
                return Math.abs(top - pad) < 3 && childView.firstVisiblePosition == 0
            } else if (childView is ScrollView) {
                return childView.scrollY == 0
            }
        }
        return false
    }

    /**
     * 刷新监听接口
     */
    interface RefreshListener {
        fun onRefresh(view: RefreshLinearLayout)
    }

}
