package com.hy.frame.view.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * 示例
 * 自定义的ViewHolder，持有每个Item的的所有界面元素
 * static不是说只存在1个实例，而是可以访问外部类的静态变量，final修饰类则是不让该类继承
 */
//@Deprecated
public class BaseHolder extends RecyclerView.ViewHolder {

    public BaseHolder(View v) {
        super(v);
        if (v.getLayoutParams() == null) {
            v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }
}