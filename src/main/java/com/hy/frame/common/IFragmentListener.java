package com.hy.frame.common;

/**
 * 用于Fragment通信
 * 
 * @author HeYan
 * @time 2014年8月11日 下午2:59:49
 */
public interface IFragmentListener {
    void sendMsg(int flag, Object obj);
}
