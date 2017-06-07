package com.hy.frame.widget.recycler

import android.content.Context
import android.support.annotation.LayoutRes
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.github.jdsjlzx.recyclerview.LuRecyclerView
import com.github.jdsjlzx.recyclerview.LuRecyclerViewAdapter
import com.hy.frame.R
import com.hy.frame.adapter.BaseRecyclerAdapter
import java.util.*

/**
 * RecyclerView 上下拉刷新加载更多 适用于为ListView格式 版本>=11
 * @author HeYan
 * @time 2016/5/28 9:19
 */
class SwipeRefreshRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : SwipeRefreshLayout(context, attrs) {
    private var mRecyclerView: LuRecyclerView? = null
    private var loadMoreListener: ILoadMoreListener? = null
    private var refreshListener: IRefreshListener? = null
    private var mAdapter: LuRecyclerViewAdapter? = null
    private var mHeaderViews: MutableList<View>? = null
    private var mFooterViews: MutableList<View>? = null

    init {
        init(context)
    }

    private fun init(context: Context) {
        mRecyclerView = LuRecyclerView(context)
        mRecyclerView!!.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        mRecyclerView!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        setColorSchemeResources(R.color.blue, R.color.yellow, R.color.green, R.color.red)
        addView(mRecyclerView)
    }

    /**
     * @param layoutId
     */
    fun addHeaderView(@LayoutRes layoutId: Int) {
        val v = View.inflate(context, layoutId, null)
        val rlp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        v.layoutParams = rlp
        addHeaderView(v)
    }

    /**
     * 调用setAdapter后生效

     * @param v
     */
    fun addHeaderView(v: View) {
        if (mAdapter == null) {
            if (mHeaderViews == null)
                mHeaderViews = ArrayList<View>()
            mHeaderViews!!.add(v)
        } else
            mAdapter!!.addHeaderView(v)
    }

    /**
     * 调用setAdapter后生效

     * @param v
     */
    fun addFooterView(v: View) {
        if (mAdapter == null) {
            if (mFooterViews == null)
                mFooterViews = ArrayList<View>()
            mFooterViews!!.add(v)
        } else
            mAdapter!!.addFooterView(v)
    }

    /**
     * @param layoutId
     */
    fun addFooterView(@LayoutRes layoutId: Int) {
        val v = View.inflate(context, layoutId, null)
        val rlp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        v.layoutParams = rlp
        addFooterView(v)
    }

    val recyclerView: RecyclerView?
        get() = mRecyclerView

    fun setLayoutManager(manager: RecyclerView.LayoutManager) {
        mRecyclerView!!.layoutManager = manager
    }

    /**
     * 请勿多次调用

     * @param adapter 适配器
     */
    fun setAdapter(adapter: BaseRecyclerAdapter<*>) {
        mAdapter = LuRecyclerViewAdapter(adapter)
        if (mHeaderViews != null && mHeaderViews!!.size > 0) {
            for (i in mHeaderViews!!.indices) {
                mAdapter!!.addHeaderView(mHeaderViews!![i])
            }
            mHeaderViews!!.clear()
        }
        if (mFooterViews != null && mFooterViews!!.size > 0) {
            for (i in mFooterViews!!.indices) {
                mAdapter!!.addFooterView(mFooterViews!![i])
            }
            mFooterViews!!.clear()
        }
        mRecyclerView!!.adapter = mAdapter
    }

    fun refreshComplete() {
        if (isRefreshing) {
            isRefreshing = false
        } else
            mRecyclerView!!.refreshComplete(itemCount)
    }

    fun closeLoadMore() {
        mRecyclerView!!.setNoMore(true)
    }

    private var itemCount: Int = 0//一页数量

    fun setRefreshListener(refreshListener: IRefreshListener) {
        this.refreshListener = refreshListener
        super.setOnRefreshListener {
            this@SwipeRefreshRecyclerView.mRecyclerView!!.setRefreshing(true)
            this@SwipeRefreshRecyclerView.refreshListener!!.onRefresh()
        }
    }

    fun setLoadMoreListener(loadMoreListener: ILoadMoreListener, itemCount: Int) {
        this.loadMoreListener = loadMoreListener
        this.itemCount = itemCount
        this.mRecyclerView!!.setOnLoadMoreListener { this@SwipeRefreshRecyclerView.loadMoreListener!!.onLoadMore() }
        //设置底部加载颜色
        this.mRecyclerView!!.setFooterViewColor(R.color.colorAccent, R.color.black, android.R.color.white)
        //设置底部加载文字提示
        this.mRecyclerView!!.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧")
    }

    interface IRefreshListener {
        fun onRefresh()
    }

    interface ILoadMoreListener {
        fun onLoadMore()
    }
}
