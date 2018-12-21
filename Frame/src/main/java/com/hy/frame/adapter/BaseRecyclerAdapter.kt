package com.hy.frame.adapter

import android.content.Context
import android.support.annotation.IdRes
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hy.frame.R
import com.hy.frame.bean.LoadCache
import com.hy.frame.widget.recycler.BaseHolder
import com.hy.frame.widget.recycler.LoadMoreView


/**
 * Base Adapter For RecyclerView
 * @author HeYan
 * @time 2017/9/25 11:45
 */
abstract class BaseRecyclerAdapter<T> constructor(protected val context: Context, protected var datas: MutableList<T>?, protected var listener: IAdapterListener<T>? = null) : RecyclerView.Adapter<BaseHolder>() {

    private var mHeaderViews: MutableList<View>? = null
    private var mFooterViews: MutableList<View>? = null
    private var emptyView: LoadCache? = null
    private var loadMoreView: LoadMoreView? = null

    fun addHeaderView(v: View, index: Int = -1) {
        if (mHeaderViews == null)
            mHeaderViews = ArrayList()
        else if (getHeaderCount() >= HEADER_SIZE_MAX)
            throw IndexOutOfBoundsException("The maximum limit of $HEADER_SIZE_MAX more than HeaderView")
        if (index >= 0)
            mHeaderViews?.add(index, v)
        else
            mHeaderViews?.add(v)
    }

    fun addFooterView(v: View, index: Int = -1) {
        if (mFooterViews == null)
            mFooterViews = ArrayList()
        else if (mFooterViews!!.size >= FOOTER_SIZE_MAX)
            throw IndexOutOfBoundsException("The maximum limit of $FOOTER_SIZE_MAX more than FooterView")
        if (index >= 0)
            mFooterViews?.add(index, v)
        else
            mFooterViews?.add(v)
    }

    /**
     * once
     */
    fun setEmptyView(emptyView: LoadCache = LoadCache(inflate(R.layout.in_loading))) {
        if (this.emptyView != null) return
        val vlp = emptyView.llyLoad!!.layoutParams
                ?: ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        emptyView.llyLoad!!.layoutParams = vlp
        emptyView.showNoData(context.getString(R.string.hint_nodata), R.drawable.v_warn)
        this.emptyView = emptyView
    }

    fun getEmptyView(): LoadCache? = emptyView

    fun showNoData(msg: String? = context.getString(R.string.hint_nodata), drawId: Int = R.drawable.v_warn) {
        if (this.emptyView == null) return
        this.emptyView?.showNoData(msg, drawId)
    }

    fun showLoading(msg: String? = null) {
        if (this.emptyView == null) return
        this.emptyView?.showLoading(msg ?: context.getString(R.string.loading))
    }

    /**
     * once
     */
    fun setLoadMoreView(loadMoreView: LoadMoreView) {
        if (this.loadMoreView != null) return
        this.loadMoreView = loadMoreView
    }

    fun getLoadMoreView(): LoadMoreView? = loadMoreView
//    /**
//     * once
//     */
//    fun setRefreshView(refreshView: RefreshView = RefreshView(inflate(R.layout.in_lv_header))) {
//        if (this.refreshView != null) return
//        this.refreshView = refreshView
//    }
//
//    fun getRefreshView(): RefreshView? = refreshView

    protected fun inflate(resId: Int): View {
        return LayoutInflater.from(context).inflate(resId, null)
    }

    fun getHeaderCount(): Int = mHeaderViews?.size ?: 0

    fun getFooterCount(): Int = mFooterViews?.size ?: 0

    fun getDataCount(): Int = datas?.size ?: 0

    override fun getItemCount(): Int {
        var count = getDataCount()
        if (count == 0) {
            if (emptyView != null)
                count++
        } else {
            if (loadMoreView != null)
                count++
        }
        count += getHeaderCount()
        count += getFooterCount()
        return count
    }

    @Deprecated("Deprecated")
    override fun getItemViewType(position: Int): Int {
        val headerCount = getHeaderCount()
        val dataCount = getDataCount()
        if (dataCount == 0) {
            if (position < headerCount)
                return TYPE_HEADER + position
            var footerLimit = headerCount + dataCount
            if (dataCount == 0) {
                if (emptyView != null)
                    footerLimit++
                if (position < footerLimit)
                    return TYPE_EMPTY
            }
            if (position >= footerLimit)
                return TYPE_FOOTER + position - footerLimit
            return getCurViewType(position - headerCount)
        } else {
            if (position < headerCount)
                return TYPE_HEADER + position - (headerCount - headerCount)
            val footerLimit = headerCount + dataCount
            if (position >= footerLimit)
                return if (loadMoreView != null && position == footerLimit + getFooterCount()) TYPE_LOADMORE else TYPE_FOOTER + position - footerLimit
            return getCurViewType(position - headerCount)
        }
    }

    /**
     * @return ViewType between[0-99]
     */
    protected open fun getCurViewType(position: Int): Int {
        return TYPE_ITEM
    }

