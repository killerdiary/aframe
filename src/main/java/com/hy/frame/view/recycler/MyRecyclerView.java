package com.hy.frame.view.recycler;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.hy.frame.adapter.BaseRecyclerAdapter;

/**
 * RecyclerView带加载更多
 *
 * @author HeYan
 * @time 2016/5/27 16:10
 */
public class MyRecyclerView extends RecyclerView implements ILoadMore {

    private boolean showLoadMore, loadingMore;
    private ILoadMoreListener loadMoreListener;
//    private int headerResId;

    public MyRecyclerView(Context context) {
        super(context);
    }

    public MyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

//    public void setHeaderResId(int layoutId) {
//        this.headerResId = layoutId;
//        if (getAdapter() != null && getAdapter() instanceof BaseRecyclerAdapter) {
//            BaseRecyclerAdapter adapter = (BaseRecyclerAdapter) getAdapter();
//            adapter.setHeaderResId(layoutId);
//        }
//    }

    @Override
    public void onScrolled(int dx, int dy) {

    }

    @Override
    public void onScrollStateChanged(int state) {
        //MyLog.e(state);
        if (null != loadMoreListener && showLoadMore && !loadingMore) {
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

    @Override
    public boolean isShowLoadMore() {
        return showLoadMore;
    }

    @Override
    public void showLoadMore() {
        this.showLoadMore = true;
        this.loadingMore = false;
        if (getAdapter() != null && getAdapter() instanceof BaseRecyclerAdapter) {
            BaseRecyclerAdapter adapter = (BaseRecyclerAdapter) getAdapter();
            adapter.setCanLoadMore(true);
            adapter.setLoadMoreState(LoadMoreHolder.STATE_LOAD_PREPARE);
        }
    }

    @Override
    public void hideLoadMore() {
        this.showLoadMore = false;
        this.loadingMore = false;
        if (getAdapter() != null && getAdapter() instanceof BaseRecyclerAdapter) {
            BaseRecyclerAdapter adapter = (BaseRecyclerAdapter) getAdapter();
            adapter.notifyItemRemoved(adapter.getItemCount() - 1);
            adapter.setCanLoadMore(false);
            adapter.setLoadMoreState(LoadMoreHolder.STATE_LOAD_PREPARE);
        }
    }

    @Override
    public void loadMoreComplete() {
        this.showLoadMore = false;
        this.loadingMore = false;
        if (getAdapter() != null && getAdapter() instanceof BaseRecyclerAdapter) {
            BaseRecyclerAdapter adapter = (BaseRecyclerAdapter) getAdapter();
            adapter.setCanLoadMore(true);
            adapter.setLoadMoreState(LoadMoreHolder.STATE_LOAD_COMPLETE);
        }
    }

    @Override
    public boolean isLoadingMore() {
        return loadingMore;
    }

    @Override
    public void setLoadMoreListener(ILoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    public interface ILoadMoreListener {
        void onLoadMore();
    }

//    public void setAdapter(BaseRecyclerAdapter adapter) {
//        adapter.setHeaderResId(headerResId);
//        super.setAdapter(adapter);
//    }
}
