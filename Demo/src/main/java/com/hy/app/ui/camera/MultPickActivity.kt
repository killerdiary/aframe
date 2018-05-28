package com.hy.app.ui.camera

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.bumptech.glide.Glide
import com.hy.album.ui.AlbumActivity
import com.hy.app.R
import com.hy.app.adapter.MultPickAdapter
import com.hy.app.bean.AlbumInfo
import com.hy.app.common.BaseActivity
import com.hy.frame.adapter.IAdapterListener
import com.hy.frame.widget.recycler.GridItemDecoration

/**
 * MultPickActivity
 * @author HeYan
 * @time 2017/10/30 15:42
 */
class MultPickActivity : BaseActivity(), IAdapterListener<AlbumInfo> {

    private var rcyList: RecyclerView? = null
    private var datas: MutableList<AlbumInfo>? = null
    private var adapter: MultPickAdapter? = null
    private var maxSize: Int = 1
    private var isMultiple: Boolean = false
        get() = maxSize > 1

    override fun getLayoutId(): Int = R.layout.act_recycler

    override fun initView() {
        rcyList = findViewById(R.id.recycler_rcyList)
        rcyList?.overScrollMode = View.OVER_SCROLL_NEVER
        rcyList?.layoutManager = GridLayoutManager(getCurContext(), 3)
        val padding = resources.getDimensionPixelSize(R.dimen.padding_normal)
        rcyList?.addItemDecoration(GridItemDecoration(rcyList!!, padding, Color.TRANSPARENT).setDividerVertical(padding).setPaddingTop(padding).setPaddingLeft(padding).build())
    }

    override fun initData() {
        maxSize = bundle?.getInt(ARG_MAX_SIZE, 1) ?: 1
        if (isMultiple) {
            initHeaderBack(R.string.camera_picture_more_format)
            title = getString(R.string.camera_picture_more_format, maxSize)
        } else {
            initHeaderBack(R.string.camera_picture_single)
        }
        requestData()
    }

    private fun requestData() {
        datas = ArrayList()
        datas?.add(AlbumInfo())
        updateUI()
    }

    private fun updateUI() {
        if (adapter == null) {
            adapter = MultPickAdapter(getCurContext()!!, datas, this, Glide.with(this))
            rcyList?.adapter = adapter
        } else
            adapter?.refresh(datas)
    }

    override fun onViewClick(v: View) {}

    override fun onViewClick(v: View, item: AlbumInfo, position: Int) {
        when (v.id) {
            R.id.camera_multpick_i_vMask -> {
                if (item.thumb.isNullOrEmpty())
                    startActForResult(AlbumActivity::class.java, 1, AlbumActivity.newArguments(maxSize - datas!!.size + 1))
            }
            R.id.camera_multpick_i_vRemove -> {
                datas?.removeAt(position)
                if (datas!!.size == 0 || !datas!!.last().thumb.isNullOrEmpty())
                    datas?.add(AlbumInfo())
                updateUI()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val array = data?.getStringArrayExtra(AlbumActivity.ARG_DATA) ?: return
            for (item in array)
                datas?.add(datas!!.size - 1, AlbumInfo(null, item))
            if (datas!!.size > maxSize)
                datas?.removeAt(datas!!.size - 1)
            updateUI()
        }
    }

    companion object {

        private val ARG_MAX_SIZE = "arg_max_size"

        fun newArguments(maxSize: Int): Bundle {
            val args = Bundle()
            args.putInt(ARG_MAX_SIZE, maxSize)
            return args
        }
    }
}