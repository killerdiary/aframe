package com.hy.album.ui

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import com.hy.album.R
import com.hy.album.adapter.ImageViewPagerAdapter
import com.hy.album.bean.AlbumInfo
import com.hy.frame.app.BaseActivity
import com.hy.frame.mvp.IBasePresenter
import com.hy.frame.widget.BadgeTextView
import com.hy.http.IMyHttpListener

/**
 * AlbumPagerActivity
 * @author HeYan
 * @time 2017/10/30 15:42
 */
class AlbumPagerActivity : BaseActivity<IBasePresenter>(), ViewPager.OnPageChangeListener {

    private var rlyBadge: RelativeLayout? = null
    private var txtBadge: BadgeTextView? = null
    private var vPager: ViewPager? = null
    private var datas: MutableList<AlbumInfo>? = null
    private var checkDatas: ArrayList<AlbumInfo> = ArrayList()
    private var adapter: ImageViewPagerAdapter? = null
    private var position: Int = 0
    private var maxSize: Int = 1
    private var isMultiple: Boolean = false
        get() = maxSize > 1

    override fun isPortrait(): Boolean = false
    override fun isTranslucentStatus(): Boolean = true
    override fun isPermissionDenied(): Boolean = false
    override fun isSingleLayout(): Boolean = true
    override fun getLayoutId(): Int = R.layout.act_album_pager

    override fun initView() {
        rlyBadge = setOnClickListener(R.id.album_pager_rlyBadge)
        txtBadge = findViewById(R.id.album_pager_txtBadge)
        vPager = findViewById(R.id.album_pager_vPager)
        vPager?.addOnPageChangeListener(this)
    }

    override fun initData() {
        if (bundle == null) {
            finish()
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
        maxSize = bundle!!.getInt(ARG_MAX_SIZE, 1)
        position = bundle!!.getInt(ARG_POSITION, 0)
        datas = bundle!!.getParcelableArrayList(ARG_DATA)
        checkDatas = bundle!!.getParcelableArrayList(ARG_CHECK_DATA)
        setHeaderLeft(R.drawable.v_back)
        setHeaderRightTxt(R.string.confirm)
        if (isMultiple) {
            title = getString(R.string.album_pager_format, position + 1, datas!!.size)
        } else {
            rlyBadge!!.visibility = View.GONE
            title = getString(R.string.album_single)
        }
        adapter = ImageViewPagerAdapter(getCurContext(), datas)
        vPager?.adapter = adapter
        if (position > 0) {
            vPager?.currentItem = position
        }
    }

    override fun onViewClick(v: View) {
        val item = datas!![vPager!!.currentItem]
        onChangeItem(item)
    }

    override fun onRightClick() {
        val intent = Intent()
        intent.putExtra(ARG_CHECK_DATA, checkDatas)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun onChangeItem(item: AlbumInfo) {
        item.isSelected = !item.isSelected
        if (item.isSelected) {
            item.flag = checkDatas.size + 1
            checkDatas.add(item)
            txtBadge!!.visibility = View.VISIBLE
            txtBadge!!.setBadgeNumber(item.flag)
        } else {
            txtBadge!!.visibility = View.GONE
            val flag = item.flag
            checkDatas.removeAt(flag - 1)
            checkDatas
                    .filter { it.flag > flag }
                    .forEach { it.flag = it.flag - 1 }
        }
        val size = checkDatas.size
        if (isMultiple)
            headerRight.visibility = if (size > 0) View.VISIBLE else View.INVISIBLE
        if (size == maxSize) {
            onRightClick()
        }
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        title = getString(R.string.album_pager_format, position + 1, datas!!.size)
        val item = datas!![position]
        if (item.isSelected) {
            txtBadge!!.visibility = View.VISIBLE
            txtBadge!!.setBadgeNumber(item.flag)
        } else {
            txtBadge!!.visibility = View.GONE
        }
    }

    override fun getRequestListener(): IMyHttpListener? = null

    override fun buildPresenter(): IBasePresenter? = null

    companion object {

        private const val ARG_MAX_SIZE = "arg_max_size"
        private const val ARG_POSITION = "arg_position"
        const val ARG_DATA = "arg_data"
        const val ARG_CHECK_DATA = "arg_check_data"

        fun newArguments(maxSize: Int, position: Int, datas: ArrayList<AlbumInfo>, checkDatas: ArrayList<AlbumInfo>): Bundle {
            val args = Bundle()
            args.putInt(ARG_MAX_SIZE, maxSize)
            args.putInt(ARG_POSITION, position)
            args.putParcelableArrayList(ARG_DATA, datas)
            args.putParcelableArrayList(ARG_CHECK_DATA, checkDatas)
            return args
        }
    }
}