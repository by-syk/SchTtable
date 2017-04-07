package com.by_syk.schttable.bean;

import android.content.Context;

import com.by_syk.schttable.SignInActivity;
import com.by_syk.schttable.util.C;
import com.by_syk.schttable.util.ExtraUtil;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by By_syk on 2016-11-20.
 */

public class AppVerBean implements Serializable {
    @SerializedName("pkgName")
    private String pkgName;

    @SerializedName("verName")
    private String verName;

    @SerializedName("verCode")
    private int verCode;

    @SerializedName("minSdk")
    private int minSdk;

    @SerializedName("apkSize")
    private int apkSize;

    @SerializedName("desc")
    private String desc;

    @SerializedName("date")
    private long date;

    @SerializedName("url")
    private String url;

    public String getPkgName() {
        return pkgName;
    }

    public String getVerName() {
        return verName;
    }

    public int getVerCode() {
        return verCode;
    }

    public int getMinSdk() {
        return minSdk;
    }

    public int getApkSize() {
        return apkSize;
    }

    public String getDesc() {
        return desc;
    }

    public long getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }

    public boolean isNew(Context context) {
        return C.SDK >= minSdk && verCode > ExtraUtil.getAppVerCode(context);
    }
}
