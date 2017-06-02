package com.hy.frame.widget.recycler

import android.view.View

@Deprecated("")
interface IHeaderViewListner {
    fun bindHearderData(holder: HeaderHolder, position: Int)

    fun createHeaderView(v: View): HeaderHolder
}