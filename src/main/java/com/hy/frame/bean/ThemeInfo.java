package com.hy.frame.bean;

import android.graphics.drawable.Drawable;

/**
 * 主题信息
 * 
 * @author HeYan
 * @time 2015-9-17 上午10:13:14
 */
public class ThemeInfo {
    /** 返回图标 */
    private int icoBack;
    /** 标题颜色 */
    private int titleColor;
    /** 标题是否加粗 */
    private boolean titleBold;
    /** 标题栏背景 */
    private Drawable drawTitleBar;
    /** 标题字体大小 */
    private int titleSize;
    /** 主题色 */
    private int themeBackground;

    public int getIcoBack() {
        return icoBack;
    }

    public void setIcoBack(int icoBack) {
        this.icoBack = icoBack;
    }

    public int getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }

    public boolean isTitleBold() {
        return titleBold;
    }

    public void setTitleBold(boolean titleBold) {
        this.titleBold = titleBold;
    }

    public Drawable getDrawTitleBar() {
        return drawTitleBar;
    }

    public void setDrawTitleBar(Drawable drawTitleBar) {
        this.drawTitleBar = drawTitleBar;
    }

    public int getTitleSize() {
        return titleSize;
    }

    public void setTitleSize(int titleSize) {
        this.titleSize = titleSize;
    }

    public int getThemeBackground() {
        return themeBackground;
    }

    public void setThemeBackground(int themeBackground) {
        this.themeBackground = themeBackground;
    }

}
