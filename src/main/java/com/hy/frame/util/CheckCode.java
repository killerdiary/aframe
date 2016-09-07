package com.hy.frame.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;

/**
 * 数据验证
 * 
 * @author HeYan
 * @time 2014年12月17日 下午4:06:03
 */
public class CheckCode {

    private static CheckCode instance = null;

    public static CheckCode get() {
        if (instance == null)
            instance = new CheckCode();
        return instance;
    }

    /**
     * 是否是手机号
     */
    public boolean isMobile(String mobiles) {
        if (mobiles == null)
            return false;
        Pattern p = Pattern.compile("^((13[0-9])|(14[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
        // String regExp = "^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$";
        // Pattern p = Pattern.compile(regExp);
        // Matcher m = p.matcher(mobiles);
        // return m.find();
    }

    /**
     * @param f
     * @return
     */
    public String floatToString(float f) {
        String str = f + "";
        if (str.indexOf(".") > 0) {
            str = str.replaceAll("0+?$", "");// 去掉多余的0
            str = str.replaceAll("[.]$", "");// 如最后一位是.则去掉
        }
        return str;
    }

    /**
     * 是否包含特殊符号
     * 
     * @param str
     * @return
     */
    public boolean isContainSpecialSymbols(String str) {
        String specialStr = "[^\\:\\!\"\\#\\$\\%\\&\\'\\(\\)\\*\\+\\,\\-\\.\\/\\:\\;\\<\\=\\>\\?\\@\\[\\\\\\]\\^\\_\\`\\{\\|\\}\\~]*";
        Pattern pattern = Pattern.compile(specialStr);
        Matcher matcher = pattern.matcher(str);
        return !matcher.matches();
    }

    /**
     * x10的金额转换成字符串(去掉多余的0)
     * 
     * @param money
     * @return
     */
    public String moneyToString(int money) {
        float f = (float) money / 10;
        return floatToString(f);
    }

    /**
     * 判断是否是数字组成
     * 
     * @param str
     * @return
     */
    public boolean isNum(String str) {
        return str.matches("[0-9]+");
    }

    public int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 是否是图片
     * 
     * @param path
     * @return
     */
    public boolean isImage(String path) {
        // 都可以用
        return path.matches("(?i).+?\\.(png|jpg|gif|bmp)");
        // return path.matches("\\w+\\.(jpg|gif|bmp|png)");
    }
}