package com.hy.frame.adapter


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.hy.frame.app.IBaseFragment

/**
 * Fragment类型Pager适配器
 * author HeYan
 * time 2016/3/8 13:08
 */
class FragPagerAdapter constructor(manager: FragmentManager, private val fragments: List<IBaseFragment>?, private val titles: Array<String?>? = null) : FragmentPagerAdapter(manager) {

    override fun getItem(position: Int): Fragment {
        return fragments!![position].getFragment()
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
