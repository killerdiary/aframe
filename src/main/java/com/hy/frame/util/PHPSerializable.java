package com.hy.frame.util;

/**
 * HY_Android
 *
 * @author HeYan
 * @time 2016/6/28 15:40
 */
public interface PHPSerializable {
    byte[] serialize();
    void unserialize(byte[] ss);
}
