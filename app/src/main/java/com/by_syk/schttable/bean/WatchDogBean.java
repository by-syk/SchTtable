package com.by_syk.schttable.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by By_syk on 2017-05-05.
 */

public class WatchDogBean {
    @SerializedName("port")
    private int port;

    @SerializedName("time")
    private long time;

    public int getPort() {
        return port;
    }

    public long getTime() {
        return time;
    }
}
