package com.hy.frame.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;

import com.hy.frame.bean.MenuInfo;

/**
 * 菜单加载器
 *
 * @author HeYan
 * @time 2014-7-21 下午3:22:06
 */
public class MenuUtil {
    public static final String KEY_CLS = "cls";
    public static final String KEY_PAGER = "pager";

    /**
     * 获取菜单列表
     *
     * @param res 菜单xml文件的ResourceId
     * @return
     */
    public static List<MenuInfo> get(Context context, int res) {
        List<MenuInfo> menus = new ArrayList<MenuInfo>();
        XmlResourceParser xrp = context.getResources().getXml(res);
        if (xrp != null) {
            MenuInfo menu = null;
            // 判断是否到了文件的结尾
            try {
                String val;
                while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
                    // 文件的内容的起始标签开始，注意这里的起始标签是test.xml文件里面<resources>标签下面的第一个标签
                    if (xrp.getEventType() == XmlResourceParser.START_TAG) {
                        String tagname = xrp.getName();
                        int size = xrp.getAttributeCount();
                        if (tagname.endsWith("item") && size > 2) {
                            menu = new MenuInfo();
                            for (int i = 0; i < size; i++) {
                                String key = xrp.getAttributeName(i);
                                String value = xrp.getAttributeValue(i);
                                if (key.contains("id")) {
                                    menu.setId(Integer.parseInt(value.replace("@", "")));
                                } else if (key.contains("icon")) {
                                    menu.setIcon(Integer.parseInt(value.replace("@", "")));
                                } else if (key.contains("title")) {
                                    menu.setTitle(Integer.parseInt(value.replace("@", "")));
                                } else {
                                    menu.putValue(key, value.replace("@", ""));
                                }
                            }
                            menus.add(menu);
                        }
                    }
                    xrp.next();
                }
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return menus;
    }
}
