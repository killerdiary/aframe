package com.hy.app.adapter

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import com.github.chrisbanes.photoview.PhotoView
import com.hy.app.R
import com.hy.app.bean.AlbumInfo
import com.hy.app.util.ComUtil

/**
 * ViewPagerAdapter
 * @author HeYan
 * @time 2017/10/31 9:34
 */
class ViewPagerAdapter(private val context: Context, private var datas: MutableList<AlbumInfo>?, private val requestManager: RequestManager?) : PagerAdapter() {

    override fun getCount(): Int {
        return datas?.size ?: 0
    }

    // 判断是否由对象生成界面
    override fun isViewFromObject(v: View, obj: Any): Boolean {
        return v === obj
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val v = View.inflate(context, R.layout.item_camera_image, null)
        val h = ItemHolder(v)
        val item = datas!![position]
        ComUtil.displayImage(requestManager, h.imgShow, item.thumb)
        container.addView(v)
        return v
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

    fun refresh(datas: MutableList<AlbumInfo>?) {
        this.datas = datas
        this.notifyDataSetChanged()
    }

    inner class ItemHolder(v: View) {
        val imgShow: PhotoView = v.findViewById(R.id.camera_image_i_imgShow)!!
    }
}