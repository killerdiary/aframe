package com.hy.frame.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hy.frame.R;

/**
 * @title 加载对话框
 * @author heyan
 * @time Apr 23, 2013 11:56:31 AM
 */
public class LoadingDialog extends Dialog {

    // dialog_txtLoadMsg
    private LinearLayout llyLoading;
    private TextView txtLoadMsg;
    private Window window;
    private String loadMsg;
    private int msgResId;
    private int msgStep;
    private boolean blackBg;

    public LoadingDialog(Context context) {
        super(context, R.style.DialogTheme);
    }

    public void init(boolean blackBg) {
        init(blackBg, 0, null);
    }

    public void init(String loadMsg) {
        init(false, 0, loadMsg);
    }

    public void init(int msgResId) {
        init(false, msgResId, null);
    }

    public void init(boolean blackBg, String loadMsg) {
        init(blackBg, 0, loadMsg);
    }

    public void init(boolean blackBg, int msgResId) {
        init(blackBg, msgResId, null);
    }

    private void init(boolean blackBg, int msgResId, String loadMsg) {
        this.blackBg = blackBg;
        this.msgResId = msgResId;
        this.loadMsg = loadMsg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // windowDeploy(x, y)
        this.setContentView(R.layout.dialog_loading);// 加载布局文件
        window = getWindow();
        WindowManager m = window.getWindowManager();
        // Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
        LayoutParams params = getWindow().getAttributes(); // 获取对话框当前的参数值
        params.height = LayoutParams.WRAP_CONTENT;
        params.width = LayoutParams.WRAP_CONTENT; // 宽度设置为屏幕的0.95
        params.verticalMargin = -0.1f;
        window.setAttributes(params); // 设置生效
        // getWindow().setGravity(Gravity.LEFT | Gravity.TOP);
        // setCanceledOnTouchOutside(true);// 设置触摸对话框意外的地方取消对话框
        // window.setWindowAnimations(R.style.winAnimFadeInFadeOut);
        initViews();
    }

    private void initViews() {
        // vLoading = (View) this.findViewById(R.id.txtLoadMsg);
        llyLoading = (LinearLayout) this.findViewById(R.id.llyLoading);
        txtLoadMsg = (TextView) this.findViewById(R.id.txtLoadMsg);
        if (loadMsg != null)
            txtLoadMsg.setText(loadMsg);
        if (msgResId > 0)
            txtLoadMsg.setText(msgResId);
        if (loadMsg == null && msgResId == 0) {
            // txtLoadMsg.setText("加载中...");
            // runnable = new Runnable() {
            //
            // @Override
            // public void run() {
            // // 要做的事情
            // if (msgStep == 1)
            // txtLoadMsg.setText(R.string.harder_loading);
            // else if ((msgStep > 1)) {
            // txtLoadMsg.setText(R.string.hardest_loading);
            // }
            // msgStep++;
            // handler.postDelayed(this, 4000);
            // }
            // };
            // handler.postDelayed(runnable, 4000);// 每两秒执行一次runnable.
            // handler.postDelayed(runnable, 8000);// 每两秒执行一次runnable.
        }
        // if (blackBg) {
        // llyLoading.setBackgroundResource(R.drawable.dialog_bg);
        // txtLoadMsg.setTextColor(Color.rgb(255, 255, 255));
        // }
        // llyLoading.setBackgroundResource(bgres);
    }

    public void updateMsg(String msg) {
        if (txtLoadMsg != null) {
            txtLoadMsg.setText(msg);
        } else {
            loadMsg = msg;
        }
    }

}
