package com.hy.frame.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.*
import android.widget.AbsListView.OnScrollListener
import com.hy.frame.R
import com.hy.frame.util.HyUtil
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author heyan
 * @title 自定义ListView(下拉刷新，点击查看更多)
 * @time 2013-6-27 下午2:59:52
 */
@Deprecated("不建议使用ListView")
class MyListView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ListView(context, attrs, defStyleAttr), OnScrollListener {
    private var inflater: LayoutInflater? = null

    private var headView: LinearLayout? = null
    private var imgHeadArrow: ImageView? = null
    private var proHead: ProgressBar? = null
    private var txtHeadHint: TextView? = null
    private var txtHeadUpdateTime: TextView? = null
    private var headerHeight: Int = 0

    private var footView: LinearLayout? = null
    private var imgFootArrow: ImageView? = null
    private var proFoot: ProgressBar? = null
    private var txtFootHint: TextView? = null
    private var txtFootUpdateTime: TextView? = null
    private var footerHeight: Int = 0

    private var animation: RotateAnimation? = null
    private var reverseAnimation: RotateAnimation? = null

    private var isRecored: Boolean = false
    private var startY: Int = 0
    private var state: Int = 0
    private var isBack: Boolean = false
    private var pullDownRefreshListener: OnRefreshListener? = null
    private var pullUpRefreshListener: OnRefreshListener? = null
    /**
     * 是否开启下拉刷新
     */
    var isPullDownRefresh: Boolean = false
    /**
     * 是否开启上拉加载更多
     */
    var isPullUpRefresh: Boolean = false

    /**
     * 是否滚动到顶部
     */
    private var scrollTop: Boolean = false
    /**
     * 是否滚动到底部
     */
    private var scrollBottom: Boolean = false
    /**
     * 滚动的方向
     */
    private var direction: Int = 0

    init {
        init(context)
    }

    /**
     * 初始化头部和底部

     * @param context
     */
    private fun init(context: Context) {
        // setCacheColorHint(android.R.color.transparent);
        inflater = LayoutInflater.from(context)
        initHeader()
        initFooter()
        initAnim()
        setOnScrollListener(this)
        state = FLAG_DONE
        isPullDownRefresh = false
        isPullUpRefresh = false
    }

    private fun initHeader() {
        headView = inflater!!.inflate(R.layout.in_lv_header, null) as LinearLayout
        imgHeadArrow = HyUtil.findViewById<ImageView>(R.id.lv_imgHeadArrow, headView)
        proHead = HyUtil.findViewById<ProgressBar>(R.id.lv_proHead, headView)
        txtHeadHint = HyUtil.findViewById<TextView>(R.id.lv_txtHeadHint, headView)
        txtHeadUpdateTime = HyUtil.findViewById<TextView>(R.id.lv_txtHeadUpdateTime, headView)
        // measureView(headView);
        // headerHeight = headView.getMeasuredHeight();
        headerHeight = resources.getDimensionPixelSize(R.dimen.lv_heigth)
        headView!!.setPadding(0, -1 * headerHeight, 0, 0)
        headView!!.invalidate()
        //addHeaderView(headView, null, false);
        //        addHeaderView(headView);
    }

    private fun initFooter() {
        footView = inflater!!.inflate(R.layout.in_lv_footer, null) as LinearLayout
        imgFootArrow = HyUtil.findViewById<ImageView>(R.id.lv_imgFootArrow, footView)
        proFoot = HyUtil.findViewById<ProgressBar>(R.id.lv_proFoot, footView)
        txtFootHint = HyUtil.findViewById<TextView>(R.id.lv_txtFootHint, footView)
        txtFootUpdateTime = HyUtil.findViewById<TextView>(R.id.lv_txtFootUpdateTime, footView)
        // measureView(footView);
        // footerHeight = footView.getMeasuredHeight();
        footerHeight = resources.getDimensionPixelSize(R.dimen.lv_heigth)
        footView!!.setPadding(0, 0, 0, -1 * headerHeight)
        footView!!.invalidate()
        addFooterView(footView, null, false)
    }

    private fun initAnim() {
        animation = RotateAnimation(0f, -180f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f)
        animation!!.interpolator = LinearInterpolator()
        animation!!.duration = 250
        animation!!.fillAfter = true
        reverseAnimation = RotateAnimation(-180f, 0f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f)
        reverseAnimation!!.interpolator = LinearInterpolator()
        reverseAnimation!!.duration = 200
        reverseAnimation!!.fillAfter = true
    }

