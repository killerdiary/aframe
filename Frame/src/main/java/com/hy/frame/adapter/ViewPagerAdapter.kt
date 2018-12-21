package com.hy.frame.adapter

import android.os.Parcelable
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup

/**
 * View类型Pager适配器
 * @author HeYan
 * @time 2014年7月26日 上午10:27:41
 */
class ViewPagerAdapter(private var views: MutableList<View>?, private var titles: Array<String?>? = null) : PagerAdapter() {

    override fun getCount(): Int {
        return views?.size ?: 0
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        container.addView(views!![position])
        return views!![position]
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(views!![position])
    }

    // 判断是否由对象生成界面
    override fun isViewFromObject(v: View, obj: Any): Boolean {
        return v === obj
    }

    override fun saveState(): Parcelable? {
        return null
    }

    override fun getPageTitle(position: Int): CharSequence? {
        if (titles != null)
            return titles!![position]
        return null
    }

    fun refresh(views: MutableList<View>?, titles: Array<String?>? = null) {
        this.views = views
        this.titles = titles
        this.notifyDataSetChanged()
    }

}
