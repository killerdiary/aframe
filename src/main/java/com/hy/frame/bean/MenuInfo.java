package com.hy.frame.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义菜单信息
 * 
 * @author HeYan
 * @time 2014-7-21 下午3:19:08
 */
public class MenuInfo {
    private int id;
    private int icon;
    private int title;
    private Map<String, String> data;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the icon
     */
    public int getIcon() {
        return icon;
    }

    /**
     * @param icon
     *            the icon to set
     */
    public void setIcon(int icon) {
        this.icon = icon;
    }

    /**
     * @return the title
     */
    public int getTitle() {
        return title;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(int title) {
        this.title = title;
    }

    public String getValue(String key) {
        if (data != null) {
            if (data.containsKey(key)) {
                return data.get(key);
            }
        }
        return null;
    }

    public void putValue(String key, String value) {
        if (data == null)
            data = new HashMap<String, String>();
        data.put(key, value);
    }

}
