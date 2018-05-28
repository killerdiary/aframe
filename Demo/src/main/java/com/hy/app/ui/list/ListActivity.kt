package com.hy.app.ui.list

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.hy.app.R
import com.hy.app.adapter.MenuAdapter
import com.hy.app.common.BaseActivity
import com.hy.frame.adapter.IAdapterListener
import com.hy.frame.bean.MenuInfo
import java.util.*

/**
 * ListActivity
 * @author HeYan
 * @time 2017/5/10 17:47
 */
class ListActivity : BaseActivity(), IAdapterListener<MenuInfo> {
    private var rcyList: RecyclerView? = null
    private var datas: MutableList<MenuInfo>? = null
    private var adapter: MenuAdapter? = null

    override fun getLayoutId(): Int = R.layout.act_recycler

    override fun initView() {
        rcyList = findViewById(R.id.recycler_rcyList)
        rcyList!!.layoutManager = GridLayoutManager(getCurContext(), 3)
    }

    override fun initData() {
        initHeaderBack(R.string.list)
        requestData()
    }

    private fun requestData() {
        datas = ArrayList()
        datas!!.add(MenuInfo(R.string.list_swipe_custom))
        datas!!.add(MenuInfo(R.string.list_swipe_recycler))
        updateUI()
    }

    private fun updateUI() {
        if (adapter == null) {
            adapter = MenuAdapter(getCurContext()!!, datas, this)
            rcyList!!.adapter = adapter
        } else
            adapter!!.refresh(datas)
    }

    override fun onViewClick(v: View) {}

    override fun onViewClick(v: View, item: MenuInfo, position: Int) {
        when (item.title) {
            R.string.list_swipe_custom -> startAct(SwipeListActivity::class.java, null, null)
            R.string.list_swipe_recycler -> startAct(SwipeRecyclerActivity::class.java, null, null)
        }
    }
}
