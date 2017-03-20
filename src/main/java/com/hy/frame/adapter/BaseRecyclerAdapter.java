package com.hy.frame.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * BaseRecyclerAdapter
 *
 * @author HeYan
 * @time 2016/5/27 16:22
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter {
    //    public static final int TYPE_HEADER = 1;
    //public static final int TYPE_MORE = 2;
    private Context context;
    private List<T> datas;
    private IAdapterListener listener;
    //private boolean isCanLoadMore;
    //private int loadMoreState;
//    private int headerResId;
//    private IHeaderViewListner headerListner;
    private int headerCount;

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

//    /**
//     * 只适用于ListView格式，如果内容不超过一屏，请用新方式
//     *
//     * @param headerResId
//     */
//    @Deprecated
//    public void setHeaderResId(int headerResId) {
//        this.headerResId = headerResId;
//    }
//
//    @Deprecated
//    public void setHeaderListner(IHeaderViewListner headerListner) {
//        this.headerListner = headerListner;
//    }

    protected View inflate(ViewGroup parent, int resId) {
        return LayoutInflater.from(context).inflate(resId, null);
    }

    @SuppressWarnings("unchecked")
    protected <V> V getView(View v, int resId) {
        return (V) v.findViewById(resId);
    }

    private int gridCount;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //if (viewType == TYPE_MORE)
        //    return new LoadMoreHolder(inflate(parent, R.layout.in_recycler_footer));
        boolean isGrid = ((RecyclerView) parent).getLayoutManager() instanceof GridLayoutManager;
        if (isGrid) {
            gridCount = ((GridLayoutManager) ((RecyclerView) parent).getLayoutManager()).getSpanCount();
            if (dividerHorizontalSize > 0) {
                parent.setPadding(dividerHorizontalSize, parent.getPaddingTop(), parent.getPaddingRight(), parent.getPaddingBottom());
            }
        }
//        if (viewType == TYPE_HEADER) {
//            View header = inflate(parent, headerResId);
//            if (headerListner != null)
//                return headerListner.createHeaderView(header);
//            return new HeaderHolder(header);
//        }
        return createView(parent);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (dividerHorizontalSize > 0 || dividerVerticalSize > 0 || topPadding > 0 || bottomPadding > 0) {
            int padding = dividerVerticalSize;
            int left = 0, top = 0, right = 0, bottom = 0;
            left = holder.itemView.getPaddingLeft();
            right = holder.itemView.getPaddingRight();
            bottom = holder.itemView.getPaddingBottom();
            if (position == 0 && topPadding > 0) {
                top = topPadding;
            } else if (dividerVerticalSize > 0) {
                top = padding;
            }
            if (bottomPadding > 0) {
                bottom = 0;
                if (gridCount <= 1 && getItemCount() - (position + 1) == 0) {
                    bottom = bottomPadding;
                } else if (gridCount > 1) {
                    int curPosition = getCurPosition(position) + 1;
                    int lastLinePosition = 0;
                    int surplus = getItemCount() % gridCount;
                    if (surplus == 0) {
                        lastLinePosition = getItemCount() - gridCount;
                    } else {
                        lastLinePosition = getItemCount() - surplus;
                    }
                    if (curPosition > lastLinePosition) {
                        bottom = bottomPadding;
                    }
                }
            }
            if (gridCount > 1 && dividerHorizontalSize > 0) {
                right = dividerHorizontalSize;
            }
            holder.itemView.setPadding(left, top, right, bottom);
        }
        bindViewData(holder, position);
    }

    private int dividerHorizontalSize, dividerVerticalSize, topPadding, bottomPadding;

    @Deprecated
    public void setDividerSize(int dividerSize) {
        setDividerVerticalSize(dividerSize);
    }

    public void setDividerHorizontalSize(int dividerHorizontalSize) {
        this.dividerHorizontalSize = dividerHorizontalSize;
    }

    public void setDividerVerticalSize(int dividerVerticalSize) {
        this.dividerVerticalSize = dividerVerticalSize;
    }

    public void setTopPadding(int topPadding) {
        this.topPadding = topPadding;
    }

    public void setBottomPadding(int bottomPadding) {
        this.bottomPadding = bottomPadding;
    }

    public void setHeaderCount(int headerCount) {
        this.headerCount = headerCount;
    }

    public int getHeaderCount() {
        return headerCount;
    }

    /**
     * Cur True Position
     */
    public int getCurPosition(int position) {
        return position - headerCount;
    }

    public T getItem(int position) {
        return datas.get(position);
    }

    public List<T> getDatas() {
        return datas;
    }

    public void refresh(List<T> datas) {
        this.datas = datas;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
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
