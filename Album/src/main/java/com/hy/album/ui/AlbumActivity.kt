package com.hy.album.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.Process
import android.provider.MediaStore
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.hy.album.R
import com.hy.album.adapter.AlbumAdapter
import com.hy.album.bean.AlbumInfo
import com.hy.frame.adapter.IAdapterLongListener
import com.hy.frame.app.BaseActivity
import com.hy.frame.bean.MyHandler
import com.hy.frame.mvp.IBasePresenter
import com.hy.frame.util.FormatUtil
import com.hy.frame.util.PermissionUtil
import com.hy.frame.widget.recycler.GridItemDecoration
import com.hy.http.IMyHttpListener
import java.io.File

/**
 * AlbumActivity
 * @author HeYan
 * @time 2017/10/16 14:27
 */
class AlbumActivity : BaseActivity<IBasePresenter>(), IAdapterLongListener<AlbumInfo>, PermissionUtil.IRequestPermissionListener {

    private var rcyList: RecyclerView? = null
    private var datas: ArrayList<AlbumInfo>? = null
    private var checkDatas: ArrayList<AlbumInfo> = ArrayList()
    private var adapter: AlbumAdapter? = null
    private var thread: Thread? = null
    private var handler: MyHandler? = null
    private var maxSize: Int = 1
    private var isMultiple: Boolean = false
        get() = maxSize > 1

    override fun isPortrait(): Boolean = false
    override fun isSingleLayout(): Boolean = false
    override fun isTranslucentStatus(): Boolean = true
    override fun isPermissionDenied(): Boolean = false
    override fun getLayoutId(): Int = R.layout.v_recycler

    override fun initView() {
        rcyList = findViewById(R.id.recycler_rcyList)
        rcyList?.overScrollMode = View.OVER_SCROLL_NEVER
        rcyList?.layoutManager = GridLayoutManager(getCurContext(), 3)
        val padding = resources.getDimensionPixelSize(R.dimen.padding_normal)
        rcyList?.addItemDecoration(GridItemDecoration(rcyList!!, padding, Color.TRANSPARENT).setDividerVertical(padding).setPaddingTop(padding).setPaddingLeft(padding).build())
    }

