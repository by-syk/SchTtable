package com.by_syk.schttable.bean;

import java.io.Serializable;

/**
 * Created by By_syk on 2016-11-17.
 */

public class FullUserBean implements Serializable {
    // 用户KEY
    private String userKey;

    // 学校代码
    private String schoolCode;

    // 学校
    private String schoolName;

    // 学院
    private String academy;

    // 专业
    private String major;

    // 姓名
    private String userName;

    // 学号
    private String stuNo;

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public void setSchoolCode(String schoolCode) {
        this.schoolCode = schoolCode;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public void setAcademy(String academy) {
        this.academy = academy;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setStuNo(String stuNo) {
        this.stuNo = stuNo;
    }

    public String getUserKey() {
        return userKey;
    }

    public String getSchoolCode() {
        return schoolCode;
    }

    public String getSchoolName() {
        return schoolName;
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

    public String getStuNo() {
        return stuNo;
    }
}
