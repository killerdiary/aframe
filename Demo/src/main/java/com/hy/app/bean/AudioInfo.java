package com.hy.app.bean;

import com.hy.frame.util.FormatUtil;
import com.hy.frame.util.HyUtil;

import java.text.DecimalFormat;

/**
 * @author HeYan
 * @title
 * @time 2015/10/23 13:13
 */
public class AudioInfo {
    String path;
    int time;
    double kb;
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public double getKb() {
        return kb;
    }

    public void setKb(double kb) {
        this.kb = kb;
    }

    public String getDate(){
        float date= (float)time/1000;
        return FormatUtil.INSTANCE.removeNumberUselessZero(new DecimalFormat("0.00").format(date))+"秒";
    }
}
