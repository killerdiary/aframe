package com.hy.frame.widget.recycler

import android.content.Context
import android.support.annotation.IdRes
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hy.frame.adapter.IAdapterListener
import com.hy.frame.adapter.IAdapterLongListener


/**
 * Base Adapter For RecyclerView
 * @author HeYan
 * @time 2017/9/25 11:45
 */
abstract class BaseRecyclerAdapter<T, H : BaseHolder> constructor(protected val context: Context, protected var datas: List<T>?, protected var listener: IAdapterListener<T>? = null) : RecyclerView.Adapter<BaseHolder>() {

    private var mHeaderViews: MutableList<View>? = null
    private var mFooterViews: MutableList<View>? = null
    private var mEmptyView: View? = null
    private var loadMoreView: LoadMoreView? = null
    private var refreshView: RefreshView? = null

    fun addHeaderView(v: View, index: Int = -1) {
        if (mHeaderViews == null)
            mHeaderViews = ArrayList()
        else if (mHeaderViews!!.size >= HEADER_SIZE_MAX)
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
        var i = index
        if (this.loadMoreView != null && i > 0)
            i--
        if (i >= 0)
            mFooterViews?.add(i, v)
        else
            mFooterViews?.add(v)
    }

    fun setEmptyView(v: View) {
        mEmptyView = v
    }

    fun setLoadMoreView(loadMoreView: LoadMoreView) {
        if (this.loadMoreView != null) return
        addFooterView(loadMoreView.v)
        this.loadMoreView = loadMoreView
    }

    fun setRefreshView(refreshView: RefreshView) {
        if (this.refreshView != null) return
        addHeaderView(refreshView.v, 0)
        this.refreshView = refreshView
    }

    protected fun inflate(resId: Int): View {
        return LayoutInflater.from(context).inflate(resId, null)
    }

    fun getHeaderCount(): Int = mHeaderViews?.size ?: 0

    fun getFooterCount(): Int = mFooterViews?.size ?: 0

    fun getDataCount(): Int = datas?.size ?: 0

    override fun getItemCount(): Int {
        var count = getDataCount()
        if (count == 0 && mEmptyView != null)
            count++
        count += getHeaderCount()
        count += getFooterCount()
        return count
    }

    @Deprecated("Deprecated")
    override fun getItemViewType(position: Int): Int {
        val headerCount = getHeaderCount()
        val dataCount = getDataCount()
        if (position < headerCount)
            return TYPE_HEADER + position
        var footerLimit = headerCount + dataCount
        if (dataCount == 0) {
            if (mEmptyView != null)
                footerLimit++
            if (position < footerLimit)
                return TYPE_EMPTY
        }
        if (position >= footerLimit)
            return TYPE_FOOTER + position - footerLimit
        return getCurViewType(position - headerCount)
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
        if (viewType in TYPE_FOOTER..TYPE_FOOTER_END)
            return createFooterView(parent, viewType - TYPE_FOOTER)
        if (viewType == TYPE_EMPTY)
            return createEmptyView(parent)
        return createView(parent, viewType)
    }

    @Deprecated("Deprecated")
    override fun onBindViewHolder(holder: BaseHolder, position: Int) {
        val viewType = holder.itemViewType
        if (viewType in TYPE_ITEM..TYPE_ITEM_END)
            @Suppress("UNCHECKED_CAST")
            bindViewData(holder as H, position - getHeaderCount())
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
            if (viewType == TYPE_FOOTER + getFooterCount() - 1) {
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

    protected fun isFixedViewType(viewType: Int): Boolean {
        return viewType in TYPE_HEADER..TYPE_HEADER_END || viewType in TYPE_FOOTER..TYPE_FOOTER_END || viewType == TYPE_EMPTY
    }

    private var dividerHorizontalSize: Int = 0
    private var dividerVerticalSize: Int = 0
    private var topPadding: Int = 0
    private var bottomPadding: Int = 0

    fun setDividerHorizontalSize(dividerHorizontalSize: Int) {
        this.dividerHorizontalSize = dividerHorizontalSize
    }

    fun setDividerVerticalSize(dividerVerticalSize: Int) {
        this.dividerVerticalSize = dividerVerticalSize
    }

    fun setTopPadding(topPadding: Int) {
        this.topPadding = topPadding
    }

    fun setBottomPadding(bottomPadding: Int) {
        this.bottomPadding = bottomPadding
    }

    /**
     * Cur True Position
     */
    @Deprecated("")
    fun getCurPosition(position: Int): Int {
        return position - getHeaderCount()
    }

    fun getItem(position: Int): T {
        return datas!![position]
    }

    fun refresh() {
        this.notifyDataSetChanged()
    }

    fun refresh(beans: List<T>?) {
        this.datas = beans
        this.notifyDataSetChanged()
    }

    /**
     * create child View
     */
    protected abstract fun createView(parent: ViewGroup, viewType: Int): H

    protected open fun createHeaderView(parent: ViewGroup, index: Int): BaseHolder {
        return BaseHolder(mHeaderViews!![index])
    }

    protected open fun createFooterView(parent: ViewGroup, index: Int): BaseHolder {
        return BaseHolder(mFooterViews!![index])
    }

    protected open fun createEmptyView(parent: ViewGroup): BaseHolder {
        return BaseHolder(mEmptyView!!)
    }

    protected open fun bindOtherViewData(holder: BaseHolder, viewType: Int) {

    }

    /**
     * bind child data
     */
    protected abstract fun bindViewData(holder: H, position: Int)

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

    open inner class BaseClickHolder(v: View) : BaseHolder(v), View.OnClickListener {

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

    open inner class BaseLongClickHolder(v: View) : BaseClickHolder(v), View.OnLongClickListener {

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
        val TYPE_ITEM = 0
        val TYPE_ITEM_END = 99
        val TYPE_HEADER = 100
        val TYPE_HEADER_END = 110
        val TYPE_FOOTER = 200
        val TYPE_FOOTER_END = 210
        val TYPE_EMPTY = 300
        val HEADER_SIZE_MAX = 10
        val FOOTER_SIZE_MAX = 10
    }
}
