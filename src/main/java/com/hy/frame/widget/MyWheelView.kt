package com.hy.frame.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.hy.frame.R
import com.hy.frame.util.HyUtil
import com.hy.frame.util.MyLog
import java.util.*

/**
 * 滚动View
 * author HeYan
 * time 2015/12/29 13:39
 * 备注：Object 如果不是 String or Integer 里面必须有getName和setName(String)
 */
class MyWheelView : ScrollView {

    class OnWheelViewListener {
        fun onSelected(selectedIndex: Int, item: Any) {}
    }

    private var views: LinearLayout? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context)
    }

    // String[] items;
    internal var items: MutableList<*>? = null
    private var count: Int = 0

    private fun <T> getItems(): MutableList<*>? {
        return items
    }

    //    public void setItems(List<String> list) {
    //        if (null == list)
    //            return;
    //        items = list;
    ////        count = items.size();
    ////        Class<?> cls = list.get(0).getClass();
    ////        // 前面和后面补全
    ////        for (int i = 0; i < offset; i++) {
    ////                items.add(0, "");
    ////                items.add("");
    ////        }
    ////        initData();
    //    }
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> setItems(list: List<T>?) {
        if (null == list || list.size == 0)
            return
        var items = ArrayList<T>()
        items.addAll(list)
        count = items.size
        val cls = list[0].javaClass
        // 前面和后面补全
        for (i in 0..offset - 1) {
            if (cls == String::class.java) {
                items.add(0, "" as T)
                items.add("" as T)
            } else if (cls == Int::class.java) {
                items.add(0, 0 as T)
                items.add(0 as T)
            } else {
                try {
                    val t1 = cls.newInstance()
                    val m1 = cls.getMethod("setName", String::class.java)
                    m1.invoke(t1, "")
                    items.add(0, t1)
                    val t2 = cls.newInstance()
                    val m2 = cls.getMethod("setName", String::class.java)
                    m2.invoke(t2, "")
                    items.add(t2)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        initData()
    }

    var offset = OFF_SET_DEFAULT // 偏移量（需要在最前面和最后面补全）

    internal var displayItemCount: Int = 0 // 每页显示的数量

    internal var selectedIndex = OFF_SET_DEFAULT

    private fun init(context: Context) {
        // scrollView = ((ScrollView)this.getParent());
        // Logger.d(TAG, "scrollview: " + scrollView);
        // Logger.d(TAG, "parent: " + this.getParent());
        // this.setOrientation(VERTICAL);
        this.isVerticalScrollBarEnabled = false

        views = LinearLayout(context)
        views!!.orientation = LinearLayout.VERTICAL
        views!!.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        this.addView(views)

        scrollerTask = Runnable {
            if (itemHeight == 0)
                return@Runnable
            val newY = scrollY
            if (initialY - newY == 0) { // stopped
                val remainder = initialY % itemHeight
                val divided = initialY / itemHeight
                // Logger.d(TAG, "initialY: " + initialY);
                // Logger.d(TAG, "remainder: " + remainder + ", divided: " + divided);
                if (remainder == 0) {
                    selectedIndex = divided + offset
                    onSeletedCallBack()
                } else {
                    if (remainder > itemHeight / 2) {
                        this@MyWheelView.post {
                            this@MyWheelView.smoothScrollTo(0, initialY - remainder + itemHeight)
                            selectedIndex = divided + offset + 1
                            onSeletedCallBack()
                        }
                    } else {
                        this@MyWheelView.post {
                            this@MyWheelView.smoothScrollTo(0, initialY - remainder)
                            selectedIndex = divided + offset
                            onSeletedCallBack()
                        }
                    }

                }

            } else {
                initialY = scrollY
                this@MyWheelView.postDelayed(scrollerTask, newCheck.toLong())
            }
        }

    }

    internal var initialY: Int = 0

    internal var scrollerTask: Runnable? = null
    internal var newCheck = 50

    fun startScrollerTask() {

        initialY = scrollY
        this.postDelayed(scrollerTask, newCheck.toLong())
    }

    private fun initData() {
        displayItemCount = offset * 2 + 1
        views!!.removeAllViews()
        for (item in items!!) {
            views!!.addView(createView(item!!))
        }
        refreshItemView(0)
        val height = itemHeight * displayItemCount
        this.layoutParams.height = height
    }

    internal var itemHeight = 0
    private var textSize: Int = 0

    fun setTextSize(sp: Int) {
        this.textSize = sp
    }

    private fun createView(item: Any): TextView {
        val tv = TextView(context)
        tv.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        tv.setSingleLine(true)
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, (if (textSize > 0) textSize else 14).toFloat())
        tv.text = getItemStr(item)
        tv.gravity = Gravity.CENTER
        val padding = getContext().resources.getDimensionPixelSize(R.dimen.margin_normal)
        tv.setPadding(padding, padding, padding, padding)
        if (0 == itemHeight) {
            // itemHeight = tv.getMeasuredHeight();
            itemHeight = HyUtil.getViewMeasuredHeight(tv)
            // Logger.d(TAG, "itemHeight: " + itemHeight);
            views!!.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight * displayItemCount)
            val lp = this.layoutParams as LinearLayout.LayoutParams
            // this.setLayoutParams(new LinearLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT,
            // itemHeight * displayItemCount));
        }
        return tv
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)

        // Logger.d(TAG, "l: " + l + ", t: " + t + ", oldl: " + oldl + ", oldt: " + oldt);

        // try {
        // Field field = ScrollView.class.getDeclaredField("mScroller");
        // field.setAccessible(true);
        // OverScroller mScroller = (OverScroller) field.get(this);
        //
        //
        // if(mScroller.isFinished()){
        // Logger.d(TAG, "isFinished...");
        // }
        //
        // } catch (Exception e) {
        // e.printStackTrace();
        // }

        refreshItemView(t)

        if (t > oldt) {
            // Logger.d(TAG, "向下滚动");
            scrollDirection = SCROLL_DIRECTION_DOWN
        } else {
            // Logger.d(TAG, "向上滚动");
            scrollDirection = SCROLL_DIRECTION_UP

        }

    }

    private fun refreshItemView(y: Int) {
        var position = y / itemHeight + offset
        val remainder = y % itemHeight
        val divided = y / itemHeight

        if (remainder == 0) {
            position = divided + offset
        } else {
            if (remainder > itemHeight / 2) {
                position = divided + offset + 1
            }

            // if(remainder > itemHeight / 2){
            // if(scrollDirection == SCROLL_DIRECTION_DOWN){
            // position = divided + offset;
            // Logger.d(TAG, ">down...position: " + position);
            // }else if(scrollDirection == SCROLL_DIRECTION_UP){
            // position = divided + offset + 1;
            // Logger.d(TAG, ">up...position: " + position);
            // }
            // }else{
            // // position = y / itemHeight + offset;
            // if(scrollDirection == SCROLL_DIRECTION_DOWN){
            // position = divided + offset;
            // Logger.d(TAG, "<down...position: " + position);
            // }else if(scrollDirection == SCROLL_DIRECTION_UP){
            // position = divided + offset + 1;
            // Logger.d(TAG, "<up...position: " + position);
            // }
            // }
            // }

            // if(scrollDirection == SCROLL_DIRECTION_DOWN){
            // position = divided + offset;
            // }else if(scrollDirection == SCROLL_DIRECTION_UP){
            // position = divided + offset + 1;
        }

        val childSize = views!!.childCount
        for (i in 0..childSize - 1) {
            val itemView = views!!.getChildAt(i) as TextView ?: return
            if (position == i) {
                itemView.setTextColor(Color.parseColor("#454545"))
            } else {
                itemView.setTextColor(Color.parseColor("#787878"))
            }
        }
    }

    /**
     * 获取选中区域的边界
     */
    internal var selectedAreaBorder: IntArray? = null

    private fun obtainSelectedAreaBorder(): IntArray {
        if (null == selectedAreaBorder) {
            selectedAreaBorder = IntArray(2)
            selectedAreaBorder!![0] = itemHeight * offset
            selectedAreaBorder!![1] = itemHeight * (offset + 1)
        }
        return selectedAreaBorder as IntArray
    }

    private var scrollDirection = -1

    internal var paint: Paint? = null
    internal var viewWidth: Int = 0
    private var lineColor: Int = 0

    fun setLineColor(lineColor: Int) {
        this.lineColor = lineColor
    }

    override fun setBackgroundDrawable(background: Drawable?) {
        var background = background

        if (viewWidth == 0) {
            viewWidth = context!!.resources.displayMetrics.xdpi.toInt()
            Log.d(TAG, "viewWidth: " + viewWidth)
        }

        if (null == paint) {
            paint = Paint()
            paint!!.color = if (lineColor == 0) resources.getColor(R.color.txt_gray) else lineColor
            paint!!.strokeWidth = HyUtil.dip2px(context!!, 1f).toFloat()
        }

        background = object : Drawable() {
            override fun draw(canvas: Canvas) {
                canvas.drawLine(0f, obtainSelectedAreaBorder()[0].toFloat(), viewWidth.toFloat(), obtainSelectedAreaBorder()[0].toFloat(), paint!!)
                canvas.drawLine(0f, obtainSelectedAreaBorder()[1].toFloat(), viewWidth.toFloat(), obtainSelectedAreaBorder()[1].toFloat(), paint!!)
            }

            override fun setAlpha(alpha: Int) {

            }

            override fun setColorFilter(cf: ColorFilter?) {

            }

            override fun getOpacity(): Int = 0
        }

        super.setBackgroundDrawable(background)

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Logger.d(TAG, "w: " + w + ", h: " + h + ", oldw: " + oldw + ", oldh: " + oldh);
        viewWidth = w
        setBackgroundDrawable(null)
    }

    /**
     * 选中回调
     */
    private fun onSeletedCallBack() {
        if (null != onWheelViewListener) {
            if (selectedIndex >= items!!.size)
                return
            if (getItemStr(selectedIndex).length < 1) {
                val size = selectedIndex + 1
                val lines = count + offset
                if (size > count + offset) {
                    this@MyWheelView.smoothScrollTo(0, itemHeight * (lines - 1))
                    selectedIndex = lines - 1
                    onSeletedCallBack()
                }
                return
            }
            onWheelViewListener!!.onSelected(selectedIndex, items!![selectedIndex]!!)
        }
    }

    fun setSeletion(position: Int) {
        val p = position
        selectedIndex = p + offset
        this.post { this@MyWheelView.smoothScrollTo(0, p * itemHeight) }

    }

    private fun getItemStr(position: Int): String {
        return getItemStr(items!![position]!!)
    }

    private fun getItemStr(obj: Any): String {
        if (obj is String || obj is Int)
            return obj.toString() + ""
        else {
            val cls = obj.javaClass
            try {
                val method = cls.getMethod("getName")
                return method.invoke(obj).toString() + ""
            } catch (e: Exception) {
                MyLog.e(javaClass, "Object 如果不是 String or Integer 里面必须有getName和setName(String)")
                e.printStackTrace()
            }

        }
        return ""
    }

    val selectedItem: String
        get() = getItemStr(selectedIndex)

    val selectedObject: Any
        get() = items!![selectedIndex]!!

    fun getSelectedIndex(): Int {
        return selectedIndex - offset
    }

    override fun fling(velocityY: Int) {
        super.fling(velocityY / 3)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_UP) {

            startScrollerTask()
        }
        return super.onTouchEvent(ev)
    }

    var onWheelViewListener: OnWheelViewListener? = null

    companion object {
        val TAG = MyWheelView::class.java.simpleName

        val OFF_SET_DEFAULT = 2
        private val SCROLL_DIRECTION_UP = 0
        private val SCROLL_DIRECTION_DOWN = 1
    }

}
