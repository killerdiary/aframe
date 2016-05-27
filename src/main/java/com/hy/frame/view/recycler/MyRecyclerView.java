package com.hy.frame.view.recycler;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.hy.frame.adapter.BaseRecyclerAdapter;
import com.hy.frame.util.MyLog;

/**
 * 描述
 *
 * @author HeYan
 * @time 2016/5/27 16:10
 */
public class MyRecyclerView extends RecyclerView {

    private boolean canLoadMore, loadingMore;
    private LoadMoreListener loadMoreListener;

    public MyRecyclerView(Context context) {
        super(context);
    }

    public MyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean isCanLoadMore() {
        return canLoadMore;
    }

    public void showLoadMore() {
        this.canLoadMore = true;
        this.loadingMore = false;
        if (getAdapter() != null && getAdapter() instanceof BaseRecyclerAdapter) {
            BaseRecyclerAdapter adapter = (BaseRecyclerAdapter) getAdapter();
            adapter.setCanLoadMore(true);
            adapter.setLoadMoreState(LoadMoreHolder.STATE_LOAD_PREPARE);
        }
    }

    public void hideLoadMore() {
        this.canLoadMore = false;
        this.loadingMore = false;
        if (getAdapter() != null && getAdapter() instanceof BaseRecyclerAdapter) {
            BaseRecyclerAdapter adapter = (BaseRecyclerAdapter) getAdapter();
            adapter.notifyItemRemoved(adapter.getItemCount() - 1);
            adapter.setCanLoadMore(false);
            adapter.setLoadMoreState(LoadMoreHolder.STATE_LOAD_PREPARE);
        }
    }

    public void setLoadMoreComplete() {
        this.canLoadMore = false;
        this.loadingMore = false;
        if (getAdapter() != null && getAdapter() instanceof BaseRecyclerAdapter) {
            BaseRecyclerAdapter adapter = (BaseRecyclerAdapter) getAdapter();
            adapter.setCanLoadMore(true);
            adapter.setLoadMoreState(LoadMoreHolder.STATE_LOAD_COMPLETE);
        }
    }

    public boolean isLoadingMore() {
        return loadingMore;
    }

    public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    @Override
    public void onScrolled(int dx, int dy) {

    }

    @Override
    public void onScrollStateChanged(int state) {
        MyLog.e(state);
        if (null != loadMoreListener && canLoadMore && !loadingMore) {
            if (getAdapter() != null && getAdapter() instanceof BaseRecyclerAdapter) {
                BaseRecyclerAdapter adapter = (BaseRecyclerAdapter) getAdapter();
                if (getLayoutManager() instanceof LinearLayoutManager) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
                    if (layoutManager.findLastVisibleItemPosition() + 1 == adapter.getItemCount()) {
                        //进入加载
                        loadingMore = true;
                        adapter.setLoadMoreState(LoadMoreHolder.STATE_LOAD_ING);
                        adapter.notifyDataSetChanged();
                        loadMoreListener.onLoadMore();
                    }
                }
            }
        }
    }

    public interface LoadMoreListener {
        void onLoadMore();
    }
}
