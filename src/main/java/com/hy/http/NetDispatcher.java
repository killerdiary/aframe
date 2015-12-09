package com.hy.http;

import android.content.Context;
import android.net.http.RequestQueue;
import android.os.Process;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class NetDispatcher extends Thread {

    private final BlockingQueue<Request> queues;
    private volatile boolean quit = false;

    public NetDispatcher() {
        queues = new LinkedBlockingQueue<>();
    }

    public void add(Request request) {
        queues.add(request);
    }

    public void quit() {
        quit = true;
        interrupt();
    }


    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        while (true) {
            Request request;
            try {
                request = queues.take();
            } catch (InterruptedException e) {
                // We may have been interrupted because it was time to quit.
                if (quit) {
                    return;
                }
                continue;
            }
            if (null != request) {
                //request

            }
            if (quit)
                break;
        }
    }
}
