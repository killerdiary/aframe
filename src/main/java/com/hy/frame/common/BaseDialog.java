package com.hy.frame.common;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

import com.hy.frame.R;
import com.hy.frame.util.HyUtil;

/**
 * @author HeYan
 * @title 父类对话框
 * @time 2015/11/16 13:25
 */
public abstract class BaseDialog extends Dialog implements View.OnClickListener {
    private IConfirmListener listener;

    public BaseDialog(Context context) {
        super(context, R.style.AppBaseTheme_DialogTheme);
    }

    public void setListener(IConfirmListener listener) {
        this.listener = listener;
    }

    public IConfirmListener getListener() {
        return listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(initLayoutId());
        initWindow();
        initView();
        initData();
    }

    protected abstract int initLayoutId();

    /**
     * 初始化Window
     */
    protected abstract void initWindow();

    /**
     * 初始化布局
     */
    protected abstract void initView();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 控件点击事件
     */
    protected abstract void onViewClick(View v);

    @Override
    public void onClick(View v) {
        if (HyUtil.isFastClick())
            return;
        onViewClick(v);
    }

    protected void windowDeploy(float width, float height, int gravity) {
        Window window = getWindow();
        LayoutParams params = getWindow().getAttributes(); // 获取对话框当前的参数值
        if (width == 0) {
            params.width = LayoutParams.WRAP_CONTENT;
        } else if (width > 0 && width <= 1) {
            params.width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * width); // 宽度设置为屏幕的0.x
        } else {
            params.width = (int) width;
        }
        if (height == 0) {
            params.height = LayoutParams.WRAP_CONTENT;
        } else if (height > 0 && height <= 1) {
            params.height = (int) (getContext().getResources().getDisplayMetrics().heightPixels * height); // 高度设置为屏幕的0.x
        } else {
            params.height = (int) height;
        }
        params.verticalMargin = -0.1f;
        window.setAttributes(params); // 设置生效
        getWindow().setGravity(gravity);
        // getWindow().setGravity(Gravity.LEFT | Gravity.TOP);
        setCanceledOnTouchOutside(false);// 设置触摸对话框意外的地方取消对话框
        setCancelable(false);
        // window.setWindowAnimations(R.style.winAnimFadeInFadeOut);
    }


    @SuppressWarnings({"unchecked"})
    protected <T> T getView(int resId) {
        return (T) findViewById(resId);
    }

    /**
     * 获取并绑定点击
     *
     * @param id id
     */
    @SuppressWarnings("unchecked")
    protected <T extends View> T getViewAndClick(@IdRes int id) {
        T v = getView(id);
        v.setOnClickListener(this);
        return v;
    }

    protected void setOnClickListener(int resId) {
        findViewById(resId).setOnClickListener(this);
    }

    private Object tag;

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public interface IConfirmListener {
        void onDlgConfirm(BaseDialog dlg);
    }
}
