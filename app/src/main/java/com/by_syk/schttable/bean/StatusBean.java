package com.by_syk.schttable.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by By_syk on 2016-12-06.
 */

public class StatusBean implements Serializable {
    @SerializedName("id")
    private String id;

    @SerializedName("code")
    private int code;

    @SerializedName("desc")
    private String desc;

    @SerializedName("time")
    private long time;

    // 无效状态码（未定义）
    public static final int STATUS_CODE_UNDEFINED = 0;
    // 参数错误
    public static final int STATUS_CODE_ERR_PARAS = -301;
    // 参数错误
    public static final int STATUS_CODE_ERR_NET = -302;

    // 成功
    public static final int STATUS_CODE_SUCCESS = 1;
    // 等待，稍后再查询结果
    public static final int STATUS_CODE_WAIT = 2;

    public String getId() {
        return id;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public long getTime() {
        return time;
    }
}
