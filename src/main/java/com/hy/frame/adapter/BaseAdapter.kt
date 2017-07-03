package com.hy.frame.adapter

import android.content.Context
import android.support.annotation.IdRes
import android.view.LayoutInflater
import android.view.View

/**
 * BaseAdapter for ListView or GridView
 * @author HeYan
 * @time 2017/5/23 9:35
 */
abstract class BaseAdapter<T> constructor(private val context: Context, listener: IAdapterListener<T>? = null) : android.widget.BaseAdapter() {
    protected var listener: IAdapterListener<T>? = null

    init {
        this.listener = listener
    }

    protected fun inflate(resId: Int): View {
        return LayoutInflater.from(context).inflate(resId, null)
    }

    fun setOnClickListener(v: View, t: T, position: Int) {
        if (listener != null)
            v.setOnClickListener(ViewOnClick(t, position))
    }

    fun setOnLongClickListener(v: View, t: T, position: Int) {
        if (listener != null && listener is IAdapterLongListener<*>)
            v.setOnLongClickListener(ViewOnLongClick(t, position))
    }

    /**
     * 获取 控件
     * @param id 行布局中某个组件的id
     * @param parent  parent
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : View> findViewById(@IdRes id: Int, parent: View?): T? {
        val view = parent?.findViewById<View>(id)
        return if(view ==null) null else view as T
    }

    /**
     * 获取并绑定点击
     * @param id 行布局中某个组件的id
     * @param parent  parent
     */
    protected fun <T : View> setOnClickListener(@IdRes id: Int, parent: View?, listener: View.OnClickListener): T? {
        val view = findViewById<T>(id, parent)
        view?.setOnClickListener(listener) ?: return null
        return view
    }

    internal inner class ViewOnClick(var obj: T, var position: Int) : android.view.View.OnClickListener {

        override fun onClick(v: android.view.View) {
            if (listener != null) {
                listener!!.onViewClick(v, obj, position)
            }
        }

    }

    internal inner class ViewOnLongClick(var obj: T, var position: Int) : android.view.View.OnLongClickListener {
        var listener: IAdapterLongListener<T>? = null

        init {
            this.listener = listener as IAdapterLongListener<T>
        }

        override fun onLongClick(v: android.view.View): Boolean {
            if (listener != null) {
                listener!!.onViewLongClick(v, obj, position)
            }
            return false
        }

    }
}
