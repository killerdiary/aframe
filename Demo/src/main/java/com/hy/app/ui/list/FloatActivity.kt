package com.hy.app.ui.list

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hy.app.R
import com.hy.app.adapter.FloatAdapter
import com.hy.app.common.BaseActivity
import com.hy.frame.adapter.IAdapterListener
import com.hy.frame.util.FormatUtil
import com.hy.frame.widget.recycler.BaseRecyclerAdapter
import com.hy.frame.widget.recycler.LinearItemDecoration
import java.util.*

/**
 * FloatActivity
 * @author HeYan
 * @time 2017/10/27 10:51
 */
class FloatActivity : BaseActivity(), IAdapterListener<String> {
    private var rcyList: RecyclerView? = null
    private var adapter: FloatAdapter? = null
    private var datas: MutableList<String>? = null
    private var handler: Handler? = null
    private var vFloat: View? = null
    private var txtFloatTitle: TextView? = null

    override fun getLayoutId(): Int = R.layout.act_recycler

    override fun initView() {
        rcyList = findViewById(R.id.recycler_rcyList)
        rcyList!!.layoutManager = LinearLayoutManager(getCurContext())
        vFloat = View.inflate(getCurContext()!!, R.layout.item_list_simple, null)
        txtFloatTitle = findViewById(R.id.list_simple_i_txtTitle, vFloat)
        txtFloatTitle?.setBackgroundColor(Color.LTGRAY)
        (mainView as ViewGroup).addView(vFloat, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
    }

    override fun initData() {
        initHeaderBack(R.string.list_float)
        showLoading()
        requestData()
    }

    private fun requestData() {
        datas = ArrayList()
        for (i in 1..41) {
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
            adapter = FloatAdapter(getCurContext()!!, datas, this)
            rcyList?.adapter = adapter
            rcyList?.addItemDecoration(FloatItemDecoration(rcyList!!, 1, resources.getColor(R.color.divider_gray)))
        } else {
            adapter?.refresh(datas)
        }
    }

    private fun getLastItem(position: Int): String {
        var size: Int = position / 3
        if (position % 3 != 0)
            size++
        if (size > 0)
            return datas!![(size - 1) * 3]
        return datas!![size * 3]
    }

    override fun onViewClick(v: View) {}


    override fun onViewClick(v: View, item: String, position: Int) {

    }

    inner class FloatItemDecoration : LinearItemDecoration {

        private var initFloatTop: Int = 0

        constructor(rcyList: RecyclerView, divider: Int, color: Int) : super(rcyList, divider, color)

        constructor(rcyList: RecyclerView, divider: Int, drawable: Drawable) : super(rcyList, divider, drawable)

        override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            super.onDrawOver(c, parent, state)
            if (initFloatTop == 0)
                initFloatTop = vFloat!!.top
            val child = parent.getChildAt(0)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val viewType = adapter?.getItemViewType(params.viewAdapterPosition) ?: 0
            var top = 0
            if (viewType == FloatAdapter.TYPE_GROUP) {
                txtFloatTitle?.text = findViewById<TextView>(R.id.list_simple_i_txtTitle, child)?.text
                vFloat!!.top = top
            } else if (viewType == BaseRecyclerAdapter.TYPE_ITEM) {
                val viewType1 = adapter?.getItemViewType(params.viewAdapterPosition + 1) ?: 0
                if (viewType1 == FloatAdapter.TYPE_GROUP)
                    top = child.top
                vFloat!!.top = top
                txtFloatTitle?.text = getLastItem(params.viewAdapterPosition)
            }
        }
    }
}