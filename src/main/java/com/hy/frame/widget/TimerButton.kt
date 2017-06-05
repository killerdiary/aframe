package com.hy.frame.widget

import android.content.Context
import android.util.AttributeSet
import com.hy.frame.R

/**
 * 倒计时按钮 (startText = "%ss 后重发")

 * @author HeYan
 *
 * @time 2015年1月6日 下午5:11:46
 */
class TimerButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = R.attr.buttonStyle) : android.support.v7.widget.AppCompatButton(context, attrs, defStyleAttr), Runnable {
    private var timer = 0
    private var status = 0
    private var prepareText: CharSequence? = null
    private var startText: CharSequence? = null
    private var endText: CharSequence? = null

    init {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.TimerButton, defStyleAttr, 0)
        prepareText = a.getText(R.styleable.TimerButton_timerPrepareText)
        startText = a.getText(R.styleable.TimerButton_timerStartText)
        endText = a.getText(R.styleable.TimerButton_timerEndText)
        a.recycle()
    }

    private fun update(time: Int) {
        if (startText != null && startText!!.toString().contains("s%")) {
            text = String.format(startText!!.toString(), time)
        } else {
            text = time.toString()
        }
    }

    /**
     * 准备倒计时
     */
    fun prepare() {
        if (prepareText != null)
            text = prepareText
        this.isEnabled = false
    }

    /**
     * 开启倒计时
     * @param interval 倒计时时间(秒)
     * @param listener 监听
     */
    fun start(interval: Int = DEFAULT_INTERVAL, listener: TimerListener? = null) {
        this.listener = listener
        if (status == UPDATE_DOING)
            return
        this.status = UPDATE_DOING
        this.isEnabled = false
        this.timer = interval
        run()
    }

    /**
     * 结束倒计时
     */
    fun end() {
        this.status = UPDATE_END
        if (endText != null)
            text = endText
        this.isEnabled = true
        if (listener != null) {
            listener!!.onTimerEnd()
        }
    }

    override fun run() {
        timer--
        if (timer < 1) {
            end()
            return
        }
        update(timer)
        postDelayed(this, 1000)
    }

    private var listener: TimerListener? = null

    interface TimerListener {
        fun onTimerEnd()
    }

    companion object {

        private val DEFAULT_INTERVAL = 120// 间隔时间60秒
        private val UPDATE_END = 0
        private val UPDATE_DOING = 2
    }
}

