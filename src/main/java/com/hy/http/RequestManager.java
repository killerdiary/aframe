package com.hy.http;

import android.content.Context;
import android.util.SparseArray;

import java.util.concurrent.BlockingDeque;

/**
 * @author HeYan
 * @title 请求管理器
 * @time 2015/11/19 10:51
 */
public final class RequestManager {

    private static RequestManager manager;
    private final static int MAX_THREAD = 5;
    private SparseArray<NetDispatcher> dispatchers;
    private BlockingDeque<Request> waitings;

    private RequestManager() {
        dispatchers = new SparseArray<>();
    }

    public static RequestManager get() {
        if (manager == null)
            manager = new RequestManager();
        return manager;
    }

    public void add(Context context, Request request) {
        NetDispatcher dispatcher;
        if (dispatchers.indexOfKey(context.hashCode()) >= 0)
            dispatcher = dispatchers.get(context.hashCode());
        else
            dispatcher = new NetDispatcher();
        dispatcher.add(request);
        dispatchers.put(context.hashCode(), dispatcher);
    }
}
