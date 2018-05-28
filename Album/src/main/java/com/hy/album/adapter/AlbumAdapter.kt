package com.hy.album.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.hy.album.R
import com.hy.frame.adapter.IAdapterListener
import com.hy.album.bean.AlbumInfo
import com.hy.frame.util.HyUtil
import com.hy.frame.widget.BadgeTextView
import com.hy.frame.widget.recycler.BaseHolder
import com.hy.frame.widget.recycler.BaseRecyclerAdapter

/**
 * AlbumAdapter
 * @author HeYan
 * @time 2017/10/16 15:51
 */
class AlbumAdapter(context: Context, datas: MutableList<AlbumInfo>?, listener: IAdapterListener<AlbumInfo>) : BaseRecyclerAdapter<AlbumInfo>(context, datas, listener) {

    private val itemWidth = (context.resources.displayMetrics.widthPixels - context.resources.getDimensionPixelSize(R.dimen.padding_normal) * 4) / 3

    override fun createView(parent: ViewGroup, viewType: Int): BaseHolder {
        return ItemHolder(inflate(R.layout.item_album))
    }

    override fun bindViewData(holder: BaseHolder, position: Int) {
        holder as ItemHolder
        val item = getItem(position)
        Glide.with(context).load(item.thumb).into(holder.imgPic)
        holder.vMask.isSelected = item.isSelected
        if (item.isSelected) {
            holder.txtBadge.visibility = View.VISIBLE
            holder.txtBadge.text = item.flag.toString()
        } else {
            holder.txtBadge.visibility = View.GONE
        }
    }

    internal inner class ItemHolder(v: View) : BaseLongClickHolder(v) {
        val imgPic: ImageView = findViewById(R.id.album_i_imgPic)!!
        val txtBadge: BadgeTextView = findViewById(R.id.album_i_txtBadge)!!
        val vMask: View = setOnClickListener(R.id.album_i_vMask)!!

        init {
            setOnClickListener<View>(R.id.album_i_vCheck)
            setOnLongClickListener(vMask)
            imgPic.layoutParams.width = itemWidth
            imgPic.layoutParams.height = itemWidth
            vMask.layoutParams.width = itemWidth
            vMask.layoutParams.height = itemWidth
        }
    }
}