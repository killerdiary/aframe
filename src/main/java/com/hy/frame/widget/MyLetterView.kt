package com.hy.frame.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class MyLetterView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private var onTouchingLetterChangedListener: OnTouchingLetterChangedListener? = null
    private val letters = arrayOf("@", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#")
    internal var choose = -1
    private val paint = Paint()
    internal var showBkg = false

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (showBkg) {
            canvas.drawColor(Color.parseColor("#40000000"))
        }
        val height = height
        val width = width
        val singleHeight = height / letters.size
        for (i in letters.indices) {
            paint.textSize = 18f
            paint.color = Color.BLACK
            //paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.isAntiAlias = true
            if (i == choose) {
                paint.color = Color.parseColor("#3399ff")
                paint.isFakeBoldText = true
            }
            val xPos = width / 2 - paint.measureText(letters[i]) / 2
            val yPos = (singleHeight * i + singleHeight).toFloat()

            canvas.drawText(letters[i], xPos, yPos, paint)

            paint.reset()
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        val y = event.y
        val x = event.x
        val oldChoose = choose
        val listener = onTouchingLetterChangedListener
        val c = (y / height * letters.size).toInt()

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                showBkg = true
                if (oldChoose != c && listener != null) {
                    if (c > 0 && c < letters.size) {
                        listener.onTouchingLetterChanged(letters[c], y, x)
                        choose = c
                        invalidate()
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> if (oldChoose != c && listener != null) {
                if (c > 0 && c < letters.size) {
                    listener.onTouchingLetterChanged(letters[c], y, x)
                    choose = c
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                showBkg = false
                choose = -1
                listener?.onTouchingLetterEnd()
                invalidate()
            }
        }
        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return super.onTouchEvent(event)
    }

    fun setOnTouchingLetterChangedListener(onTouchingLetterChangedListener: OnTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener
    }

    interface OnTouchingLetterChangedListener {
        fun onTouchingLetterEnd()
        fun onTouchingLetterChanged(s: String, y: Float, x: Float)
    }
}