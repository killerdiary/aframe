package com.hy.album.adapter

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.hy.album.R
import com.hy.album.bean.AlbumInfo

/**
 * ViewPagerAdapter
 * @author HeYan
 * @time 2017/10/31 9:34
 */
class ImageViewPagerAdapter(private val context: Context, private var datas: MutableList<AlbumInfo>?) : PagerAdapter() {

    override fun getCount(): Int {
        return datas?.size ?: 0
    }

    // 判断是否由对象生成界面
    override fun isViewFromObject(v: View, obj: Any): Boolean {
        return v === obj
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val v = View.inflate(context, R.layout.item_album_image, null)
        val h = ItemHolder(v)
        val item = datas!![position]
        Glide.with(context).load(item.thumb).into(h.imgPic)
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
        val imgPic: PhotoView = v.findViewById(R.id.album_image_i_imgPic)!!
    }
}