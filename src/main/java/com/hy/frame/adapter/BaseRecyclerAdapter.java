package com.hy.frame.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hy.frame.R;
import com.hy.frame.view.recycler.LoadMoreHolder;

import java.util.List;

/**
 * BaseRecyclerAdapter
 *
 * @author HeYan
 * @time 2016/5/27 16:22
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter {
    public static final int TYPE_HEADER = 1;
    public static final int TYPE_FOOTER = 2;
    private Context context;
    private List<T> datas;
    private IAdapterListener listener;
    private boolean isCanLoadMore;
    private int loadMoreState;

    public BaseRecyclerAdapter(Context context, List<T> datas) {
        this.context = context;
        this.datas = datas;
    }

    public BaseRecyclerAdapter(Context context, List<T> datas, IAdapterListener listener) {
        this.context = context;
        this.datas = datas;
        this.listener = listener;
    }

    protected Context getContext() {
        return context;
    }

    protected IAdapterListener getListener() {
        return listener;
    }

    public void setListener(IAdapterListener listener) {
        this.listener = listener;
    }

    public void setCanLoadMore(boolean isCanLoadMore) {
        this.isCanLoadMore = isCanLoadMore;
    }

    public void setLoadMoreState(int state) {
        this.loadMoreState = state;
    }

    protected View inflate(ViewGroup parent, int resId) {
        return LayoutInflater.from(context).inflate(resId, null);
    }

    @SuppressWarnings("unchecked")
    protected <V> V getView(View v, int resId) {
        return (V) v.findViewById(resId);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER)
            return new LoadMoreHolder(inflate(parent, R.layout.in_recycler_footer));
        return createView(parent);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LoadMoreHolder) {
            LoadMoreHolder footer = (LoadMoreHolder) holder;
            footer.onChangeState(loadMoreState);
        } else {
            bindViewData(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        int size = datas == null ? 0 : datas.size();
        if (size > 0 && isCanLoadMore) {
            size = size + 1;
        }
        return size;
    }

    public T getItem(int position) {
        return datas.get(position);
    }

    public void refresh(List<T> datas) {
        this.datas = datas;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为footerView
        if (isCanLoadMore && position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        }
        return super.getItemViewType(position);
    }

    /**
     * create child View
     */
    protected abstract RecyclerView.ViewHolder createView(ViewGroup parent);

    /**
     * bind child data
     */
    protected abstract void bindViewData(RecyclerView.ViewHolder holder, int position);
}
