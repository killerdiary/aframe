package com.hy.frame.widget.recycler

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView

import com.hy.frame.R

/**
 * LoadMoreHolder

 * @author HeYan LoadMoreHolder
 * *
 * @time 2016/5/27 16:35
 */
@Deprecated("")
class LoadMoreHolder(v: View) : RecyclerView.ViewHolder(v) {
    internal var proFoot: ProgressBar
    internal var txtFootHint: TextView

    init {
        if (v.layoutParams == null) {
            v.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        proFoot = v.findViewById(R.id.footer_proFoot) as ProgressBar
        txtFootHint = v.findViewById(R.id.footer_txtFootHint) as TextView
    }

    fun onChangeState(state: Int) {
        when (state) {
            STATE_LOAD_ING -> loading()
            STATE_LOAD_COMPLETE -> loadComplete()
            else -> prepare()
        }
    }


    private fun prepare() {
        proFoot.visibility = View.GONE
        txtFootHint.setText(R.string.load_more)
    }

    private fun loading() {
        proFoot.visibility = View.VISIBLE
        txtFootHint.setText(R.string.loading)
    }

    private fun loadComplete() {
        proFoot.visibility = View.GONE
        txtFootHint.setText(R.string.load_complete)
    }

    companion object {
        val STATE_LOAD_PREPARE = 0
        val STATE_LOAD_ING = 1
        val STATE_LOAD_COMPLETE = 2
    }
}
