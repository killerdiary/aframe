package com.hy.frame.util

import android.content.Context
import android.content.res.XmlResourceParser
import com.hy.frame.bean.MenuInfo

/**
 * 菜单加载器
 * @author HeYan
 * @time 2014-7-21 下午3:22:06
 */
object MenuUtil {
    val KEY_CLS = "cls"
    val KEY_MENU = "menu"

    /**
     * 获取菜单列表

     * @param res 菜单xml文件的ResourceId
     *
     * @return
     */
    operator fun get(context: Context, res: Int): MutableList<MenuInfo> {
        val menus = ArrayList<MenuInfo>()
        val xrp = context.resources.getXml(res)
        if (xrp != null) {
            var menu: MenuInfo? = null
            // 判断是否到了文件的结尾
            try {
                while (xrp.eventType != XmlResourceParser.END_DOCUMENT) {
                    // 文件的内容的起始标签开始，注意这里的起始标签是test.xml文件里面<resources>标签下面的第一个标签
                    if (xrp.eventType == XmlResourceParser.START_TAG) {
                        val tagname = xrp.name
                        val size = xrp.attributeCount
                        if (tagname.endsWith("item")) {
                            menu = MenuInfo()
                            for (i in 0 until size) {
                                val key = xrp.getAttributeName(i)
                                val value = xrp.getAttributeValue(i)
                                when {
                                    key.contains("id") -> menu.id = Integer.parseInt(value.replace("@", ""))
                                    key.contains("icon") -> menu.icon = Integer.parseInt(value.replace("@", ""))
                                    key.contains("title") -> menu.title = Integer.parseInt(value.replace("@", ""))
                                    else -> menu.putValue(key, value.replace("@", ""))
                                }
                            }
                            menus.add(menu)
                        }
                    }
                    xrp.next()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return menus
    }
}
