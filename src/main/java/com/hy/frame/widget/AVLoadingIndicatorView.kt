package com.hy.frame.widget

import android.annotation.TargetApi
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils

import com.hy.frame.R
import com.hy.frame.widget.indicators.BallPulseIndicator
import com.hy.frame.widget.indicators.Indicator


class AVLoadingIndicatorView : View {

    private var mStartTime: Long = -1
    private var mPostedHide = false
    private var mPostedShow = false
    private var mDismissed = false
    private val mDelayedHide = Runnable {
        mPostedHide = false
        mStartTime = -1
        visibility = View.GONE
    }
    private val mDelayedShow = Runnable {
        mPostedShow = false
        if (!mDismissed) {
            mStartTime = System.currentTimeMillis()
            visibility = View.VISIBLE
        }
    }

    private var mMinWidth: Int = 0
    private var mMaxWidth: Int = 0
    private var mMinHeight: Int = 0
    private var mMaxHeight: Int = 0

    //need to set indicator color again if you didn't specified when you update the indicator .
    var indicator: Indicator? = null
        set(d) {
            if (indicator !== d) {
                if (indicator != null) {
                    field!!.callback = null
                    unscheduleDrawable(indicator)
                }

                field = d
                setIndicatorColor(mIndicatorColor)
                if (d != null) {
                    d.callback = this
                }
                postInvalidate()
            }
        }
    private var mIndicatorColor: Int = 0

    private var mShouldStartAnimationDrawable: Boolean = false

