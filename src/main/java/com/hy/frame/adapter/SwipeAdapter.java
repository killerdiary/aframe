package com.hy.frame.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

import com.hy.frame.R;
import com.hy.frame.view.NewMyListView;
import com.hy.frame.view.SwipeView;
import com.hy.frame.view.swipe.DeletItemListView;
import com.hy.frame.view.swipe.SwipeMenu;
import com.hy.frame.view.swipe.SwipeMenuItem;
import com.hy.frame.view.swipe.SwipeMenuLayout;
import com.hy.frame.view.swipe.SwipeMenuView;

public class SwipeAdapter implements WrapperListAdapter {

    private Context context;
    private ListAdapter adapter;
    private int swipeMenuLayouId;
    private NewMyListView.OnMlvSwipeListener listener;

    public SwipeAdapter(Context context, ListAdapter adapter, int swipeMenuLayouId,NewMyListView.OnMlvSwipeListener listener) {
        this.context = context;
        this.adapter = adapter;
        this.swipeMenuLayouId = swipeMenuLayouId;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return adapter.getCount();
    }

    @Override
    public Object getItem(int position) {
        return adapter.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return adapter.getItemId(position);
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        SwipeView layout;
        if (v == null) {
            View contentView = adapter.getView(position, null, parent);
            View menuView = View.inflate(context, swipeMenuLayouId, null);
            layout = new SwipeView(contentView, menuView,listener);
            layout.setPosition(position);
        } else {
            layout = (SwipeView) v;
            layout.closeMenu();
            layout.setPosition(position);
        }
        return layout;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        adapter.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        adapter.unregisterDataSetObserver(observer);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return adapter.areAllItemsEnabled();
    }

    @Override
    public boolean isEnabled(int position) {
        return adapter.isEnabled(position);
    }

    @Override
    public boolean hasStableIds() {
        return adapter.hasStableIds();
    }

    @Override
    public int getItemViewType(int position) {
        return adapter.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return adapter.getViewTypeCount();
    }

    @Override
    public boolean isEmpty() {
        return adapter.isEmpty();
    }

    @Override
    public ListAdapter getWrappedAdapter() {
        return adapter;
    }

}
