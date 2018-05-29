package com.hy.app.ui.list

import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.hy.app.R
import com.hy.app.adapter.RecyclerAdapter
import com.hy.app.common.BaseActivity
import com.hy.app.widget.TestRecyclerView
import com.hy.frame.adapter.IAdapterListener
import com.hy.frame.bean.MenuInfo
import com.hy.frame.widget.recycler.LinearItemDecoration
import com.hy.frame.widget.recycler.SwipeRecyclerView
import java.util.ArrayList

/**
 * TestActivity
 * @author HeYan
 * @time 2017/9/25 12:00
 */
class TestActivity : BaseActivity(), IAdapterListener<MenuInfo> {

    private var rcyList: TestRecyclerView? = null
    private var datas: MutableList<MenuInfo>? = null
    private var adapter: RecyclerAdapter? = null

    override fun getLayoutId(): Int = R.layout.act_list_refresh

    override fun initView() {
        rcyList = findViewById(R.id.recycler_rcyList)
        rcyList?.layoutManager = LinearLayoutManager(getCurContext())
        rcyList?.addItemDecoration(LinearItemDecoration(rcyList!!, 1, resources.getColor(R.color.divider_gray)))
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



}