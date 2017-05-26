package com.hy.frame.bean

import java.util.HashMap

/**
 * 自定义菜单信息

 * @author HeYan
 * *
 * @time 2014-7-21 下午3:19:08
 */
class MenuInfo {
    /**
     * @return the id
     */
    /**
     * @param id the id to set
     */
    var id: Int = 0
    /**
     * @return the icon
     */
    /**
     * @param icon the icon to set
     */
    var icon: Int = 0
    /**
     * @return the title
     */
    /**
     * @param title the title to set
     */
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
            data = HashMap<String, String>()
        data!!.put(key, value)
    }

    constructor() {}

    constructor(id: Int, icon: Int, title: Int) {
        this.id = id
        this.icon = icon
        this.title = title
    }

    constructor(title: Int) {
        this.title = title
    }
}