    @Deprecated("Deprecated")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        if (viewType in TYPE_HEADER..TYPE_HEADER_END)
            return createHeaderView(parent, viewType - TYPE_HEADER)
        if (viewType == TYPE_EMPTY)
            return createEmptyView(parent)
        if (viewType in TYPE_FOOTER..TYPE_FOOTER_END)
            return createFooterView(parent, viewType - TYPE_FOOTER)
        if (viewType == TYPE_LOADMORE)
            return BaseHolder(loadMoreView!!.v)
        return createView(parent, viewType)
    }

    @Deprecated("Deprecated")
    override fun onBindViewHolder(holder: BaseHolder, position: Int) {
        val viewType = holder.itemViewType
        if (viewType in TYPE_ITEM..TYPE_ITEM_END)
            bindViewData(holder, position - getHeaderCount())
        else
            bindOtherViewData(holder, viewType)
    }

    @Deprecated("Deprecated")
    override fun onViewAttachedToWindow(holder: BaseHolder) {
        val viewType = holder.itemViewType
        if (isFixedViewType(viewType)) {
            if (holder.itemView.layoutParams is StaggeredGridLayoutManager.LayoutParams) {
                val params = holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
                params.isFullSpan = true
            }
            if (viewType == TYPE_LOADMORE) {
                loadMoreView?.onViewAttachedToWindow()
            }
        }
    }

    @Deprecated("Deprecated")
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        val manager = recyclerView.layoutManager
        if (manager is GridLayoutManager) {
            manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val viewType = getItemViewType(position)
                    if (isFixedViewType(viewType)) {
                        return manager.spanCount
                    }
                    return 1
                }
            }
        }
    }

    protected open fun isFixedViewType(viewType: Int): Boolean {
        return viewType in TYPE_HEADER..TYPE_HEADER_END || viewType in TYPE_FOOTER..TYPE_FOOTER_END || viewType == TYPE_EMPTY || viewType == TYPE_LOADMORE
    }

    /**
     * Cur True Position
     */
    @Deprecated("Deprecated", ReplaceWith("position - getHeaderCount()"))
    fun getCurPosition(position: Int): Int {
        return position - getHeaderCount()
    }

    fun getItem(position: Int): T {
        return datas!![position]
    }

    fun refresh() {
        this.notifyDataSetChanged()
    }

    fun refresh(beans: MutableList<T>?) {
        this.datas = beans
        this.notifyDataSetChanged()
    }

    /**
     * create child View
     */
    protected abstract fun createView(parent: ViewGroup, viewType: Int): BaseHolder

    protected open fun createHeaderView(parent: ViewGroup, index: Int): BaseHolder {
        return BaseHolder(mHeaderViews!![index])
    }

    protected open fun createFooterView(parent: ViewGroup, index: Int): BaseHolder {
        return BaseHolder(mFooterViews!![index])
    }

    protected open fun createEmptyView(parent: ViewGroup): BaseHolder {
        return BaseHolder(emptyView!!.v!!)
    }

    protected open fun bindOtherViewData(holder: BaseHolder, viewType: Int) {

    }

    /**
     * bind child data
     */
    protected abstract fun bindViewData(holder: BaseHolder, position: Int)

    /**
     * 获取 控件
     * @param id 行布局中某个组件的id
     * @param parent  parent
     */
    protected fun <V : View> findViewById(@IdRes id: Int, parent: View?): V? {
        return parent?.findViewById(id)
    }

    /**
     * 获取并绑定点击
     * @param id 行布局中某个组件的id
     * @param parent  parent
     * @param parent a{@link  View.OnClickListener}
     */
    protected fun <V : View> setOnClickListener(@IdRes id: Int, parent: View?, listener: View.OnClickListener): V? {
        val view = findViewById<V>(id, parent)
        view?.setOnClickListener(listener) ?: return null
        return view
    }

    open inner class BaseClickHolder(v: View, bindItemClick: Boolean = false) : BaseHolder(v), View.OnClickListener {

        init {
            if (bindItemClick)
                setOnClickListener(v)
        }

        override fun onClick(v: View) {
            if (listener != null) {
                var position = adapterPosition
                if (position < 0) return
                position = getCurPosition(position)
                if (position < 0) return
                listener!!.onViewClick(v, getItem(position), position)
            }
        }

        fun setOnClickListener(v: View?) {
            v?.setOnClickListener(this)
        }

        /**
         * 获取并绑定点击
         * @param id 行布局中某个组件的id
         * @param parent  parent
         */
        protected fun <V : View> setOnClickListener(@IdRes id: Int, parent: View? = null): V? {
            val view = findViewById<V>(id, parent)
            view?.setOnClickListener(this)
            return view
        }
    }

    open inner class BaseLongClickHolder(v: View, bindItemClick: Boolean = false) : BaseClickHolder(v, bindItemClick), View.OnLongClickListener {

        override fun onLongClick(v: View): Boolean {
            if (listener != null && listener is IAdapterLongListener<T>) {
                var position = adapterPosition
                if (position < 0) return false
                position = getCurPosition(position)
                if (position < 0) return false
                (listener as IAdapterLongListener<T>).onViewLongClick(v, getItem(position), position)
            }
            return false
        }

        fun setOnLongClickListener(v: View?) {
            v?.setOnLongClickListener(this)
        }

        fun <V : View> setOnLongClickListener(@IdRes id: Int, parent: View? = null): V? {
            val v = findViewById<V>(id, parent)
            setOnLongClickListener(v)
            return v
        }
    }

    companion object {
        const val TYPE_ITEM = 0
        const val TYPE_ITEM_END = 99
        //const val TYPE_REFRESH = 100
        const val TYPE_HEADER = 101
        const val TYPE_HEADER_END = 110
        const val TYPE_LOADMORE = 200
        const val TYPE_FOOTER = 201
        const val TYPE_FOOTER_END = 210
        const val TYPE_EMPTY = 300
        const val HEADER_SIZE_MAX = 10
        const val FOOTER_SIZE_MAX = 10
    }
}
