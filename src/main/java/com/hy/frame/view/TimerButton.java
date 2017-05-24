package com.hy.frame.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import com.hy.frame.R;

/**
 * 倒计时按钮 (startText = "%ss 后重发")
 *
 * @author HeYan
 * @time 2015年1月6日 下午5:11:46
 */
public class TimerButton extends AppCompatButton implements Runnable {

    private final static int DEFAULT_INTERVAL = 120;// 间隔时间60秒
    private final static int UPDATE_END = 0;
    private final static int UPDATE_DOING = 2;
    private int timer = 0;
    private int status = 0;
    private CharSequence prepareText;
    private CharSequence startText;
    private CharSequence endText;

    public TimerButton(Context context) {
        this(context, null);
    }

    public TimerButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyle);
    }

    public TimerButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimerButton, defStyleAttr, 0);
        prepareText = a.getText(R.styleable.TimerButton_timerPrepareText);
        startText = a.getText(R.styleable.TimerButton_timerStartText);
        endText = a.getText(R.styleable.TimerButton_timerEndText);
        a.recycle();
    }

    private void update(int time) {
        if (startText != null && startText.toString().contains("s%")) {
            setText(String.format(startText.toString(), time));
        } else {
            setText(String.valueOf(time));
        }
    }

    /**
     * 准备倒计时
     */
    public void prepare() {
        if (prepareText != null)
            setText(prepareText);
        this.setEnabled(false);
    }

    /**
     * 开启倒计时
     */
    public void start() {
        start(DEFAULT_INTERVAL);
    }

    /**
     * 开启倒计时
     *
     * @param interval 倒计时时间(秒)
     */
    public void start(int interval) {
        if (status == UPDATE_DOING)
            return;
        this.status = UPDATE_DOING;
        this.setEnabled(false);
        this.timer = interval;
        run();
    }

    public void start(int interval, TimerListener listener) {
        this.listener = listener;
        start(interval);
    }

    /**
     * 结束倒计时
     */
    public void end() {
        this.status = UPDATE_END;
        if (endText != null)
            setText(endText);
        this.setEnabled(true);
        if (listener != null) {
            listener.onTimerEnd();
        }
    }

    @Override
    public void run() {
        timer--;
        if (timer < 1) {
            end();
            return;
        }
        update(timer);
        postDelayed(this, 1000);
    }

    private TimerListener listener;

    public interface TimerListener {
        void onTimerEnd();
    }
}
