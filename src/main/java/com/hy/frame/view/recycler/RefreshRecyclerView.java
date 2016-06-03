package com.hy.frame.view.recycler;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.hy.frame.R;
import com.hy.frame.adapter.BaseRecyclerAdapter;

/**
 * RecyclerView 上下拉刷新加载更多 适用于为ListView格式
 *
 * @author HeYan
 * @time 2016/5/28 9:19
 */
public class RefreshRecyclerView extends SwipeRefreshLayout implements ILoadMore {
    private MyRecyclerView recyclerView;
    //private LinearLayout llyContainer;

    public RefreshRecyclerView(Context context) {
        this(context, null);
    }

    public RefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        //llyContainer = new LinearLayout(context);
        //llyContainer.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //llyContainer.setOrientation(LinearLayout.VERTICAL);
        //addView(llyContainer);
        recyclerView = new MyRecyclerView(context);
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        setColorSchemeResources(R.color.blue, R.color.yellow, R.color.green, R.color.red);
        addView(recyclerView);
        //llyContainer.addView(recyclerView);
    }

//    public void setHeaderResId(int layoutId) {
//        recyclerView.setHeaderResId(layoutId);
//    }

    public MyRecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setLayoutManager(RecyclerView.LayoutManager manager) {
        recyclerView.setLayoutManager(manager);
    }

    @Override
    public void showLoadMore() {
        recyclerView.showLoadMore();
    }

    @Override
    public void hideLoadMore() {
        recyclerView.hideLoadMore();
    }

    @Override
    public void loadMoreComplete() {
        recyclerView.loadMoreComplete();
    }

    @Override
    public boolean isShowLoadMore() {
        return recyclerView.isShowLoadMore();
    }

    @Override
    public boolean isLoadingMore() {
        return recyclerView.isLoadingMore();
    }

    @Override
    public void setLoadMoreListener(MyRecyclerView.ILoadMoreListener loadMoreListener) {
        recyclerView.setLoadMoreListener(loadMoreListener);
    }

    public void setAdapter(BaseRecyclerAdapter adapter) {
        recyclerView.setAdapter(adapter);
    }

}
