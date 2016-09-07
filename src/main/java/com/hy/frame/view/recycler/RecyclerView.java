package com.hy.frame.view.recycler;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.hy.frame.adapter.BaseRecyclerAdapter;

/**
 * RecyclerView
 *
 * @author HeYan
 * @time 2016/5/28 9:19
 */
public class RecyclerView extends FrameLayout {
    private android.support.v7.widget.RecyclerView recyclerView;
    private RecyclerViewHeader header;

    private boolean hasHeader;

    public RecyclerView(Context context) {
        this(context, null);
    }

    public RecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    //private int testCount = 0;

    private void init(Context context) {
        recyclerView = new android.support.v7.widget.RecyclerView(context);
        recyclerView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        addView(recyclerView);
    }

    /**
     * 只能调用一次
     *
     * @param layoutId
     */
    //@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setHeaderResId(@LayoutRes int layoutId) {
        View v = inflate(getContext(), layoutId, null);
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(rlp);
        setHeaderView(v);
    }

    /**
     * 只能调用一次
     *
     * @param v
     */
    public void setHeaderView(View v) {
        hasHeader = true;
        header = new RecyclerViewHeader(getContext());
        header.addView(v);
        addView(header, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        header.attachTo(recyclerView);
    }

    public android.support.v7.widget.RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setLayoutManager(android.support.v7.widget.RecyclerView.LayoutManager manager) {
        recyclerView.setLayoutManager(manager);
    }

    public void setAdapter(BaseRecyclerAdapter adapter) {
        recyclerView.setAdapter(adapter);
    }
}
