package com.hy.frame.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.hy.frame.R;

/**
 * 倒计时按钮 (hint = "%ss 后重发")
 *
 * @author HeYan
 * @time 2015年1月6日 下午5:11:46
 */
public class TimerButton extends Button implements Runnable {

    private final static int DEFAULT_INTERVAL = 120;// 间隔时间60秒
    private final static int UPDATE_END = 0;
    private final static int UPDATE_DOING = 2;
    private int timer = 0;
    private int status = 0;

    public TimerButton(Context context) {
        super(context);
    }

    public TimerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimerButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 开启倒计时
     */
    public void start() {
        start(DEFAULT_INTERVAL);
    }

    /**
     * 结束倒计时
     */
    public void end() {
        this.status = UPDATE_END;
        this.setText(R.string.timer_send_re);
        this.setEnabled(true);
    }

    private void update(int time) {
        this.setText(String.format(getHint().toString(), time));
        // this.setText(time + "秒后重新获取");
    }

    /**
     * 准备倒计时
     */
    public void prepare() {
        this.setText(R.string.timer_sending);
        this.setEnabled(false);
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
}
