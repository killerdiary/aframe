package com.hy.app.ui.list


import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.hy.app.R
import com.hy.app.adapter.SwipeAdapter
import com.hy.app.common.BaseActivity
import com.hy.frame.adapter.IAdapterListener
import com.hy.frame.util.FormatUtil
import com.hy.frame.util.HyUtil
import com.hy.frame.widget.SwipeMenuView
import com.hy.frame.widget.recycler.LinearItemDecoration
import java.util.*

/**
 * SwipeListActivity
 *
 * @author HeYan
 * @time 2017/5/10 17:13
 */
class SwipeListActivity : BaseActivity(), IAdapterListener<String> {
    private var rcyList: RecyclerView? = null
    private var adapter: SwipeAdapter? = null
    private var datas: MutableList<String>? = null
    private var handler: Handler? = null

    override fun getLayoutId(): Int {
        return R.layout.act_recycler
    }

    override fun initView() {
        initHeaderBack(R.string.list_swipe_custom)
        rcyList = findViewById(R.id.recycler_rcyList)
        rcyList!!.layoutManager = LinearLayoutManager(getCurContext())
        rcyList!!.addItemDecoration(LinearItemDecoration(rcyList!!, 1, resources.getColor(R.color.divider_gray)))
    }

    override fun initData() {
        showLoading()
        requestData()
    }


    private fun requestData() {
        datas = ArrayList()
        for (i in 1..19) {
            datas!!.add("测试" + i)
        }
        if (handler == null)
            handler = Handler()
        handler!!.postDelayed({ updateUI() }, 1000)
    }

    private fun updateUI() {
        if (FormatUtil.isEmpty(datas)) {
            showNoData()
            return
        }
        showCView()
        if (adapter == null) {
            adapter = SwipeAdapter(getCurContext()!!, datas, this)
            rcyList!!.adapter = adapter
        } else {
            adapter!!.refresh(datas)
        }
    }

    override fun onViewClick(v: View) {}


    override fun onViewClick(v: View, item: String, position: Int) {
        when (v.id) {
            R.id.swipe_i_btnDelete -> {
                val menuView = v.parent as SwipeMenuView
                if (menuView.isLeftSwipe()) {
                    //menuView.smoothClose();
                    menuView.quickClose()
                    //return;
                }
                datas!!.removeAt(position)
                adapter!!.notifyItemRemoved(position)
            }
        }//adapter.refresh(datas);
    }
}
