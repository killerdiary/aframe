package com.hy.app.ui.list

import android.view.View
import com.hy.app.R
import com.hy.app.adapter.RecyclerAdapter
import com.hy.app.common.BaseActivity
import com.hy.frame.adapter.IAdapterListener
import com.hy.frame.bean.MenuInfo
import com.hy.frame.widget.recycler.LinearItemDecoration
import com.hy.frame.widget.recycler.SwipeRecyclerView
import java.util.*

/**
 * RecyclerActivity
 * @author HeYan
 * @time 2017/9/25 12:00
 */
class RecyclerActivity : BaseActivity(), IAdapterListener<MenuInfo>, SwipeRecyclerView.ILoadMoreListener, SwipeRecyclerView.IRefreshListener {

    private var rcyList: SwipeRecyclerView? = null
    private var datas: MutableList<MenuInfo>? = null
    private var adapter: RecyclerAdapter? = null

    override fun getLayoutId(): Int {
        return R.layout.act_recycler_refresh
    }

    override fun initView() {
        rcyList = findViewById(R.id.recycler_rcyList)
        //rcyList?.layoutManager = LinearLayoutManager(context)
        val padding = resources.getDimensionPixelSize(R.dimen.margin_normal)
        rcyList?.addItemDecoration(LinearItemDecoration(rcyList!!.getRecyclerView(), padding, resources.getColor(R.color.translucence)).setDividerVertical(padding).setPaddingTop(padding).setPaddingLeft(padding).setPaddingRight(padding).setPaddingBottom(padding).build())
        rcyList?.setBackgroundResource(R.color.yellow)
        rcyList?.setLoadMoreListener(this)
        rcyList?.setRefreshListener(this)
        rcyList?.refreshEnabled = true
        rcyList?.loadMoreEnabled = true
    }

    override fun initData() {
        initHeaderBack(R.string.list)
        requestData()
    }

    private fun requestData() {
        datas = ArrayList()
        for (item in 0 until 15) {
            datas?.add(MenuInfo(R.string.list_recycler))
        }
        updateUI()
    }

    private fun updateUI() {
        if (adapter == null) {
            adapter = RecyclerAdapter(getCurContext()!!, datas!!, this)
            rcyList?.adapter = adapter
        } else
            adapter?.refresh(datas)
    }

    override fun onViewClick(v: View) {}
    private var precent = 0f
    override fun onViewClick(v: View, item: MenuInfo, position: Int) {
//        if (precent < 1)
//            precent += 0.1f
//        if (precent >= 1f) {
//            precent = 0f
//            refreshView?.clear()
//        } else {
//            refreshView?.updateComleteState(precent)
//        }
    }

    override fun onRefresh() {
        rcyList?.postDelayed({
            rcyList?.refreshComplete()
        }, 2000L)
    }

    private var isAgain = false
    override fun onLoadMore() {
        rcyList?.postDelayed({
            if (datas != null)
                when {
                    datas!!.size <= 15 && !isAgain -> {
                        isAgain = true
                        rcyList?.loadMoreError()
                    }
                    datas!!.size <= 60 -> {
                        for (item in 0 until 15) {
                            datas?.add(MenuInfo(R.string.list_recycler))
                        }
                        rcyList?.loadMoreComplete()
                        adapter?.refresh()
                    }
                    else -> {
                        rcyList?.closeLoadMore()
                    }
                }
        }, 3000L)
    }


}
