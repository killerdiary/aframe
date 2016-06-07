package com.hy.frame.view.recycler;

import android.content.Context;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.hy.frame.R;
import com.hy.frame.adapter.BaseRecyclerAdapter;

/**
 * RecyclerView 上下拉刷新加载更多 适用于为ListView格式 版本>=11
 *
 * @author HeYan
 * @time 2016/5/28 9:19
 */
public class RefreshRecyclerView extends SwipeRefreshLayout implements ILoadMore {
    private MyRecyclerView recyclerView;
    private RecyclerViewHeader header;

    public RefreshRecyclerView(Context context) {
        this(context, null);
    }

    public RefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private int testCount = 0;

    private void init(Context context) {
        final FrameLayout flyContainer = new FrameLayout(context);
        flyContainer.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(flyContainer);
        recyclerView = new MyRecyclerView(context);
        recyclerView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                //MyLog.e(RefreshRecyclerView.this.getClass(), recyclerView.getChildCount() + " | rTop:" + recyclerView.getTop() + " | sTop:" + RefreshRecyclerView.this.getTop() + " | sTop:" + header.getTop());
                //int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                //RefreshRecyclerView.this.setEnabled(topRowVerticalPosition >= 0);
                //RefreshRecyclerView.this.setEnabled(false);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    int translation = header.calculateTranslation();
                    //MyLog.e(RefreshRecyclerView.this.getClass(), "translation=" + translation);
                    RefreshRecyclerView.this.setEnabled(translation == 0);
                } else {
                    RefreshRecyclerView.this.setEnabled(false);
                }
            }
        });
        setColorSchemeResources(R.color.blue, R.color.yellow, R.color.green, R.color.red);
        flyContainer.addView(recyclerView);
        header = new RecyclerViewHeader(context);
        header.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        flyContainer.addView(header);
        //llyContainer.addView(recyclerView);
    }

    /**
     * 只能调用一次
     *
     * @param layoutId
     */
    public void setHeaderResId(@LayoutRes int layoutId) {
        inflate(getContext(), layoutId, header);
        header.attachTo(recyclerView);
    }

    /**
     * 只能调用一次
     *
     * @param v
     */
    public void setHeaderView(View v) {
        header.addView(v);
        header.attachTo(recyclerView);
    }

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
