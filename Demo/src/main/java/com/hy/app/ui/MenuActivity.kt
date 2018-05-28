package com.hy.app.ui

import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.JsonReader
import android.view.View
import com.google.gson.JsonParser
import com.hy.app.R
import com.hy.app.adapter.MenuAdapter
import com.hy.app.bean.ArgumentInfo
import com.hy.app.common.BaseActivity
import com.hy.app.util.ComUtil
import com.hy.frame.adapter.IAdapterListener
import com.hy.frame.bean.MenuInfo
import com.hy.frame.mvp.IBasePresenter
import com.hy.frame.util.FormatUtil
import com.hy.frame.util.HyUtil
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

    override fun getLayoutId(): Int = R.layout.act_recycler

    override fun initView() {
        rcyList = findViewById(R.id.recycler_rcyList)
        //rcyList?.setBackgroundColor(Color.CYAN)
        //rcyList?.layoutManager = GridLayoutManager(context, 3, LinearLayoutManager.VERTICAL, false)
        rcyList?.layoutManager = GridLayoutManager(getCurContext(), 3)
        //rcyList?.layoutManager = StaggeredGridLayoutManager(context)
        //rcyList?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val padding = resources.getDimensionPixelSize(R.dimen.margin_normal)
        //rcyList?.setPadding(padding, 0, 0, 0)
        //rcyList?.addItemDecoration(GridItemDecoration(padding, resources.getColor(R.color.translucence)).setDividerVertical(padding).setPaddingTop(padding))
        rcyList?.addItemDecoration(GridItemDecoration(rcyList!!, padding, Color.TRANSPARENT).setDividerVertical(padding).setPaddingTop(padding).setPaddingLeft(padding).setPaddingRight(padding).setPaddingBottom(padding).build())
        //rcyList?.addItemDecoration(GridItemDecoration(rcyList!!, padding, resources.getColor(R.color.translucence)).setDividerVertical(padding).setPaddingTop(padding).setPaddingLeft(padding).setPaddingRight(padding).setPaddingBottom(padding).build())
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
        datas = MenuUtil[getCurContext()!!, xmlId]
        updateUI()
    }

    private fun updateUI() {
        if (adapter == null) {
            adapter = MenuAdapter(getCurContext()!!, datas, this)
            rcyList?.adapter = adapter
        } else
            adapter?.refresh(datas)
    }

    override fun onViewClick(v: View) {}

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
            val datas = ComUtil.getListFromJson(json, ArgumentInfo::class.java)
            if (FormatUtil.isNoEmpty(datas)) {
                for (i in datas!!) {
                    when(i.type){
                        "Int"->bundle.putInt(i.key, i.value!!.toInt())
                        "Long"->bundle.putLong(i.key, i.value!!.toLong())
                        "String"->bundle.putString(i.key, i.value)
                        "Boolean"->bundle.putBoolean(i.key, i.value!!.toBoolean())
                    }
                }
            }
        }
        if (!clsStr.isNullOrEmpty()) {
            try {
                val cls = Class.forName(clsStr)
                startAct(cls, bundle)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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