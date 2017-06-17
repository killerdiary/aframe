package com.hy.frame.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

import com.hy.frame.common.BaseFragment

/**
 * 自定义主页Pager适配器
 * author HeYan
 * time 2016/3/8 13:08
 */
class FragPagerAdapter constructor(manager: FragmentManager, private val fragments: List<BaseFragment>?, private val titles: Array<String?>? = null) : FragmentPagerAdapter(manager) {

    override fun getItem(arg0: Int): Fragment {
        return fragments!![arg0]
    }

    override fun getCount(): Int {
        return fragments?.size ?: 0
    }

    override fun getPageTitle(position: Int): CharSequence? {
        if (titles != null)
            return titles[position]
        return null
    }
}
