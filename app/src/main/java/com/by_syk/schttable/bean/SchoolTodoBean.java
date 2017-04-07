package com.by_syk.schttable.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by By_syk on 2016-12-03.
 */

public class SchoolTodoBean {
    // 学校名称
    @SerializedName("name")
    private String name;

    // 已确定不支持
    @SerializedName("deprecated")
    private boolean deprecated;

    public String getName() {
        return name;
    }

    public boolean isDeprecated() {
        return deprecated;
    }
}
