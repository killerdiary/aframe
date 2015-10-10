package com.hy.frame.util;

/**
 * 常量
 * 
 * @author HeYan
 * @time 2014年12月18日 下午4:36:33
 */
public class Constant {
    /** 权限 */
    public static final String PERMISSION = "android.permission.THYDSFT";
    // /** 消息页是否允许 */
    // public static final String APP_MSG_RUNNING = "APP_MSG_RUNNING";
    /** 是否是首次进入 */
    public static final String FIRST_ENTER = "FIRST_ENTER";
    /** 用户是否登录 */
    public static final String LAST_ACT = "LAST_ACT";
    /** 用户信息 */
    public static final String USER_INFO = "USER_INFO";
    /** 广告信息 */
    public static final String AD_LIST = "AD_LIST";
    /** 用户登录信息 */
    public static final String USER_LOGIN_INFO = "USER_LOGIN_INFO";
    /** 用户是否登录 */
    public static final String USER_ISLOGIN = "USER_LOGIN";
    /** 用户ID */
    public static final String USER_ID = "USER_IDTHREE";
    /** 上次记录的APP版本 */
    public static final String LAST_VERSION = "LAST_VERSION";
    /** 数据库名 */
    public static final String DB_NAME = "DB_SFT";
    /** 数据库版本 */
    public static final int DB_VERSION = 1;
    /** 网络状态 */
    public static final String NET_STATUS = "NET_STATUS";
    /** 网络状态-断开 */
    public static final int NET_DISCONNECT = -1;
    /** 网络状态-断开 */
    public static final int NET_CONNECTED = 1;
    /** 标记 */
    public static final String FLAG = "FLAG";
    /** 标记 2 */
    public static final String FLAG2 = "FLAG2";
    /** 标记 3 */
    public static final String FLAG3 = "FLAG3";
    /** 标记ID */
    public static final String FLAG_ID = "FLAG_ID";
    /** 标记标题 */
    public static final String FLAG_TITLE = "FLAG_TITLE";
    /** 登录 */
    public static final int FLAG_USER_LOGIN = 100;
    /** 普通注册 */
    public static final int FLAG_USER_REGIST = 101;
    /** QQ注册 */
    public static final int FLAG_USER_REGIST_QQ = 102;
    /** 修改登录密码 */
    public static final int FLAG_USER_UPD_PWD = 103;
    /** 类别请求 */
    public static final String REQUEST_KIND = "REQUEST_KIND";
    /** 类别请求 间隔 */
    public static final long REQUEST_KIND_SPLIT = 60 * 60 * 1000;
    // /** 接口地址 */
    // public static final String API_HOST = "";
    // --------------布局类型----------------
    /** 左图标 */
    public static final int HEADER_LEFT = 1;
    /** 左文本 */
    public static final int HEADER_LEFT_TXT = 100;
    /** 标题 */
    public static final int HEADER_TITLE = 0;
    /** 自定义标题,需要手动填充View */
    public static final int HEADER_TITLE_CUSTOM = 500;
    /** 右图标 */
    public static final int HEADER_RIGHT = 2;
    /** 右文本 */
    public static final int HEADER_RIGHT_TXT = 200;
    /** 每页的条数 */
    public static final int PAGE_SIZE = 10;
    /** 标题 */
    public static final int VIEW_DEFAULT = 0;
    /** 返回,标题 */
    public static final int VIEW_BACK = 1;
    /** 返回,标题 ,设置 */
    public static final int VIEW_BACK_SETTING = 2;
    /** 标题,图标 */
    public static final int VIEW_TITLE_RIGHT = HEADER_TITLE | HEADER_RIGHT;
    /** 无图模式 */
    public static final String SETTING_NOIMAGE = "SETTING_NOIMAGE";
    /** 病人昵称 */
    public static final String SICK_NICK = "SICK_NICK";
    /** 主页Footer隐藏 */
    public static final int MAIN_FOOTER_HIDE = 100;
    /** 主页跳转 */
    public static final int MAIN_CHANGE_PAGER = 104;
    /** 主页Footer显示 */
    public static final int MAIN_FOOTER_SHOW = 101;
    /** 主页Footer显示 */
    public static final int MAIN_FOOTER_DISMISS = 102;
    /** 隐藏子页面Footer */
    public static final int CHILD_FOOTER_HIDE = 103;
    /** 上次记录的手机号 */
    public static final String LAST_PHONE = "LAST_PHONE";
    /** 类型-医生 */
    public static final int TYPE_DOCTOR = 2;
    /** 类型-病人 */
    public static final int TYPE_SICK = 1;
    /** 内部消息-网络改变 */
    public static final String ACTION_RECEIVE_NET_CHANGE = "com.hy.frame.ACTION_NET_CHANGE";
    /** 内部消息 */
    public static final String ACTION_RECEIVE_MSG = "com.zhinantech.sft.ACTION_RECEIVE_MSG";
    /** 内部消息后台 */
    public static final String ACTION_RECEIVE_BACK_MSG = "com.zhinantech.sft.ACTION_RECEIVE_BACK_MSG";
    // /** 我的病人信息改变 */
    // public static final int FLAG_MYSICKS_CHANGE = 1001;
    /** 接受到推送消息，ID */
    public static final int FLAG_RECEIVE_PUSH_ID = 601;
    /** 接受到用户登录消息 */
    public static final int FLAG_RECEIVE_LOGIN = 602;
    /** 接受到用户注销消息 */
    public static final int FLAG_RECEIVE_LOGINOUT = 603;
    // /** 接受到注册推送 */
    // public static final int FLAG_RECEIVE_PUSH_REGIST = 604;
    /** 接收到初始化云通讯 */
    public static final int FLAG_RECEIVE_REGIST_CCP = 605;
    /** 接收到更新用户信息 */
    public static final int FLAG_RECEIVE_UPDATE_USER = 606;
    /** 提醒有新消息 */
    public static final int FLAG_RECEIVE_NOTIFY = 607;
    /** 提醒更新历史消息 */
    public static final int FLAG_RECEIVE_UPDATE_MSG = 608;
    /** 提醒强制退出 */
    public static final int FLAG_RECEIVE_FORCE_LOGINOUT = 609;
    /** 提醒通知点击 */
    public static final int FLAG_RECEIVE_NOTIFY_CLICK = 610;
//    /** 复诊页显示右边图标 */
//    public static final int FLAG_DIAGNOSE_HEADER_RIGHT_SHOW = 2001;
//    public static final int FLAG_DIAGNOSE_HEADER_RIGHT_HIDE = 2002;
//    public static final int FLAG_DIAGNOSE_HEADER_RIGHT_CLICK = 2003;
//    /** 医生 */
//    public static final String FLAG_DOCTOR = "FLAG_DOCTOR";
//    /** 病人 */
//    public static final String FLAG_SICK = "FLAG_SICK";
    // /** 退出 */
    // public static final String FLAG_EXIT = "FLAG_EXIT";
//    public static final int FLAG_WORKSTATUS_MOVE = 2101;
//    public static final int ITEM_ONE = 11;
//    public static final int ITEM_TWO = 12;
//    public static final int ITEM_THREE = 13;
//    public static final int ITEM_FOUR = 14;
//    public static final int ITEM_FIVE = 15;
//    /** 赞帖子 */
//    public static final String PRAISE_ARTICLE = "PRAISE_ARTICLE";
//    /** 赞回复 */
//    public static final String PRAISE_REPLY = "PRAISE_REPLY";
//
//    /** 最新资讯 */
//    public static final String FLAG_BBS_NEWS_STR = "FLAG_BBS_NEWS_STR";
//    /** 最新资讯量 */
//    public static final String FLAG_BBS_NEWS_NUM = "FLAG_BBS_NEWS_NUM";
//    /** 论坛 */
//    public static final String FLAG_BBS_STR = "FLAG_BBS_STR";
//    /** 论坛 */
//    public static final String FLAG_BBS_NUM = "FLAG_BBS_NUM";
//    /** 系统 */
//    public static final String FLAG_SYS_STR = "FLAG_SYS_STR";
//    /** 系统 */
//    public static final String FLAG_SYS_NUM = "FLAG_SYS_NUM";

//    public static final String CCP_SERVER = "app.cloopen.com";
//    public static final int CCP_PORT = 8883;
//
//    public static final String XGPUSH_ACCOUNT = "XGPUSH_ACCOUNT";

    public static final String HTTP_CONTENTTYPE = "application/x-www-form-urlencoded";
    public static final String HTTP_USERAGENT = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/525.13 (KHTML, like Gecko) Chrome/0.A.B.C Safari/525.13";
    public static final String HTTP_ACCEPT_JSON = "application/json";
    /**拍照*/
    public static final int FLAG_UPLOAD_TAKE_PICTURE = 10;
    /**选择图片*/
    public static final int FLAG_UPLOAD_CHOOICE_IMAGE = 12;
    /**剪切*/
    public static final int FLAG_UPLOAD_IMAGE_CUT = 13;
}
