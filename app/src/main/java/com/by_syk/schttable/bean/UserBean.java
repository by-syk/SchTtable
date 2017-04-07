package com.by_syk.schttable.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by By_syk on 2016-11-17.
 */

public class UserBean {
    // 用户KEY
    @SerializedName("userKey")
    private String userKey;

    // 学院
    @SerializedName("academy")
    private String academy;

    // 专业
    @SerializedName("major")
    private String major;

    // 姓名
    @SerializedName("userName")
    private String userName;

    public String getUserKey() {
        return userKey;
    }

    public String getAcademy() {
        return academy;
    }

    public String getMajor() {
        return major;
    }

    public String getUserName() {
        return userName;
    }
}