    override fun onScroll(arg0: AbsListView, firstVisiableItem: Int,
                          visibleItemCount: Int, totalItemCount: Int) {
        if (state == FLAG_DONE) {
            scrollTop = firstVisiableItem == 0
            // MyLog.d("最后位置: " + getLastVisiblePosition() + " 总：" + totalItemCount);
            scrollBottom = lastVisiblePosition == totalItemCount - 1
        }
    }

    override fun onScrollStateChanged(arg0: AbsListView, arg1: Int) {}

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // 正在刷新
        if (isPullDownRefresh || isPullUpRefresh) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (scrollTop || scrollBottom) {
                        // MyLog.e("ACTION_DOWN");
                        if (!isRecored) {
                            // 开始检测
                            isRecored = true
                            startY = event.y.toInt()
                            direction = 0
                            // MyLog.e("ACTION_DOWN 记录当前位置:" + startY);
                        }
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (scrollTop || scrollBottom) {
                        val tempY = event.y.toInt()
                        // MyLog.e("ACTION_MOVE");
                        if (!isRecored) {
                            isRecored = true
                            startY = tempY
                            direction = 0
                            // MyLog.e("ACTION_MOVE 记录当前位置:" + startY);
                        }
                        // 检测开启，没有刷新，没有加载
                        if (state != FLAG_REFRESHING && isRecored) {
                            // 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动
                            val distance = tempY - startY
                            val abs = Math.abs(distance)
                            if (state == FLAG_DONE) {
                                if (distance != 0) {
                                    if (distance > 0)
                                        direction = TO_DOWN
                                    else
                                        direction = TO_UP
                                    state = FLAG_PULLING
                                    changeViewByState()
                                }
                                // MyLog.e("ACTION_MOVE FLAG_DONE" + direction);
                            }
                            if (direction == TO_DOWN && !isPullDownRefresh) {
                                state = FLAG_DONE
                            } else if (direction == TO_UP && !isPullUpRefresh) {
                                state = FLAG_DONE
                            } else {
                                if (state == FLAG_PULLING) {
                                    // MyLog.e("ACTION_MOVE FLAG_PULLING " + direction);
                                    if (direction == TO_DOWN) {
                                        if (distance / RATIO >= headerHeight) {
                                            state = FLAG_RELEASE
                                            isBack = true
                                            changeViewByState()
                                        } else if (distance <= 0) {
                                            state = FLAG_DONE
                                            changeViewByState()
                                        }
                                        headView!!.setPadding(0, distance / RATIO - headerHeight, 0, 0)
                                    } else if (direction == TO_UP) {
                                        if (abs / RATIO >= footerHeight) {
                                            state = FLAG_RELEASE
                                            isBack = true
                                            changeViewByState()
                                        } else if (distance >= 0) {
                                            state = FLAG_DONE
                                            changeViewByState()
                                        }
                                        footView!!.setPadding(0, 0, 0, abs / RATIO - 1 * footerHeight)
                                    }
                                }
                                // 可以松手去刷新了
                                if (state == FLAG_RELEASE) {
                                    // MyLog.e("ACTION_MOVE 可以松手去刷新了");
                                    if (direction == TO_DOWN) {
                                        // setSelection(0);
                                        // 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
                                        if (distance / RATIO < headerHeight && distance > 0) {
                                            state = FLAG_PULLING
                                            changeViewByState()
                                        } else if (distance <= 0) {
                                            state = FLAG_DONE
                                            changeViewByState()
                                        }
                                        headView!!.setPadding(0, distance / RATIO - headerHeight, 0, 0)
                                    } else if (direction == TO_UP) {
                                        if (abs / RATIO < footerHeight && distance < 0) {
                                            state = FLAG_PULLING
                                            changeViewByState()
                                        } else if (distance >= 0) {
                                            state = FLAG_DONE
                                            changeViewByState()
                                        }
                                        footView!!.setPadding(0, 0, 0, abs / RATIO - 1 * footerHeight)
                                    }
                                }
                            }
                        }
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (!(scrollTop || scrollBottom)) {
                    } else {
                        if (state != FLAG_REFRESHING) {
                            if (state == FLAG_DONE) {
                            }
                            if (state == FLAG_PULLING) {
                                state = FLAG_DONE
                                changeViewByState()
                            }
                            if (state == FLAG_RELEASE) {
                                state = FLAG_REFRESHING
                                changeViewByState()
                                if (direction == TO_DOWN) {
                                    if (pullDownRefreshListener != null) {
                                        pullDownRefreshListener!!.onRefresh(this, false)
                                    }
                                } else if (direction == TO_UP) {
                                    if (pullUpRefreshListener != null) {
                                        pullUpRefreshListener!!.onRefresh(this, false)
                                    }
                                }

                            }
                        }
                        isRecored = false
                        isBack = false
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun changeViewByState() {
        if (direction == TO_DOWN) {
            when (state) {
                FLAG_RELEASE -> {
                    imgHeadArrow!!.visibility = View.VISIBLE
                    proHead!!.visibility = View.GONE
                    txtHeadHint!!.visibility = View.VISIBLE
                    txtHeadUpdateTime!!.visibility = View.VISIBLE
                    imgHeadArrow!!.clearAnimation()
                    // imgHeadArrow.setImageResource(R.drawable.refresh_arrow_get);
                    imgHeadArrow!!.startAnimation(animation)
                    txtHeadHint!!.text = "请释放 刷新"
                }
                FLAG_PULLING -> {
                    proHead!!.visibility = View.GONE
                    txtHeadHint!!.visibility = View.VISIBLE
                    txtHeadUpdateTime!!.visibility = View.VISIBLE
                    imgHeadArrow!!.clearAnimation()
                    imgHeadArrow!!.visibility = View.VISIBLE
                    if (isBack) {
                        isBack = false
                        imgHeadArrow!!.clearAnimation()
                        imgHeadArrow!!.startAnimation(reverseAnimation)
                        txtHeadHint!!.text = "下拉刷新"
                    } else {
                        txtHeadHint!!.text = "下拉刷新"
                    }
                }
                FLAG_REFRESHING -> {
                    headView!!.setPadding(0, 0, 0, 0)
                    proHead!!.visibility = View.VISIBLE
                    imgHeadArrow!!.clearAnimation()
                    imgHeadArrow!!.visibility = View.GONE
                    txtHeadHint!!.text = "正在加载中 ..."
                    txtHeadUpdateTime!!.visibility = View.VISIBLE
                }
                FLAG_DONE -> {
                    headView!!.setPadding(0, -1 * headerHeight, 0, 0)
                    proHead!!.visibility = View.GONE
                    imgHeadArrow!!.clearAnimation()
                    imgHeadArrow!!.setImageResource(R.mipmap.refresh_arrow_top)
                    txtHeadHint!!.text = "已经加载完毕 "
                    txtHeadUpdateTime!!.visibility = View.VISIBLE
                }
            }// Log.i("HyLog", "RELEASE_To_REFRESH 这是第  " + i++ + "步" + 12 +
            // "请释放 刷新");

        } else if (direction == TO_UP) {
            when (state) {
                FLAG_RELEASE -> {
                    imgFootArrow!!.visibility = View.VISIBLE
                    proFoot!!.visibility = View.GONE
                    txtFootHint!!.visibility = View.VISIBLE
                    txtFootUpdateTime!!.visibility = View.VISIBLE
                    imgFootArrow!!.clearAnimation()
                    // imgFootArrow.setImageResource(R.drawable.refresh_arrow_get);
                    imgFootArrow!!.startAnimation(animation)
                    txtFootHint!!.text = "请释放 加载更多"
                }
                FLAG_PULLING -> {
                    proFoot!!.visibility = View.GONE
                    txtFootHint!!.visibility = View.VISIBLE
                    txtFootUpdateTime!!.visibility = View.VISIBLE
                    imgFootArrow!!.clearAnimation()
                    imgFootArrow!!.visibility = View.VISIBLE
                    if (isBack) {
                        isBack = false
                        imgFootArrow!!.clearAnimation()
                        imgFootArrow!!.startAnimation(reverseAnimation)
                        txtFootHint!!.text = "上拉加载更多"
                    } else {
                        txtFootHint!!.text = "上拉加载更多"
                    }
                }
                FLAG_REFRESHING -> {
                    // footView.setPadding(0, 0, 0, 0);
                    proFoot!!.visibility = View.VISIBLE
                    imgFootArrow!!.clearAnimation()
                    imgFootArrow!!.visibility = View.GONE
                    txtFootHint!!.text = "正在加载中 ..."
                    txtFootUpdateTime!!.visibility = View.VISIBLE
                }
                FLAG_DONE -> {
                    footView!!.setPadding(0, -1 * footerHeight, 0, 0)
                    proFoot!!.visibility = View.GONE
                    imgFootArrow!!.clearAnimation()
                    imgFootArrow!!.setImageResource(R.mipmap.refresh_arrow_top)
                    txtFootHint!!.text = "已经加载完毕 "
                    txtFootUpdateTime!!.visibility = View.VISIBLE
                }
            }
        }

    }

    /**
     * 下拉刷新

     * @param refreshListener
     */
    fun setPullDownRefreshListener(refreshListener: OnRefreshListener) {
        this.pullDownRefreshListener = refreshListener
        isPullDownRefresh = true
    }

    /**
     * 上拉刷新

     * @param refreshListener
     */
    fun setPullUpRefreshListener(refreshListener: OnRefreshListener) {
        this.pullUpRefreshListener = refreshListener
        isPullUpRefresh = true
    }

    interface OnRefreshListener {

        fun onRefresh(lv: MyListView, first: Boolean)
    }

    fun onRefreshComplete() {
        state = FLAG_DONE
        if (direction == TO_DOWN) {
            txtHeadUpdateTime!!.text = "上次更新: " + nowTime
        } else if (direction == TO_UP) {
            txtFootUpdateTime!!.text = "上次更新: " + nowTime
        }
        changeViewByState()
        // Log.i("HyLog", "onRefreshComplete() 被调用。。。");
    }

    // /**
    // * 手动调用
    // *
    // * @param first
    // */
    // public void onRefresh(boolean first) {
    // if (refreshListener != null) {
    // refreshListener.onRefresh(first);
    // // Log.i("HyLog", "onRefresh被调用，这是第  " + i++ + "步");
    // }
    // }

    // public void setShowMoreListener(android.view.View.OnClickListener listener) {
    // footView.setOnClickListener(listener);
    // }

    // public void showMoreClose(boolean state) {
    // if (state)
    // footView.setPadding(0, -1 * footerHeight, 0, 0);
    // else
    // footView.setPadding(0, 0, 0, 0);
    // }

    // private void measureView(View child) {
    // ViewGroup.LayoutParams p = child.getLayoutParams();
    // if (p == null) {
    // p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.match_parent, ViewGroup.LayoutParams.WRAP_CONTENT);
    // }
    // int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
    // int lpHeight = p.height;
    // int childHeightSpec;
    // if (lpHeight > 0) {
    // childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
    // } else {
    // childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
    // }
    // child.measure(childWidthSpec, childHeightSpec);
    // }

    fun setAdapter(adapter: BaseAdapter) {
        txtHeadUpdateTime!!.text = "更新时间：" + nowTime
        txtFootUpdateTime!!.text = "更新时间：" + nowTime
        super.setAdapter(adapter)
    }

    /**
     * 手动调用刷新
     */
    fun onRefresh() {
        state = FLAG_REFRESHING
        direction = TO_DOWN
        changeViewByState()
        if (pullDownRefreshListener != null) {
            pullDownRefreshListener!!.onRefresh(this, false)
        }
    }

    // public void setRefreshable(boolean b) {
    // this.isRefreshable = b;
    // }

    /**
     * 获取当前时间Date

     * @return 现在时间(Now)
     */
    // String type = "yyyy-MM-dd HH:mm:ss";
    val nowTime: String
        get() {
            val d = Date(System.currentTimeMillis())
            val type = "HH:mm:ss"
            val formatter = SimpleDateFormat(type, Locale.CHINA)
            return formatter.format(d)
        }

    private var expandAllItem: Boolean = false

    fun setExpandAllItem(expandAllItem: Boolean) {
        this.expandAllItem = expandAllItem
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (expandAllItem) {
            val expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE shr 2, View.MeasureSpec.AT_MOST)
            super.onMeasure(widthMeasureSpec, expandSpec)
        } else
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    companion object {

        private val FLAG_DONE = 0// 完成
        private val FLAG_PULLING = 1// 拉...
        private val FLAG_REFRESHING = 2// 正在刷新
        private val FLAG_RELEASE = 3// 请释放
        // private final static int FLAG_LOADING = 4;// 加载中
        private val RATIO = 3// 移动的比例
        private val TO_UP = 1// 向上
        private val TO_DOWN = 2// 向下
    }

}