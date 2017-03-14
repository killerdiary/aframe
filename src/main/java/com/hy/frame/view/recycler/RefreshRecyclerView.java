package com.hy.frame.view.recycler;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
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
public class RefreshRecyclerView extends LRecyclerView {
    private ILoadMoreListener loadMoreListener;
    private IRefreshListener refreshListener;
    private LRecyclerViewAdapter mAdapter;
    private List<View> mHeaderViews = null;
    private List<View> mFooterViews = null;

    public RefreshRecyclerView(Context context) {
        this(context, null);
    }

    public RefreshRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
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


    /**
     * 请勿多次调用
     *
     * @param adapter 适配器
     */
    public void setAdapter(BaseRecyclerAdapter adapter) {
        mAdapter = new LRecyclerViewAdapter(adapter);
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
        adapter.setHeaderCount(mAdapter.getHeaderViewsCount() + 1);
        super.setAdapter(mAdapter);
    }

    public void refreshComplete() {
        refreshComplete(itemCount);
    }

    public void closeLoadMore() {
        setNoMore(true);
    }

    private int itemCount;//一页数量

    public void setRefreshListener(IRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
        super.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefreshRecyclerView.this.refreshListener.onRefresh();
            }
        });
    }

    public void setLoadMoreListener(ILoadMoreListener loadMoreListener, int itemCount) {
        this.loadMoreListener = loadMoreListener;
        this.itemCount = itemCount;
        this.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                RefreshRecyclerView.this.loadMoreListener.onLoadMore();
            }
        });
        //设置底部加载颜色
        this.setFooterViewColor(R.color.colorAccent, R.color.black, android.R.color.white);
        //设置底部加载文字提示
        this.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧");
    }

    public interface IRefreshListener {
        void onRefresh();
    }

    public interface ILoadMoreListener {
        void onLoadMore();
    }
}
