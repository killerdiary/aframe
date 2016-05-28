package com.hy.frame.view.recycler;

public interface ILoadMore {
    void showLoadMore();

    void hideLoadMore();

    void loadMoreComplete();

    boolean isShowLoadMore();

    boolean isLoadingMore();

    void setLoadMoreListener(MyRecyclerView.ILoadMoreListener loadMoreListener);
}