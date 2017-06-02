package com.hy.frame.widget.recycler

import android.content.Context
import android.support.annotation.LayoutRes
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout

import com.hy.frame.adapter.BaseRecyclerAdapter

/**
 * RecyclerView

 * @author HeYan
 * *
 * @time 2016/5/28 9:19
 */
class RecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {
    var recyclerView: android.support.v7.widget.RecyclerView? = null
    private var header: RecyclerViewHeader? = null
    private var hasHeader: Boolean = false

    init {
        init(context)
    }

    //private int testCount = 0;

    private fun init(context: Context) {
        recyclerView = android.support.v7.widget.RecyclerView(context)
        recyclerView!!.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        recyclerView!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        addView(recyclerView)
    }

    /**
     * 只能调用一次

     * @param layoutId
     */
    //@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    fun setHeaderResId(@LayoutRes layoutId: Int) {
        val v = View.inflate(context, layoutId, null)
        val rlp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        v.layoutParams = rlp
        setHeaderView(v)
    }

    /**
     * 只能调用一次

     * @param v
     */
    fun setHeaderView(v: View) {
        hasHeader = true
        header = RecyclerViewHeader(context)
        header!!.addView(v)
        addView(header, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT))
        header!!.attachTo(recyclerView!!)
    }

    fun setLayoutManager(manager: android.support.v7.widget.RecyclerView.LayoutManager) {
        recyclerView!!.layoutManager = manager
    }

    fun setAdapter(adapter: BaseRecyclerAdapter<*>) {
        recyclerView!!.adapter = adapter
    }
}
