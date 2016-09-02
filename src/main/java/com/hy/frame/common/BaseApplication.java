package com.hy.frame.common;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.CallSuper;
import android.text.TextUtils;

import com.hy.frame.util.Constant;
import com.hy.frame.util.HyUtil;
import com.hy.frame.util.MyLog;
import com.hy.frame.util.MyShare;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 应用
 *
 * @author HeYan
 * @time 2014年12月17日 下午4:19:29
 */
public class BaseApplication extends Application {
    /**
     * Activity栈
     */
    private List<Activity> acts;
    /**
     * 全局数据
     */
    private HashMap<String, Object> hashMap;
    private BroadcastReceiver receiver;

    @Override
    public void onCreate() {
        super.onCreate();
        String processName = HyUtil.getProcessName(this, android.os.Process.myPid());
        if (processName != null) {
            MyLog.d(getClass(), "Application start! process:" + processName);
            boolean defaultProcess = TextUtils.equals(processName, getPackageName());
            if (defaultProcess) {
                initAppForMainProcess();
            } else {
                initAppForOtherProcess(processName);
            }
        }
    }

    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        MyLog.d(getClass(), "Application closed! onTerminate");
        if (receiver != null) {
            try {
                unregisterReceiver(receiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        MyLog.d(getClass(), "onLowMemory");
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        MyLog.d(getClass(), "onTrimMemory");
        super.onTrimMemory(level);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        MyLog.d(getClass(), "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }


    /**
     * 添加Activity到容器中
     */
    public void addActivity(Activity activity) {
        //防止重复添加
        remove(activity);
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
    public void remove(Activity activity) {
        if (acts != null && !acts.isEmpty()) {
            acts.remove(activity);
        }
    }

    /**
     * 清理activity栈
     */
    public void removeFinish(Class cls) {
        if (acts != null && !acts.isEmpty()) {
            Activity act = null;
            for (Activity item : acts) {
                if (TextUtils.equals(item.getClass().getName(), cls.getName())) {
                    act = item;
                    break;
                }
            }
            if (act != null) {
                act.finish();
                acts.remove(act);
            }
        }
    }

    /**
     * 清理activity栈
     */
    public void clear() {
        if (acts != null && !acts.isEmpty()) {
            for (Activity activity : acts) {
                activity.finish();
            }
            acts.clear();
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
            hashMap = new HashMap<>();
        }
        hashMap.put(key, value);
    }

    /**
     * 取数据
     */
    public Object getValue(String key) {
        if (hashMap != null) {
            return hashMap.get(key);
        }
        return null;
    }

    /**
     * 主线程方法
     */
    @CallSuper
    protected void initAppForMainProcess() {
        initAppForMainProcess(null);
    }

    @CallSuper
    protected void initAppForMainProcess(IntentFilter filter) {
        if (filter != null) {
            filter.setPriority(IntentFilter.SYSTEM_LOW_PRIORITY);
            if (receiver == null)
                receiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String action = intent.getAction();
                        MyLog.d(getClass(), "action:" + action);
                        BaseApplication.this.onReceive(this, intent);
                    }
                };
            registerReceiver(receiver, filter);
        }
        acts = new CopyOnWriteArrayList<>();
        hashMap = new HashMap<>();
    }

    /**
     * 其它线程方法
     */
    protected void initAppForOtherProcess(String process) {

    }

    protected void onReceive(BroadcastReceiver receiver, Intent intent) {

    }
}