    override fun initData() {
        maxSize = bundle?.getInt(ARG_MAX_SIZE, 1) ?: 1
        setHeaderLeft(R.drawable.v_back)
        setTitle(R.string.album_single)
        setHeaderRightTxt(R.string.confirm)
        if (isMultiple) {
            title = getString(R.string.album_more_format, maxSize)
            headerRight.visibility = View.INVISIBLE
        }
        showLoading()
        handler = MyHandler(this, object : MyHandler.HandlerListener {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    WHAT_PERMISSION_GRANTED -> {
                        requestData()
                    }
                    WHAT_PERMISSION_DENIED -> {
                        finish()
                    }
                    WHAT_FETCH_STARTED -> {
                        showLoading()
                    }
                    WHAT_FETCH_COMPLETED -> {
                        if (datas == null)
                            datas = ArrayList()
                        datas?.clear()
                        checkDatas.clear()
                        if (msg.obj != null && msg.obj is MutableList<*>) {
                            val temp = msg.obj as MutableList<*>
                            for (item in temp) {
                                if (item is AlbumInfo)
                                    datas?.add(item)
                            }
                        }
                        updateUI()
                    }
                    WHAT_ERROR -> {
                        showNoData("图片加载错误")
                    }
                }
            }
        })
    }

    private fun requestData() {
        abortLoading()
        val runnable = AlbumLoaderRunnable(handler!!)
        thread = Thread(runnable)
        thread?.start()
    }

    private fun abortLoading() {
        if (thread != null) {
            if (thread!!.isAlive) {
                thread!!.interrupt()
                try {
                    thread!!.join()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun updateUI() {
        if (FormatUtil.isEmpty(datas)) {
            showNoData("没有搜索到图片")
            return
        }
        showCView()
        if (adapter == null) {
            adapter = AlbumAdapter(getCurContext(), datas, this)
            rcyList?.adapter = adapter
        } else
            adapter?.refresh(datas)
    }

    override fun onViewClick(v: View) {}

    override fun onViewClick(v: View, item: AlbumInfo, position: Int) {
        when (v.id) {
            R.id.album_i_vMask -> {
                //Preview
                startActForResult(AlbumPagerActivity::class.java, 1, AlbumPagerActivity.newArguments(maxSize, position, datas!!, checkDatas))
            }
            R.id.album_i_vCheck -> {
                onChangeItem(item)
            }
        }
    }

    override fun onViewLongClick(v: View, item: AlbumInfo, position: Int) {
        onChangeItem(item)
    }

    private fun onChangeItem(item: AlbumInfo) {
        item.isSelected = !item.isSelected
        if (item.isSelected) {
            item.flag = checkDatas.size + 1
            checkDatas.add(item)
        } else {
            val flag = item.flag
            checkDatas.removeAt(flag - 1)
            checkDatas
                    .filter { it.flag > flag }
                    .forEach { it.flag = it.flag - 1 }
        }
        val size = checkDatas.size
        if (isMultiple)
            headerRight.visibility = if (size > 0) View.VISIBLE else View.GONE
        updateUI()
        if (size == maxSize) {
            onRightClick()
        }
    }

    override fun onRightClick() {
        val intent = Intent()
        val beans = ArrayList<String>()
        checkDatas.mapTo(beans) { it.thumb!! }
        val strs = beans.toTypedArray()
        intent.putExtra(ARG_DATA, strs)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val array = data?.getParcelableArrayListExtra<AlbumInfo>(AlbumPagerActivity.ARG_CHECK_DATA)
                    ?: return
            checkDatas.clear()
            checkDatas.addAll(array)
            for (item in datas!!) {
                item.isSelected = false
            }
            for ((p, i) in checkDatas.withIndex()) {
                val item = datas!![i.id]
                item.isSelected = true
                item.flag = p + 1
            }
            adapter?.refresh()
        }
    }

    override fun onResume() {
        super.onResume()
        if (datas == null && PermissionUtil.requesStoragetPermission(this)) {
            requestData()
        }
    }

    override fun onPause() {
        super.onPause()
        abortLoading()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtil.onRequestPermissionsResult(getCurContext(), requestCode, permissions, grantResults, this)
    }

    override fun onRequestPermissionSuccess(requestCode: Int) {
        requestData()
    }

    override fun onRequestPermissionFail(requestCode: Int) {
        finish()
    }

    private val projection = arrayOf(MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE, MediaStore.Images.Media.DATE_MODIFIED)

    private inner class AlbumLoaderRunnable(val handler: Handler) : Runnable {
        override fun run() {
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
            var msg: Message = handler.obtainMessage()
            msg.what = WHAT_FETCH_STARTED
            msg.sendToTarget()
            if (Thread.interrupted()) {
                return
            }
            val cursor = applicationContext.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.Media.DATE_ADDED)
            //val cursor = applicationContext.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.Media.DATE_ADDED)
            if (cursor == null) {
                msg = handler.obtainMessage()
                msg.what = WHAT_ERROR
                msg.sendToTarget()
                return
            }
            val temp: MutableList<AlbumInfo> = ArrayList(cursor.count)
            //val albumSet = HashSet<String>()
            var file: File
            if (cursor.moveToLast()) {
                do {
                    if (Thread.interrupted()) {
                        return
                    }
                    val name = cursor.getString(cursor.getColumnIndex(projection[0]))
                    val thumb = cursor.getString(cursor.getColumnIndex(projection[1]))
                    val size = cursor.getLong(cursor.getColumnIndex(projection[2]))
                    val time = cursor.getLong(cursor.getColumnIndex(projection[3]))
                    file = File(thumb)
                    //if (file.exists() && !albumSet.contains(album)) {
                    if (file.exists()) {
                        temp.add(AlbumInfo(temp.size, name, thumb, size, time))
                        //albumSet.add(album)
                    }

                } while (cursor.moveToPrevious())
            }
            cursor.close()
            msg = handler.obtainMessage()
            msg.what = WHAT_FETCH_COMPLETED
            msg.obj = temp
            msg.sendToTarget()
            Thread.interrupted()
        }
    }

    override fun getRequestListener(): IMyHttpListener? = null

    override fun buildPresenter(): IBasePresenter? = null

    companion object {
        private const val WHAT_PERMISSION_GRANTED = 1
        private const val WHAT_PERMISSION_DENIED = 2
        private const val WHAT_FETCH_STARTED = 3
        private const val WHAT_FETCH_COMPLETED = 4
        private const val WHAT_ERROR = -1
        private const val ARG_MAX_SIZE = "arg_max_size"
        const val ARG_DATA = "arg_data"

        fun newArguments(maxSize: Int): Bundle {
            val args = Bundle()
            args.putInt(ARG_MAX_SIZE, maxSize)
            return args
        }
    }
}