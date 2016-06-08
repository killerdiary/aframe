package com.hy.frame.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hy.frame.view.recycler.HeaderHolder;
import com.hy.frame.view.recycler.IHeaderViewListner;

import java.util.List;

/**
 * BaseRecyclerAdapter
 *
 * @author HeYan
 * @time 2016/5/27 16:22
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter {
    public static final int TYPE_HEADER = 1;
    //public static final int TYPE_MORE = 2;
    private Context context;
    private List<T> datas;
    private IAdapterListener listener;
    //private boolean isCanLoadMore;
    //private int loadMoreState;
    private int headerResId;
    private IHeaderViewListner headerListner;

    public BaseRecyclerAdapter(Context context, List<T> datas) {
        this(context, datas, null);
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

//    public void setCanLoadMore(boolean isCanLoadMore) {
//        this.isCanLoadMore = isCanLoadMore;
//    }
//
//    public void setLoadMoreState(int state) {
//        this.loadMoreState = state;
//    }

    /**
     * 只适用于ListView格式，如果内容不超过一屏，请用新方式
     *
     * @param headerResId
     */
    @Deprecated
    public void setHeaderResId(int headerResId) {
        this.headerResId = headerResId;
    }

    @Deprecated
    public void setHeaderListner(IHeaderViewListner headerListner) {
        this.headerListner = headerListner;
    }

    /**
     * Cur True Position
     */
    @Deprecated
    protected int getCurPosition(int position) {
        return position - (headerResId != 0 ? 1 : 0);
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
        //if (viewType == TYPE_MORE)
        //    return new LoadMoreHolder(inflate(parent, R.layout.in_recycler_footer));
        if (viewType == TYPE_HEADER) {
            View header = inflate(parent, headerResId);
            if (headerListner != null)
                return headerListner.createHeaderView(header);
            return new HeaderHolder(header);
        }
        return createView(parent);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //if (holder instanceof LoadMoreHolder) {
        //    LoadMoreHolder footer = (LoadMoreHolder) holder;
        //    footer.onChangeState(loadMoreState);
        //} else
        if (holder instanceof HeaderHolder) {
            if (headerListner != null)
                headerListner.bindHearderData((HeaderHolder) holder, position);
        } else {
            bindViewData(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        int size = datas == null ? 0 : datas.size();
//        if (size > 0 && isCanLoadMore) {
//            size++;
//        }
        if (headerResId != 0) {
            size++;
        }
        return size;
    }

    public T getItem(int position) {
        if (headerResId != 0)
            position = position - 1;
        return datas.get(position);
    }

    public void refresh(List<T> datas) {
        this.datas = datas;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为footerView
//        if (isCanLoadMore && position + 1 == getItemCount()) {
//            return TYPE_MORE;
//        }
        if (headerResId != 0 && position == 0) {
            return TYPE_HEADER;
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
