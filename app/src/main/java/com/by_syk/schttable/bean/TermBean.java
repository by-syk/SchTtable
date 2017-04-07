package com.by_syk.schttable.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by By_syk on 2016-11-17.
 */

public class TermBean {
    // 学期
    @SerializedName("term")
    private int term;

    // 距开学天数（以在学期内为参考点）
    @SerializedName("daysFromStart")
    private int daysFromStart;

    // 距放假天数（以在学期内为参考点）
    @SerializedName("daysBeforeEnd")
    private int daysBeforeEnd;

    public int getTerm() {
        return term;
    }

    public int getDaysFromStart() {
        return daysFromStart;
    }

    public int getDaysBeforeEnd() {
        return daysBeforeEnd;
    }
}