    constructor(context: Context) : super(context) {
        init(context, null, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs, 0, R.style.AVLoadingIndicatorView)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr, R.style.AVLoadingIndicatorView)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs, defStyleAttr, R.style.AVLoadingIndicatorView)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        mMinWidth = 24
        mMaxWidth = 48
        mMinHeight = 24
        mMaxHeight = 48

        val a = context.obtainStyledAttributes(
                attrs, R.styleable.AVLoadingIndicatorView, defStyleAttr, defStyleRes)

        mMinWidth = a.getDimensionPixelSize(R.styleable.AVLoadingIndicatorView_minWidth, mMinWidth)
        mMaxWidth = a.getDimensionPixelSize(R.styleable.AVLoadingIndicatorView_maxWidth, mMaxWidth)
        mMinHeight = a.getDimensionPixelSize(R.styleable.AVLoadingIndicatorView_minHeight, mMinHeight)
        mMaxHeight = a.getDimensionPixelSize(R.styleable.AVLoadingIndicatorView_maxHeight, mMaxHeight)
        val indicatorName = a.getString(R.styleable.AVLoadingIndicatorView_indicatorName)
        mIndicatorColor = a.getColor(R.styleable.AVLoadingIndicatorView_indicatorColor, Color.WHITE)
        setIndicator(indicatorName)
        if (indicator == null) {
            indicator = DEFAULT_INDICATOR
        }
        a.recycle()
    }


    /**
     * setIndicatorColor(0xFF00FF00)
     * or
     * setIndicatorColor(Color.BLUE)
     * or
     * setIndicatorColor(Color.parseColor("#FF4081"))
     * or
     * setIndicatorColor(0xFF00FF00)
     * or
     * setIndicatorColor(getResources().getColor(android.R.color.black))
     * @param color Color
     */
    fun setIndicatorColor(color: Int) {
        this.mIndicatorColor = color
        indicator!!.color = color
    }


    /**
     * You should pay attention to pass this parameter with two way:
     * for example:
     * 1. Only class Name,like "SimpleIndicator".(This way would use default package name with
     * "com.wang.avi.indicators")
     * 2. Class name with full package,like "com.my.android.indicators.SimpleIndicator".
     * @param indicatorName the class must be extend Indicator .
     */
    fun setIndicator(indicatorName: String?) {
        if (TextUtils.isEmpty(indicatorName)) {
            return
        }
        val drawableClassName = StringBuilder()
        if (!indicatorName!!.contains(".")) {
            val defaultPackageName = javaClass.`package`.name
            drawableClassName.append(defaultPackageName)
                    .append(".indicators")
                    .append(".")
        }
        drawableClassName.append(indicatorName)
        try {
            val drawableClass = Class.forName(drawableClassName.toString())
            val indicator = drawableClass.newInstance() as Indicator
            this.indicator = indicator
        } catch (e: ClassNotFoundException) {
            Log.e(TAG, "Didn't find your class , check the name again !")
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

    }

    fun smoothToShow() {
        startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))
        visibility = View.VISIBLE
    }

    fun smoothToHide() {
        startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out))
        visibility = View.GONE
    }

    fun hide() {
        mDismissed = true
        removeCallbacks(mDelayedShow)
        val diff = System.currentTimeMillis() - mStartTime
        if (diff >= MIN_SHOW_TIME || mStartTime == -1L) {
            // The progress spinner has been shown long enough
            // OR was not shown yet. If it wasn't shown yet,
            // it will just never be shown.
            visibility = View.GONE
        } else {
            // The progress spinner is shown, but not long enough,
            // so put a delayed message in to hide it when its been
            // shown long enough.
            if (!mPostedHide) {
                postDelayed(mDelayedHide, MIN_SHOW_TIME - diff)
                mPostedHide = true
            }
        }
    }

    fun show() {
        // Reset the start time.
        mStartTime = -1
        mDismissed = false
        removeCallbacks(mDelayedHide)
        if (!mPostedShow) {
            postDelayed(mDelayedShow, MIN_DELAY.toLong())
            mPostedShow = true
        }
    }

    override fun verifyDrawable(who: Drawable): Boolean {
        return who === indicator || super.verifyDrawable(who)
    }

    internal fun startAnimation() {
        if (visibility != View.VISIBLE) {
            return
        }

        if (indicator != null) {
            mShouldStartAnimationDrawable = true
        }
        postInvalidate()
    }

    internal fun stopAnimation() {
        if (indicator != null) {
            indicator!!.stop()
            mShouldStartAnimationDrawable = false
        }
        postInvalidate()
    }

    override fun setVisibility(v: Int) {
        if (visibility != v) {
            super.setVisibility(v)
            if (v == View.GONE || v == View.INVISIBLE) {
                stopAnimation()
            } else {
                startAnimation()
            }
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            stopAnimation()
        } else {
            startAnimation()
        }
    }

    override fun invalidateDrawable(dr: Drawable) {
        if (verifyDrawable(dr)) {
            val dirty = dr.bounds
            val scrollX = scrollX + paddingLeft
            val scrollY = scrollY + paddingTop

            invalidate(dirty.left + scrollX, dirty.top + scrollY,
                    dirty.right + scrollX, dirty.bottom + scrollY)
        } else {
            super.invalidateDrawable(dr)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        updateDrawableBounds(w, h)
    }

    private fun updateDrawableBounds(width: Int, height: Int) {
        var w = width
        var h = height
        // onDraw will translate the canvas so we draw starting at 0,0.
        // Subtract out padding for the purposes of the calculations below.
        w -= paddingRight + paddingLeft
        h -= paddingTop + paddingBottom

        var right = w
        var bottom = h
        var top = 0
        var left = 0

        if (indicator != null) {
            // Maintain aspect ratio. Certain kinds of animated drawables
            // get very confused otherwise.
            val intrinsicWidth = indicator!!.intrinsicWidth
            val intrinsicHeight = indicator!!.intrinsicHeight
            val intrinsicAspect = intrinsicWidth.toFloat() / intrinsicHeight
            val boundAspect = w.toFloat() / h
            if (intrinsicAspect != boundAspect) {
                if (boundAspect > intrinsicAspect) {
                    // New width is larger. Make it smaller to match height.
                    val nw = (h * intrinsicAspect).toInt()
                    left = (w - nw) / 2
                    right = left + nw
                } else {
                    // New height is larger. Make it smaller to match width.
                    val nh = (w * (1 / intrinsicAspect)).toInt()
                    top = (h - nh) / 2
                    bottom = top + nh
                }
            }
            indicator!!.setBounds(left, top, right, bottom)
        }
    }

    @Synchronized override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawTrack(canvas)
    }

    internal fun drawTrack(canvas: Canvas) {
        val d = indicator
        if (d != null) {
            // Translate canvas so a indeterminate circular progress bar with padding
            // rotates properly in its animation
            val saveCount = canvas.save()

            canvas.translate(paddingLeft.toFloat(), paddingTop.toFloat())

            d.draw(canvas)
            canvas.restoreToCount(saveCount)

            if (mShouldStartAnimationDrawable) {
                (d as Animatable).start()
                mShouldStartAnimationDrawable = false
            }
        }
    }

    @Synchronized override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var dw = 0
        var dh = 0

        val d = indicator
        if (d != null) {
            dw = Math.max(mMinWidth, Math.min(mMaxWidth, d.intrinsicWidth))
            dh = Math.max(mMinHeight, Math.min(mMaxHeight, d.intrinsicHeight))
        }

        updateDrawableState()

        dw += paddingLeft + paddingRight
        dh += paddingTop + paddingBottom

        val measuredWidth = View.resolveSizeAndState(dw, widthMeasureSpec, 0)
        val measuredHeight = View.resolveSizeAndState(dh, heightMeasureSpec, 0)
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        updateDrawableState()
    }

    private fun updateDrawableState() {
        val state = drawableState
        if (indicator != null && indicator!!.isStateful) {
            indicator!!.state = state
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun drawableHotspotChanged(x: Float, y: Float) {
        super.drawableHotspotChanged(x, y)

        if (indicator != null) {
            indicator!!.setHotspot(x, y)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimation()
        removeCallbacks()
    }

    override fun onDetachedFromWindow() {
        stopAnimation()
        // This should come after stopAnimation(), otherwise an invalidate message remains in the
        // queue, which can prevent the entire view hierarchy from being GC'ed during a rotation
        super.onDetachedFromWindow()
        removeCallbacks()
    }

    private fun removeCallbacks() {
        removeCallbacks(mDelayedHide)
        removeCallbacks(mDelayedShow)
    }

    companion object {

        private val TAG = "AVLoadingIndicatorView"

        private val DEFAULT_INDICATOR = BallPulseIndicator()

        private val MIN_SHOW_TIME = 500 // ms
        private val MIN_DELAY = 500 // ms
    }


}
