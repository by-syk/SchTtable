package com.by_syk.schttable.util;

import android.os.Build;

/**
 * Created by By_syk on 2016-07-16.
 */
public class C {
    public static final int SDK = Build.VERSION.SDK_INT;

    public static final String LOG_TAG = "SCHTTABLE";

    public static String BASE_URL_SERVER = "https://schttable.by-syk.com/";
//    public static String BASE_URL_SERVER = "http://192.168.43.76:8080/SchTtableServer/";
//    public static String BASE_URL_SERVER = "http://192.168.31.108:8080/SchTtableServer/";

    public static final int STATUS_LOADING = 0; // 正在加载
    public static final int STATUS_OK = 1; // 一切正常
    public static final int STATUS_NO_LESSONS = 2; // 今日无课
    public static final int STATUS_NO_LOGIN = 3; // 未登录
    public static final int STATUS_EMPTY = 4; // 无数据
    public static final int STATUS_NO_NETWORK = 5; // 未联网
    public static final int STATUS_ERROR = 6; // 出错

    public static final String ACTION_APPWIDGET_DATA_OK = "com.by_syk.schttable.ACTION_APPWIDGET_DATA_OK";
    public static final String ACTION_APPWIDGET_UPDATE = "com.by_syk.schttable.ACTION_APPWIDGET_UPDATE";
    public static final String ACTION_SIGN_OUT = "com.by_syk.schttable.ACTION_SIGN_OUT";
}
