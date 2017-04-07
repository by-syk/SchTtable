package com.by_syk.schttable.bean;

import com.by_syk.schttable.util.DateUtil;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by By_syk on 2016-08-29.
 */

public class CourseBean implements Serializable {
    // 课程名
    @SerializedName("name")
    private String name;

    // 教室名
    @SerializedName("room")
    private String room;

    // 教室名缩写
    @SerializedName("roomAbbr")
    private String roomAbbr;

    // 讲师名
    @SerializedName("lecturer")
    private String lecturer;

    // 节次（1~
    @SerializedName("courseOrder")
    private int courseOrder;

    // 时段
    @SerializedName("interval")
    private int interval;

    // 时段内节次（1~
    @SerializedName("intervalCourseOrder")
    private int intervalCourseOrder;

    // 上课时间
    @SerializedName("timeStart")
    private long timeStart;

    // 下课时间
    @SerializedName("timeEnd")
    private long timeEnd;

    // 周次（1~
    @SerializedName("weekOrder")
    private int weekOrder;

    // 无课
    @SerializedName("sleep")
    private boolean sleep;

    // 该时间段有重课未显示
    @SerializedName("merge")
    private boolean merge;

    public static final int INTERVAL_DAY = 0; // 全天
    public static final int INTERVAL_MORNING = 1; // 上午
    public static final int INTERVAL_AFTERNOON = 2; // 下午
    public static final int INTERVAL_EVENING = 3; // 晚上

    public void setName(String name) {
        this.name = name;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setRoomAbbr(String roomAbbr) {
        this.roomAbbr = roomAbbr;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public void setCourseOrder(int courseOrder) {
        this.courseOrder = courseOrder;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setIntervalCourseOrder(int intervalCourseOrder) {
        this.intervalCourseOrder = intervalCourseOrder;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public void setWeekOrder(int weekOrder) {
        this.weekOrder = weekOrder;
    }

    public void setSleep(boolean sleep) {
        this.sleep = sleep;
    }

    public void setMerge(boolean merge) {
        this.merge = merge;
    }

    public String getName() {
        return name;
    }

    public String getRoom() {
        return room;
    }

    public String getRoomAbbr() {
        return roomAbbr;
    }

    public String getLecturer() {
        return lecturer;
    }

    public int getCourseOrder() {
        return courseOrder;
    }

    public int getInterval() {
        return interval;
    }

    public int getIntervalCourseOrder() {
        return intervalCourseOrder;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public String getTimeStartReadable() {
        String str = DateUtil.getDateStr(timeStart, "HH:mm");
        if ("00:00".equals(str)) {
            return "xx:xx";
        }
        return str;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public String getTimeEndReadable() {
        String str = DateUtil.getDateStr(timeEnd, "HH:mm");
        if ("00:00".equals(str)) {
            return "xx:xx";
        }
        return str;
    }

    public int getWeekOrder() {
        return weekOrder;
    }

    public boolean isSleep() {
        return sleep;
    }

    public boolean isMerge() {
        return merge;
    }

    public boolean isNoCourseTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStart);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour == 0) {
            return true;
        }
        calendar.setTimeInMillis(timeEnd);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour == 0) {
            return true;
        }
        return false;
    }
}
