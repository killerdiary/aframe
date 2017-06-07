package com.hy.frame.adapter

import android.os.Parcelable
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup

/**
 * List<View>类型
 * @author HeYan
 * @time 2014年7月26日 上午10:27:41
</View> */
class ViewPagerAdapter(// 界面列表
        private var views: List<View>?) : PagerAdapter() {

    // 获得当前界面数
    override fun getCount(): Int {
        return if (views == null) 0 else views!!.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        //MyLog.i(getClass(), " position:" + position);
        // container.removeAllViews();
        container.addView(views!![position])
        return views!![position]
    }

    // 销毁arg1位置的界面
    override fun destroyItem(v: View?, position: Int, obj: Any?) {
        (v as ViewPager).removeView(views!![position])
    }

    // 判断是否由对象生成界面
    override fun isViewFromObject(v: View, obj: Any): Boolean {
        return v === obj
    }

    override fun saveState(): Parcelable? {
        return null
    }

    override fun startUpdate(container: ViewGroup) {

    }

    override fun finishUpdate(arg0: View?) {

    }

    fun refresh(views: List<View>) {
        this.views = views
        this.notifyDataSetChanged()
    }

}
