package com.hy.frame.camera

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Toast

/**
 * CaptureButton
 * @author HeYan
 * @time 2017/4/28 15:36
 */
class CaptureButton @JvmOverloads constructor(private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(mContext, attrs, defStyleAttr) {

    val TAG = "CaptureButtom"

    private val mPaint: Paint

    private var btn_center_Y: Float = 0.toFloat()
    private var btn_center_X: Float = 0.toFloat()

    private var btn_inside_radius: Float = 0.toFloat()
    private var btn_outside_radius: Float = 0.toFloat()
    //before radius
    private var btn_before_inside_radius: Float = 0.toFloat()
    private var btn_before_outside_radius: Float = 0.toFloat()
    //after radius
    private var btn_after_inside_radius: Float = 0.toFloat()
    private var btn_after_outside_radius: Float = 0.toFloat()


    private var btn_left_X: Float = 0.toFloat()
    private var btn_right_X: Float = 0.toFloat()
    private var btn_result_radius: Float = 0.toFloat()

    //state
    private var STATE_SELECTED: Int = 0
    private val STATE_LESSNESS = 0
    private val STATE_KEY_DOWN = 1
    private val STATE_CAPTURED = 2
    private val STATE_RECORD = 3
    private val STATE_PICTURE_BROWSE = 4
    private val STATE_RECORD_BROWSE = 5
    private val STATE_READYQUIT = 6
    private val STATE_RECORDED = 7

    private var key_down_Y: Float = 0.toFloat()

    private var rectF: RectF? = null
    private var progress = 0f
    private val longPressRunnable = LongPressRunnable()
    private val recordRunnable = RecordRunnable()
    private val record_anim = ValueAnimator.ofFloat(0F, 360F)
    private var mCaptureListener: CaptureListener? = null

    init {
        mPaint = Paint()
        mPaint.isAntiAlias = true
        STATE_SELECTED = STATE_LESSNESS
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        //int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        //int width = widthSize;
        Log.i(TAG, "measureWidth = " + widthSize)
        val height = widthSize / 9 * 4
        setMeasuredDimension(widthSize, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        btn_center_X = (width / 2).toFloat()
        btn_center_Y = (height / 2).toFloat()

        btn_outside_radius = (width / 9).toFloat()
        btn_inside_radius = (btn_outside_radius * 0.75).toFloat()

        btn_before_outside_radius = (width / 9).toFloat()
        btn_before_inside_radius = (btn_outside_radius * 0.75).toFloat()
        btn_after_outside_radius = (width / 7).toFloat()
        btn_after_inside_radius = (btn_outside_radius * 0.65).toFloat()

        //        btn_result_radius = 80;
        btn_result_radius = (width / 9).toFloat()
        btn_left_X = (width / 2).toFloat()
        btn_right_X = (width / 2).toFloat()
    }

    private var paintArc: Paint? = null
    private var paintText: Paint? = null
    private var textSize: Float = 0.toFloat()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (STATE_SELECTED == STATE_LESSNESS || STATE_SELECTED == STATE_RECORD) {
            //draw capture button
            mPaint.color = 0xFFEEEEEE.toInt()
            canvas.drawCircle(btn_center_X, btn_center_Y, btn_outside_radius, mPaint)
            mPaint.color = Color.WHITE
            canvas.drawCircle(btn_center_X, btn_center_Y, btn_inside_radius, mPaint)

            //draw Progress bar
            if (paintArc == null) {
                paintArc = Paint()
                paintArc!!.isAntiAlias = true
                paintArc!!.color = 0xFF00CC00.toInt()
                paintArc!!.style = Paint.Style.STROKE
                paintArc!!.strokeWidth = 10f
            }
            if (rectF == null)
                rectF = RectF()
            rectF!!.set(btn_center_X - (btn_after_outside_radius - 5),
                    btn_center_Y - (btn_after_outside_radius - 5),
                    btn_center_X + (btn_after_outside_radius - 5),
                    btn_center_Y + (btn_after_outside_radius - 5))
            canvas.drawArc(rectF!!, -90f, progress, false, paintArc!!)
            if (paintText == null) {
                paintText = Paint()
                paintText!!.isAntiAlias = true
                paintText!!.color = 0xFF333333.toInt()
                textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14f, mContext.resources.displayMetrics)
                paintText!!.textSize = textSize
            }
            val surplus = (20 - 20 * progress / 360).toInt()
            canvas.drawText((if (surplus < 10) "0" else "") + surplus.toString(), btn_center_X - textSize / 2, btn_center_Y + textSize / 2, paintText!!)
        } else if (STATE_SELECTED == STATE_RECORD_BROWSE || STATE_SELECTED == STATE_PICTURE_BROWSE) {

            mPaint.color = 0xFFEEEEEE.toInt()
            canvas.drawCircle(btn_left_X, btn_center_Y, btn_result_radius, mPaint)
            mPaint.color = Color.WHITE
            canvas.drawCircle(btn_right_X, btn_center_Y, btn_result_radius, mPaint)


            //left button
            val paint = Paint()
            paint.isAntiAlias = true
            paint.color = Color.BLACK
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 3f
            val path = Path()

            path.moveTo(btn_left_X - 2, btn_center_Y + 14)
            path.lineTo(btn_left_X + 14, btn_center_Y + 14)
            path.arcTo(RectF(btn_left_X, btn_center_Y - 14, btn_left_X + 28, btn_center_Y + 14), 90f, -180f)
            path.lineTo(btn_left_X - 14, btn_center_Y - 14)
            canvas.drawPath(path, paint)


            paint.style = Paint.Style.FILL
            path.reset()
            path.moveTo(btn_left_X - 14, btn_center_Y - 22)
            path.lineTo(btn_left_X - 14, btn_center_Y - 6)
            path.lineTo(btn_left_X - 23, btn_center_Y - 14)
            path.close()
            canvas.drawPath(path, paint)


            paint.style = Paint.Style.STROKE
            paint.color = 0xFF00CC00.toInt()
            paint.strokeWidth = 4f
            path.reset()
            path.moveTo(btn_right_X - 28, btn_center_Y)
            path.lineTo(btn_right_X - 8, btn_center_Y + 22)
            path.lineTo(btn_right_X + 30, btn_center_Y - 20)
            path.lineTo(btn_right_X - 8, btn_center_Y + 18)
            path.close()
            canvas.drawPath(path, paint)
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> if (STATE_SELECTED == STATE_LESSNESS) {
                if (event.y > btn_center_Y - btn_outside_radius &&
                        event.y < btn_center_Y + btn_outside_radius &&
                        event.x > btn_center_X - btn_outside_radius &&
                        event.x < btn_center_X + btn_outside_radius &&
                        event.pointerCount == 1) {
                    if (!FileUtil.isExternalStorageWritable) {
                        Toast.makeText(mContext, "请插入储存卡", Toast.LENGTH_SHORT).show()
                    } else {
                        key_down_Y = event.y
                        STATE_SELECTED = STATE_KEY_DOWN
                        //postCheckForLongTouch();
                    }
                }
            } else if (STATE_SELECTED == STATE_RECORD_BROWSE || STATE_SELECTED == STATE_PICTURE_BROWSE) {
                if (event.y > btn_center_Y - btn_result_radius &&
                        event.y < btn_center_Y + btn_result_radius &&
                        event.x > btn_left_X - btn_result_radius &&
                        event.x < btn_left_X + btn_result_radius &&
                        event.pointerCount == 1) {
                    if (mCaptureListener != null) {

                        if (STATE_SELECTED == STATE_RECORD_BROWSE) {
                            mCaptureListener!!.deleteRecordResult()
                        } else if (STATE_SELECTED == STATE_PICTURE_BROWSE) {
                            mCaptureListener!!.cancel()
                        }
                    }
                    STATE_SELECTED = STATE_LESSNESS
                    btn_left_X = btn_center_X
                    btn_right_X = btn_center_X
                    invalidate()
                } else if (event.y > btn_center_Y - btn_result_radius &&
                        event.y < btn_center_Y + btn_result_radius &&
                        event.x > btn_right_X - btn_result_radius &&
                        event.x < btn_right_X + btn_result_radius &&
                        event.pointerCount == 1) {
                    if (mCaptureListener != null) {
                        if (STATE_SELECTED == STATE_RECORD_BROWSE) {
                            mCaptureListener!!.getRecordResult()
                        } else if (STATE_SELECTED == STATE_PICTURE_BROWSE) {
                            mCaptureListener!!.determine()
                        }
                    }
                    STATE_SELECTED = STATE_LESSNESS
                    btn_left_X = btn_center_X
                    btn_right_X = btn_center_X
                    invalidate()
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (event.y > btn_center_Y - btn_outside_radius &&
                        event.y < btn_center_Y + btn_outside_radius &&
                        event.x > btn_center_X - btn_outside_radius &&
                        event.x < btn_center_X + btn_outside_radius) {
                }
                if (mCaptureListener != null) {
                    mCaptureListener!!.scale(key_down_Y - event.y)
                }
            }
            MotionEvent.ACTION_UP -> {
                removeCallbacks(longPressRunnable)
                if (STATE_SELECTED == STATE_KEY_DOWN) {
                    if (event.y > btn_center_Y - btn_outside_radius &&
                            event.y < btn_center_Y + btn_outside_radius &&
                            event.x > btn_center_X - btn_outside_radius &&
                            event.x < btn_center_X + btn_outside_radius) {
                        //                        if (mCaptureListener != null) {
                        //                            mCaptureListener.capture();
                        //                        }
                        //                        STATE_SELECTED = STATE_PICTURE_BROWSE;
                        postCheckForLongTouch()
                    }
                } else if (STATE_SELECTED == STATE_RECORD) {
                    stopRecord()
                }
            }
        }
        return true
    }

    private fun stopRecord() {
        if (STATE_SELECTED == STATE_RECORD) {
            if (record_anim.currentPlayTime < 500) {
                STATE_SELECTED = STATE_LESSNESS
                //                        Toast.makeText(mContext, "Under time", Toast.LENGTH_SHORT).show();
                progress = 0f
                invalidate()
                record_anim.cancel()
            } else {
                STATE_SELECTED = STATE_RECORD_BROWSE
                removeCallbacks(recordRunnable)
                //                        Toast.makeText(mContext, "Time length " + record_anim.getCurrentPlayTime(), Toast.LENGTH_SHORT).show();
                captureAnimation((width / 5).toFloat(), (width / 5 * 4).toFloat())
                record_anim.cancel()
                progress = 0f
                invalidate()
                if (mCaptureListener != null) {
                    mCaptureListener!!.rencodEnd()
                }
            }
            if (btn_outside_radius == btn_after_outside_radius && btn_inside_radius == btn_after_inside_radius) {
                //                            startAnimation(btn_outside_radius, btn_outside_radius - 40, btn_inside_radius, btn_inside_radius + 20);
                startAnimation(btn_after_outside_radius, btn_before_outside_radius, btn_after_inside_radius, btn_before_inside_radius)
            } else {
                startAnimation(btn_after_outside_radius, btn_before_outside_radius, btn_after_inside_radius, btn_before_inside_radius)
            }

        }
    }

    fun onPause() {
        if (STATE_SELECTED == STATE_RECORD) {
            STATE_SELECTED = STATE_LESSNESS
            progress = 0f
            invalidate()
            record_anim.cancel()
            startAnimation(btn_after_outside_radius, btn_before_outside_radius, btn_after_inside_radius, btn_before_inside_radius)
        }
    }

    fun captureSuccess() {
        captureAnimation((width / 5).toFloat(), (width / 5 * 4).toFloat())
    }

    private fun postCheckForLongTouch() {
        //postDelayed(longPressRunnable, 500);
        post(longPressRunnable)
    }


    private inner class LongPressRunnable : Runnable {
        override fun run() {
            startAnimation(btn_before_outside_radius, btn_after_outside_radius, btn_before_inside_radius, btn_after_inside_radius)
            STATE_SELECTED = STATE_RECORD
        }
    }

    private inner class RecordRunnable : Runnable {
        override fun run() {
            if (mCaptureListener != null) {
                mCaptureListener!!.record()
            }
            record_anim.addUpdateListener { animation ->
                if (STATE_SELECTED == STATE_RECORD) {
                    progress = animation.animatedValue as Float
                }
                invalidate()
            }
            record_anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    if (STATE_SELECTED == STATE_RECORD) {
                        STATE_SELECTED = STATE_RECORD_BROWSE
                        progress = 0f
                        invalidate()
                        captureAnimation((width / 5).toFloat(), (width / 5 * 4).toFloat())
                        if (btn_outside_radius == btn_after_outside_radius && btn_inside_radius == btn_after_inside_radius) {
                            startAnimation(btn_after_outside_radius, btn_before_outside_radius, btn_after_inside_radius, btn_before_inside_radius)
                        } else {
                            startAnimation(btn_after_outside_radius, btn_before_outside_radius, btn_after_inside_radius, btn_before_inside_radius)
                        }
                        if (mCaptureListener != null) {
                            mCaptureListener!!.rencodEnd()
                        }
                    }
                }
            })
            record_anim.interpolator = LinearInterpolator()
            record_anim.duration = 20000
            record_anim.start()
        }
    }

    private fun startAnimation(outside_start: Float, outside_end: Float, inside_start: Float, inside_end: Float) {

        val outside_anim = ValueAnimator.ofFloat(outside_start, outside_end)
        val inside_anim = ValueAnimator.ofFloat(inside_start, inside_end)
        outside_anim.addUpdateListener { animation ->
            btn_outside_radius = animation.animatedValue as Float
            invalidate()
        }
        outside_anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if (STATE_SELECTED == STATE_RECORD) {
                    postDelayed(recordRunnable, 100)
                }
            }
        })
        inside_anim.addUpdateListener { animation ->
            btn_inside_radius = animation.animatedValue as Float
            invalidate()
        }
        outside_anim.duration = 100
        inside_anim.duration = 100
        outside_anim.start()
        inside_anim.start()
    }

    private fun captureAnimation(left: Float, right: Float) {
        //        Toast.makeText(mContext,left+ " = "+right,Toast.LENGTH_SHORT).show();
        Log.i("CaptureButtom", left.toString() + "==" + right)
        val left_anim = ValueAnimator.ofFloat(btn_left_X, left)
        val right_anim = ValueAnimator.ofFloat(btn_right_X, right)
        left_anim.addUpdateListener { animation ->
            btn_left_X = animation.animatedValue as Float
            Log.i("CJT", btn_left_X.toString() + "=====")
            invalidate()
        }
        right_anim.addUpdateListener { animation ->
            btn_right_X = animation.animatedValue as Float
            invalidate()
        }
        left_anim.duration = 200
        right_anim.duration = 200
        left_anim.start()
        right_anim.start()
    }

    fun setCaptureListener(mCaptureListener: CaptureListener) {
        this.mCaptureListener = mCaptureListener
    }


    interface CaptureListener {
        fun capture()

        fun cancel()

        fun determine()

        fun record()

        fun rencodEnd()

        fun getRecordResult()

        fun deleteRecordResult()

        fun scale(scaleValue: Float)
    }
}
