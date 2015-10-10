package com.hy.frame.common;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.hy.frame.util.Constant;
import com.hy.frame.util.MyLog;
import com.hy.frame.util.MyShare;

/**
 * 应用
 * 
 * @author HeYan
 * @time 2014年12月17日 下午4:19:29
 */
public class BaseApplication extends Application {
    /** Activity栈 */
    private List<Activity> acts;
    /** 全局数据 */
    private HashMap<String, Object> hashMap;
    private IntentFilter filter;
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            MyLog.d(getClass(), "action:" + action);
            if (TextUtils.equals(action, ConnectivityManager.CONNECTIVITY_ACTION)) {
                ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                int result = -1;
                NetworkInfo netInfo = manager.getActiveNetworkInfo();// 当前网络信息
                if (netInfo != null && netInfo.isConnected()) {
                    result = netInfo.getType();
                }
                MyLog.d(getClass(), "NetState:" + result);
                MyShare.get(context).putInt(Constant.NET_STATUS, result);
                sendBroadcast(new Intent(Constant.ACTION_RECEIVE_NET_CHANGE));
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        MyLog.d(getClass(), "Application start!");
        acts = new LinkedList<Activity>();
        hashMap = new HashMap<String, Object>();
        initNetListener();
    }

    /**
     * 初始化网络监听
     */
    private void initNetListener() {
        filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.setPriority(IntentFilter.SYSTEM_LOW_PRIORITY);
        registerReceiver(receiver, filter);
        receiver.onReceive(this, new Intent(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (receiver != null)
            unregisterReceiver(receiver);
        MyLog.d(getClass(), "Application closed!");
    }

    /**
     * 添加Activity到容器中
     * 
     * @param activity
     */
    public void addActivity(Activity activity) {
        acts.add(activity);
    }

    /**
     * 清理activity栈 并退出
     */
    public void exit() {
        clear();
        System.exit(0);
    }

    /**
     * 清理activity栈
     */
    public void clear() {
        if (acts != null && !acts.isEmpty())
            for (Activity activity : acts) {
                activity.finish();
            }
    }

    /**
     * 存数据
     * 
     * @param key
     * @param value
     */
    public void putValue(String key, Object value) {
        if (hashMap == null) {
            hashMap = new HashMap<String, Object>();
        }
        hashMap.put(key, value);
    }

    /**
     * 取数据
     * 
     * @param key
     * @return
     */
    public Object getValue(String key) {
        if (hashMap != null) {
            return hashMap.get(key);
        }
        return null;
    }

}
