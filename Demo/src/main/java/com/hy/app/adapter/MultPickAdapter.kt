package com.hy.app.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.RequestManager
import com.hy.app.R
import com.hy.app.bean.AlbumInfo
import com.hy.app.util.ComUtil
import com.hy.frame.adapter.IAdapterListener
import com.hy.frame.widget.recycler.BaseHolder
import com.hy.frame.widget.recycler.BaseRecyclerAdapter

/**
 * AlbumAdapter
 * @author HeYan
 * @time 2017/10/16 15:51
 */
class MultPickAdapter(context: Context, datas: MutableList<AlbumInfo>?, listener: IAdapterListener<AlbumInfo>, private val requestManager: RequestManager) : BaseRecyclerAdapter<AlbumInfo>(context, datas, listener) {

    private val itemWidth = (context.resources.displayMetrics.widthPixels - context.resources.getDimensionPixelSize(R.dimen.padding_normal) * 4) / 3

    override fun createView(parent: ViewGroup, viewType: Int): BaseHolder {
        return ItemHolder(inflate(R.layout.item_camera_multpick))
    }

    override fun bindViewData(holder: BaseHolder, position: Int) {
        holder as ItemHolder
        val item = getItem(position)
        if (item.thumb.isNullOrEmpty()) {
            holder.imgRemove.visibility = View.GONE
            holder.imgPic.setImageResource(R.mipmap.ic_add_member)
        } else {
            holder.imgRemove.visibility = View.VISIBLE
            ComUtil.displayImage(requestManager, holder.imgPic, item.thumb)
        }
    }

    internal inner class ItemHolder(v: View) : BaseClickHolder(v) {
        val imgPic: ImageView = findViewById(R.id.camera_multpick_i_imgPic)!!
        val imgRemove: ImageView = findViewById(R.id.camera_multpick_i_imgRemove)!!
        private val vMask: View = setOnClickListener(R.id.camera_multpick_i_vMask)!!

        init {
            setOnClickListener<View>(R.id.camera_multpick_i_vRemove)
            imgPic.layoutParams.width = itemWidth
            imgPic.layoutParams.height = itemWidth
            vMask.layoutParams.width = itemWidth
            vMask.layoutParams.height = itemWidth
        }
    }
}