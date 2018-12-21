package com.hy.demo2.ui

import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.google.gson.JsonParser
import com.hy.demo2.R
import com.hy.demo2.adapter.MenuAdapter
import com.hy.demo2.app.BaseActivity
import com.hy.demo2.bean.ArgumentInfo
import com.hy.frame.adapter.IAdapterListener
import com.hy.frame.bean.MenuInfo
import com.hy.frame.util.FormatUtil
import com.hy.frame.util.JsonUtil
import com.hy.frame.util.MenuUtil
import com.hy.frame.widget.recycler.GridItemDecoration

/**
 * 菜单
 * @author HeYan
 * @time 2017/9/11 16:28
 */
open class MenuActivity : BaseActivity(), IAdapterListener<MenuInfo> {
    private var rcyList: RecyclerView? = null
    private var datas: MutableList<MenuInfo>? = null
    private var adapter: MenuAdapter? = null
    private var xmlId: Int = 0

    override fun getLayoutId(): Int = R.layout.v_recycler

    override fun initView() {
        rcyList = findViewById(R.id.recycler_rcyList)
        rcyList?.layoutManager = GridLayoutManager(getCurContext(), 3)
        val padding = resources.getDimensionPixelSize(R.dimen.margin_normal)
        rcyList?.addItemDecoration(GridItemDecoration(rcyList!!, padding, Color.TRANSPARENT).setDividerVertical(padding).setPaddingTop(padding).setPaddingLeft(padding).setPaddingRight(padding).setPaddingBottom(padding).build())
    }

    override fun initData() {
        xmlId = bundle?.getInt(ARG_XMLID) ?: 0
        if (xmlId <= 0) {
            finish()
            return
        }
        val titleId = bundle?.getInt(ARG_TITLEID) ?: 0
        initHeaderBack(titleId)
        requestData()
    }

    private fun requestData() {
        datas = MenuUtil[getCurContext(), xmlId]
        updateUI()
    }

    private fun updateUI() {
        if (adapter == null) {
            adapter = MenuAdapter(getCurContext(), datas, this)
            rcyList?.adapter = adapter
        } else
            adapter?.refresh(datas)
    }

    override fun onViewClick(v: View) {}

    override fun onKeyDpadEnter(): Boolean {
        var position = 0
        for ((index, item) in datas!!.withIndex()) {
            if (item.isSelected) {
                position = index
                break
            }
        }
        onViewClick(rcyList!!, datas!![position], position)
        return true
    }

    override fun onViewClick(v: View, item: MenuInfo, position: Int) {
        val clsStr = item.getValue(MenuUtil.KEY_CLS)
        val menuStr = item.getValue(MenuUtil.KEY_MENU)
        if (!menuStr.isNullOrEmpty()) {
            val xmlId = getXmlId(menuStr!!)
            startAct(MenuActivity::class.java, MenuActivity.newArguments(xmlId, item.title))
            return
        }
        val args = item.getValue("args")
        val bundle = Bundle()
        if (!args.isNullOrEmpty()) {
            val json = JsonParser().parse(args)
            val datas = JsonUtil.getListFromJson(json, ArgumentInfo::class.java)
            if (FormatUtil.isNoEmpty(datas)) {
                for (i in datas!!) {
                    when (i.type) {
                        "Int" -> bundle.putInt(i.key, i.value!!.toInt())
                        "Long" -> bundle.putLong(i.key, i.value!!.toLong())
                        "Boolean" -> bundle.putBoolean(i.key, i.value!!.toBoolean())
                        else -> bundle.putString(i.key, i.value)
                    }
                }
            }
        }
        if (!clsStr.isNullOrEmpty()) {
            try {
                val cls = Class.forName(clsStr!!)
                startAct(cls, bundle)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onKeyDpad(keyCode: Int): Boolean {
        val size = adapter?.getDataCount() ?: 0
        if (size == 0) return false
        if (selectId == View.NO_ID) {
            selectId = 0
            onfocusChange(selectId)
            return true
        }

        return super.onKeyDpad(keyCode)
    }

    override fun onKeyDpadLeft(): Boolean {
        val size = adapter?.getDataCount() ?: 0
        if (size <= 1) return false
        val spanCount = 3
        val position = selectId + 1
        val number = position % spanCount
        if (number == 1) {
            //最左侧
            //是左翻页
        } else if (position > 0) {
            selectId--
            onfocusChange(selectId)
            return true
        }
        return false
    }

    override fun onKeyDpadUp(): Boolean {
        val size = adapter?.getDataCount() ?: 0
        if (size <= 1) return false
        val spanCount = 3
        val position = selectId + 1
        val number = position % spanCount
        if (position <= spanCount) {
            //最顶部
        } else {
            selectId -= spanCount
            onfocusChange(selectId)
            return true
        }
        return false
    }

    override fun onKeyDpadRight(): Boolean {
        val size = adapter?.getDataCount() ?: 0
        if (size <= 1) return false
        val spanCount = 3
        val position = selectId + 1
        val number = position % spanCount
        if (number == 0 || position == size) {
            //最右侧
            //是否翻页
        } else if (number < spanCount) {
            selectId++
            onfocusChange(selectId)
            return true
        }
        return false
    }

    override fun onKeyDpadDown(): Boolean {
        val size = adapter?.getDataCount() ?: 0
        if (size <= 1) return false
        val spanCount = 3
        val position = selectId + 1
        val number1 = size % spanCount
        val number2 = position % spanCount
        var line1 = size / spanCount
        var line2 = position / spanCount
        if (number1 > 0)
            line1++
        if (number2 > 0)
            line2++
        if (line1 == line2) {
            //最后一排
        } else if (line2 < line1) {
            selectId += spanCount
            if (selectId >= size) {
                selectId = size - 1
            }
            onfocusChange(selectId)
            return true
        }
        return false
    }

    override fun onfocusChange(id: Int) {
        for ((index, item) in datas!!.withIndex()) {
            item.isSelected = id == index
        }
        adapter?.refresh()
    }

    private fun getXmlId(name: String): Int {
        return resources.getIdentifier(name, "xml", packageName)
    }

    companion object {
        private val ARG_XMLID = "arg_xmlid"
        private val ARG_TITLEID = "arg_titleid"

        fun newArguments(xmlId: Int, titleId: Int): Bundle {
            val args = Bundle()
            args.putInt(ARG_XMLID, xmlId)
            args.putInt(ARG_TITLEID, titleId)
            return args
        }
    }
}