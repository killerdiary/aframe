package com.hy.app.bean;

/**
 * Created by HeYan on 2016/5/13.
 */
public class GameInfo {
    private int color;
    private int flag;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public GameInfo(int color, int flag) {
        this.color = color;
        this.flag = flag;
    }
    public GameInfo() {}
}
