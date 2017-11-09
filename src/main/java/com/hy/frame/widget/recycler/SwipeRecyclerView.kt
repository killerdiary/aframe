package com.hy.frame.widget.recycler

import android.content.Context
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import com.hy.frame.R


/**
 * RecyclerView
 * @author HeYan
 * @time 2016/5/28 9:19
 */
class SwipeRecyclerView constructor(context: Context, attrs: AttributeSet? = null) : SwipeRefreshLayout(context, attrs) {
    private val mRecyclerView: RecyclerView = RecyclerView(context)
    private var mLoadMoreListener: ILoadMoreListener? = null
    private var mRefreshListener: IRefreshListener? = null
    private var loadMoreView: LoadMoreView? = null
    //    private var refreshView: RefreshView? = null
//    private var loadCache: LoadCache? = null
    var loadMoreEnabled: Boolean = false
        set(value) {
            field = value
            if (value && adapter != null && initLoadMore()) {
                adapter?.setLoadMoreView(loadMoreView!!)
            } else if (value && loadMoreView != null) {
                loadMoreView?.loadComplete()
            } else if (!value && loadMoreView != null) {
                loadMoreView?.close()
            }
        }
    var refreshEnabled: Boolean
        set(value) {
            isEnabled = value
        }
        get() = isEnabled

    init {
        init(context)
    }

    private fun init(context: Context) {
        mRecyclerView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        setColorSchemeResources(R.color.blue, R.color.yellow, R.color.green, R.color.red)
        addView(mRecyclerView)
        isEnabled = false
    }

    var adapter: BaseRecyclerAdapter<*>? = null
        set(value) {
            if (value == null) return
            if (loadMoreEnabled && initLoadMore()) {
                value.setLoadMoreView(loadMoreView!!)
            }
            field = value
            mRecyclerView.adapter = value
        }

    private fun initLoadMore(): Boolean {
        if (loadMoreView == null) {
            loadMoreView = LoadMoreView(View.inflate(context, R.layout.item_recycler_loadmore, null))
            loadMoreView?.setLoadMoreViewListener(object : LoadMoreView.ILoadMoreViewListener {
                override fun onLoading() {
                    if (isRefreshing)
                        loadMoreView?.loadComplete()
                    else
                        mLoadMoreListener?.onLoadMore()
                }
            })
            return true
        }
        return false
    }

    var layoutManager: RecyclerView.LayoutManager?
        set(value) {
            mRecyclerView.layoutManager = value
        }
        get() = mRecyclerView.layoutManager

    fun getRecyclerView(): RecyclerView = mRecyclerView

    fun addItemDecoration(decor: RecyclerView.ItemDecoration) {
        mRecyclerView.addItemDecoration(decor)
    }

    /**
     * 刷新完成
     */
    fun refreshComplete() {
        if (isRefreshing)
            isRefreshing = false
        else if (loadMoreEnabled)
            loadMoreView?.loadComplete()
    }

    /**
     * 加载完成
     */
    fun loadMoreComplete() {
        loadMoreView?.loadComplete()
    }

    /**
     * 加载错误，点击重试
     */
    fun loadMoreError() {
        loadMoreView?.loadError()
    }

    /**
     * 没有更多
     */
    fun closeLoadMore() {
        loadMoreView?.close()
    }

    fun setLoadMoreBackgroundResource(@DrawableRes resId: Int) {
        loadMoreView?.setBackgroundResource(resId)
    }

    fun setLoadMoreBackgroundColor(@ColorInt color: Int) {
        loadMoreView?.setBackgroundColor(color)
    }

    private var itemCount: Int = 0//一页数量

    fun setRefreshListener(refreshListener: IRefreshListener, refreshEnabled: Boolean = true) {
        this.refreshEnabled = refreshEnabled
        this.mRefreshListener = refreshListener
        setOnRefreshListener {
            if (loadMoreView != null && loadMoreView!!.isLoading()) {
                isRefreshing = false
                return@setOnRefreshListener
            }
            //itemCount = 1
            mRefreshListener?.onRefresh()
        }
    }

    fun setLoadMoreListener(loadMoreListener: ILoadMoreListener, itemCount: Int = 10) {
        this.mLoadMoreListener = loadMoreListener
        this.itemCount = itemCount
//        this.mRecyclerView!!.setOnLoadMoreListener { this@SwipeRecyclerView.loadMoreListener!!.onLoadMore() }
//        //设置底部加载颜色
//        this.mRecyclerView!!.setFooterViewColor(R.color.colorAccent, R.color.black, android.R.color.white)
//        //设置底部加载文字提示
//        this.mRecyclerView!!.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")

    }

    interface IRefreshListener {
        fun onRefresh()
    }

    interface ILoadMoreListener {
        fun onLoadMore()
    }
}