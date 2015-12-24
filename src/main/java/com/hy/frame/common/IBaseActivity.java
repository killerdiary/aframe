package com.hy.frame.common;

import android.support.annotation.LayoutRes;
import android.view.View;

/**
 * @author HeYan
 * @title 公有接口
 * @time 2015/11/20 11:30
 */
public abstract interface IBaseActivity {
    /**
     * 初始化布局 可空,可以包含 Toolbar 否则 使用默认的 Toolbar
     */
    @LayoutRes
    int initLayoutId();

    /**
     * 初始化控件
     */
    void initView();

    /**
     * 初始化数据
     */
    void initData();

    /**
     * 初始化数据
     */
    void onStartData();

    /**
     * 请求数据
     */
    void requestData();

    /**
     * 更新UI
     */
    void updateUI();

    /**
     * 控件点击事件
     */
    void onViewClick(View v);

    /**
     * 头-左边图标点击
     */
    void onLeftClick();

    /**
     * 头-右边图标点击
     */
    void onRightClick();
}
