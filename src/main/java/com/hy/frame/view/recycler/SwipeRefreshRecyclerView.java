package com.hy.frame.view.recycler;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.recyclerview.LuRecyclerView;
import com.github.jdsjlzx.recyclerview.LuRecyclerViewAdapter;
import com.hy.frame.R;
import com.hy.frame.adapter.BaseRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView 上下拉刷新加载更多 适用于为ListView格式 版本>=11
 *
 * @author HeYan
 * @time 2016/5/28 9:19
 */
public class SwipeRefreshRecyclerView extends SwipeRefreshLayout {
    private LuRecyclerView mRecyclerView;
    private ILoadMoreListener loadMoreListener;
    private IRefreshListener refreshListener;
    private LuRecyclerViewAdapter mAdapter;
    private List<View> mHeaderViews = null;
    private List<View> mFooterViews = null;

    public SwipeRefreshRecyclerView(Context context) {
        this(context, null);
    }

    public SwipeRefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mRecyclerView = new LuRecyclerView(context);
        mRecyclerView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        setColorSchemeResources(R.color.blue, R.color.yellow, R.color.green, R.color.red);
        addView(mRecyclerView);
    }

    /**
     * @param layoutId
     */
    public void addHeaderView(@LayoutRes int layoutId) {
        View v = View.inflate(getContext(), layoutId, null);
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(rlp);
        addHeaderView(v);
    }

    /**
     * 调用setAdapter后生效
     *
     * @param v
     */
    public void addHeaderView(View v) {
        if (mAdapter == null) {
            if (mHeaderViews == null)
                mHeaderViews = new ArrayList<>();
            mHeaderViews.add(v);
        } else
            mAdapter.addHeaderView(v);
    }

    /**
     * 调用setAdapter后生效
     *
     * @param v
     */
    public void addFooterView(View v) {
        if (mAdapter == null) {
            if (mFooterViews == null)
                mFooterViews = new ArrayList<>();
            mFooterViews.add(v);
        } else
            mAdapter.addFooterView(v);
    }

    /**
     * @param layoutId
     */
    public void addFooterView(@LayoutRes int layoutId) {
        View v = View.inflate(getContext(), layoutId, null);
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(rlp);
        addFooterView(v);
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void setLayoutManager(RecyclerView.LayoutManager manager) {
        mRecyclerView.setLayoutManager(manager);
    }

    /**
     * 请勿多次调用
     *
     * @param adapter 适配器
     */
    public void setAdapter(BaseRecyclerAdapter adapter) {
        mAdapter = new LuRecyclerViewAdapter(adapter);
        if (mHeaderViews != null && mHeaderViews.size() > 0) {
            for (int i = 0; i < mHeaderViews.size(); i++) {
                mAdapter.addHeaderView(mHeaderViews.get(i));
            }
            mHeaderViews.clear();
        }
        if (mFooterViews != null && mFooterViews.size() > 0) {
            for (int i = 0; i < mFooterViews.size(); i++) {
                mAdapter.addFooterView(mFooterViews.get(i));
            }
            mFooterViews.clear();
        }
        mRecyclerView.setAdapter(mAdapter);
    }

    public void refreshComplete() {
        if (isRefreshing()) {
            setRefreshing(false);
        } else
            mRecyclerView.refreshComplete(itemCount);
    }

    public void closeLoadMore() {
        mRecyclerView.setNoMore(true);
    }

    private int itemCount;//一页数量

    public void setRefreshListener(IRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
        super.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                SwipeRefreshRecyclerView.this.mRecyclerView.setRefreshing(true);
                SwipeRefreshRecyclerView.this.refreshListener.onRefresh();
            }
        });
    }

    public void setLoadMoreListener(ILoadMoreListener loadMoreListener, int itemCount) {
        this.loadMoreListener = loadMoreListener;
        this.itemCount = itemCount;
        this.mRecyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                SwipeRefreshRecyclerView.this.loadMoreListener.onLoadMore();
            }
        });
        //设置底部加载颜色
        this.mRecyclerView.setFooterViewColor(R.color.colorAccent, R.color.black, android.R.color.white);
        //设置底部加载文字提示
        this.mRecyclerView.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧");
    }

    public interface IRefreshListener {
        void onRefresh();
    }

    public interface ILoadMoreListener {
        void onLoadMore();
    }
}
