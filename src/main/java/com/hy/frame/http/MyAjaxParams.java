package com.hy.frame.http;

import net.tsz.afinal.http.AjaxParams;

/**
 * 新增int long支持
 *
 * @author HeYan
 * @time 2014-9-28 上午11:41:18
 */
public class MyAjaxParams extends AjaxParams {

    public void put(String key, int value) {
        put(key, value + "");
    }

    public void put(String key, long value) {
        put(key, value + "");
    }

}
