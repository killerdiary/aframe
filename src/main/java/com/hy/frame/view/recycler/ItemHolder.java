package com.hy.frame.view.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.hy.frame.adapter.IAdapterListener;

/**
 * 示例
 * 自定义的ViewHolder，持有每个Item的的所有界面元素
 * static不是说只存在1个实例，而是可以访问外部类的静态变量，final修饰类则是不让该类继承
 */
@Deprecated
public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private IAdapterListener listener;
    public TextView txtTitle;

    public ItemHolder(View v, IAdapterListener listener) {
        super(v);
        this.listener = listener;
        v.setOnClickListener(this);
        txtTitle = (TextView) v.findViewById(android.R.id.text1);
    }

    @Override
    public void onClick(View v) {
        if (listener != null)
            listener.onViewClick(v.getId(), "", getAdapterPosition());
    }
}