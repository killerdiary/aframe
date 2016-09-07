package com.hy.frame.view.recycler;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.hy.frame.R;
import com.hy.frame.adapter.BaseRecyclerAdapter;
import com.hy.frame.util.MyLog;

/**
 * RecyclerView 上下拉刷新加载更多 适用于为ListView格式 版本>=11
 *
 * @author HeYan
 * @time 2016/5/28 9:19
 */
public class RefreshRecyclerView extends SwipeRefreshLayout {
    private RecyclerView recyclerView;
    private FrameLayout flyContainer;
    private RecyclerViewHeader header;
    private boolean canLoadMore, loadingMore,openRefresh;
    private ILoadMoreListener loadMoreListener;
    private boolean hasHeader;

    public RefreshRecyclerView(Context context) {
        this(context, null);
    }

    public RefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    //private int testCount = 0;

    private void init(Context context) {
        flyContainer = new FrameLayout(context);
        flyContainer.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(flyContainer);
        recyclerView = new RecyclerView(context);
        recyclerView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                //MyLog.e(RefreshRecyclerView.this.getClass(), recyclerView.getChildCount() + " | rTop:" + recyclerView.getTop() + " | sTop:" + RefreshRecyclerView.this.getTop() + " | sTop:" + header.getTop());
                //int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                //RefreshRecyclerView.this.setEnabled(topRowVerticalPosition >= 0);
                //RefreshRecyclerView.this.setEnabled(false);
                if (null != loadMoreListener && canLoadMore && !loadingMore && itemCount > 0) {
                    RecyclerView.Adapter adapter = recyclerView.getAdapter();
                    if (adapter != null && adapter instanceof BaseRecyclerAdapter && ((BaseRecyclerAdapter) adapter).getTrueItemCount() % itemCount == 0) {
                        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                            if (layoutManager.findLastVisibleItemPosition() + 1 == adapter.getItemCount()) {
                                //进入加载
                                loadingMore = true;
                                loadMoreListener.onLoadMore();
                            }
                        } else if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                            GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                            if (layoutManager.findLastVisibleItemPosition() + 1 == adapter.getItemCount()) {
                                //进入加载
                                loadingMore = true;
                                loadMoreListener.onLoadMore();
                            }
                        } else if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                            MyLog.e(RefreshRecyclerView.this.getClass(), "Error StaggeredGridLayoutManager");
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (hasHeader) {
                    int translation = header.calculateTranslation();
                    //MyLog.e(RefreshRecyclerView.this.getClass(), "translation=" + translation);
                    RefreshRecyclerView.this.setEnabled(translation == 0);
                } else if (recyclerView.getLayoutManager().getItemCount() > 0) {
//                    boolean enable = false;
//                    if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
//                        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                        enable = layoutManager.findLastVisibleItemPosition() == 0;
//                    } else if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
//                        GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
//                        enable = layoutManager.findLastVisibleItemPosition() == 0;
//                    } else if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
//                        StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
//                        MyLog.e(RefreshRecyclerView.this.getClass(), "Error StaggeredGridLayoutManager");
//                    }
                    View v = recyclerView.getLayoutManager().getChildAt(0);
                    //MyLog.e(RefreshRecyclerView.this.getClass(), "top=" + v.getTop());
                    RefreshRecyclerView.this.setEnabled(v.getTop() >= 0);
//                    if (enable) {
//                        //MyLog.e(RefreshRecyclerView.this.getClass(), "top=" + v.getTop());
//                        RefreshRecyclerView.this.setEnabled(v.getTop() >= 0);
//                    } else
//                        RefreshRecyclerView.this.setEnabled(false);
                } else {
                    RefreshRecyclerView.this.setEnabled(false);
                }
            }
        });
        setColorSchemeResources(R.color.blue, R.color.yellow, R.color.green, R.color.red);
        flyContainer.addView(recyclerView);
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
        flyContainer.addView(header, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        header.attachTo(recyclerView);
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setLayoutManager(RecyclerView.LayoutManager manager) {
        recyclerView.setLayoutManager(manager);
    }

    public void setAdapter(BaseRecyclerAdapter adapter) {
        recyclerView.setAdapter(adapter);
    }


    public boolean isCanLoadMore() {
        return canLoadMore;
    }

    public void openLoadMore() {
        this.canLoadMore = true;
        this.loadingMore = false;
    }

    public void closeLoadMore() {
        this.canLoadMore = false;
        this.loadingMore = false;
    }


    public boolean isLoadingMore() {
        return loadingMore;
    }

    private int itemCount;//一页数量

    public void setLoadMoreListener(ILoadMoreListener loadMoreListener, int itemCount) {
        this.loadMoreListener = loadMoreListener;
        this.itemCount = itemCount;
    }

    public interface ILoadMoreListener {
        void onLoadMore();
    }
}
