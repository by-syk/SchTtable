package com.by_syk.schttable.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by By_syk on 2016-11-14.
 */

public class SchoolBean implements Parcelable {
    // 学校代码
    @SerializedName("code")
    private String code;

    // 学校名称
    @SerializedName("name")
    private String name;

    // 学号长度
    @SerializedName("stuNoLen")
    private int stuNoLen;

    // 学号字符限制匹配，为null则默认匹配数字
    @SerializedName("stuNoRegex")
    private String stuNoRegex;

    // 学校教务网站链接
    @SerializedName("url")
    private String url;

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getStuNoLen() {
        return stuNoLen;
    }

    public String getStuNoRegex() {
        return stuNoRegex;
    }

    public String getUrl() {
        return url;
    }

    protected SchoolBean(Parcel in) {
        code = in.readString();
        name = in.readString();
        stuNoLen = in.readInt();
        stuNoRegex = in.readString();
        url = in.readString();
    }

    public static final Creator<SchoolBean> CREATOR = new Creator<SchoolBean>() {
        @Override
        public SchoolBean createFromParcel(Parcel in) {
            return new SchoolBean(in);
        }

        @Override
        public SchoolBean[] newArray(int size) {
            return new SchoolBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(code);
        parcel.writeString(name);
        parcel.writeInt(stuNoLen);
        parcel.writeString(stuNoRegex);
        parcel.writeString(url);
    }
}
