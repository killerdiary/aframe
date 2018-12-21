package com.hy.frame.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.hy.frame.ui.IBaseFragment

/**
 * Fragment类型Pager适配器
 * author HeYan
 * time 2016/3/8 13:08
 */
class FragPagerAdapter constructor(manager: FragmentManager, private var fragments: MutableList<IBaseFragment>?, private var titles: Array<String?>? = null) : FragmentPagerAdapter(manager) {

    override fun getItem(position: Int): Fragment {
        return fragments!![position].getFragment()
    }

    override fun getCount(): Int {
        return fragments?.size ?: 0
    }

    /**
     * 不建议更改fragments的数量
     */
    fun refresh(fragments: MutableList<IBaseFragment>?, titles: Array<String?>? = null) {
        this.fragments = fragments
        this.titles = titles
        this.notifyDataSetChanged()
    }

    override fun getPageTitle(position: Int): CharSequence? {
        if (titles != null)
            return titles!![position]
        return null
    }
}
