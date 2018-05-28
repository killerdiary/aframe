package com.hy.frame.bean

import java.util.*

/**
 * 自定义菜单信息
 * @author HeYan
 * @time 2014-7-21 下午3:19:08
 */
class MenuInfo {

    var id: Int = 0
    var icon: Int = 0
    var title: Int = 0
    private var data: MutableMap<String, String>? = null

    fun getValue(key: String): String? {
        if (data != null) {
            if (data!!.containsKey(key)) {
                return data!![key]
            }
        }
        return null
    }

    fun putValue(key: String, value: String) {
        if (data == null)
            data = HashMap()
        data!!.put(key, value)
    }

    constructor()

    constructor(id: Int, icon: Int, title: Int) {
        this.id = id
        this.icon = icon
        this.title = title
    }

    constructor(title: Int) {
        this.title = title
    }
}
