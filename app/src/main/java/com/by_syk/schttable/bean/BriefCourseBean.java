package com.by_syk.schttable.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Created by By_syk on 2016-11-29.
 */

public class BriefCourseBean implements Parcelable {
    // 课程名
    private String name;

    // 教室名缩写
    private String roomAbbr;

    // 节次（1~
    private int courseOrder;

    // 节数（1~
    private int courseNum;

    // 上课时间
    private long timeStart;

    // 下课时间
    private long timeEnd;

    // 无课
    private boolean sleep;

    public BriefCourseBean() {}

    public void setName(String name) {
        this.name = name;
    }

    public void setRoomAbbr(String roomAbbr) {
        this.roomAbbr = roomAbbr;
    }

    public void setCourseOrder(int courseOrder) {
        if (courseOrder > 0) {
            this.courseOrder = courseOrder;
        }
    }

    public void setCourseNum(int courseNum) {
        if (courseNum > 0) {
            this.courseNum = courseNum;
        }
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public void setSleep(boolean sleep) {
        this.sleep = sleep;
    }

    public String getName() {
        return name;
    }

    public String getRoomAbbr() {
        return roomAbbr;
    }

    public int getCourseOrder() {
        return courseOrder;
    }

    public int getCourseNum() {
        return courseNum;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public boolean isSleep() {
        return sleep;
    }

    public void parse(CourseBean courseBean) {
        if (courseBean != null) {
            setName(courseBean.getName());
            setRoomAbbr(courseBean.getRoomAbbr());
            setCourseOrder(courseBean.getCourseOrder());
            setCourseNum(1);
            setTimeStart(courseBean.getTimeStart());
            setTimeEnd(courseBean.getTimeEnd());
            setSleep(courseBean.isSleep());
        }
    }

    public boolean isSame(CourseBean courseBean) {
        if (courseBean == null) {
            return false;
        }

        return ((TextUtils.isEmpty(name) && TextUtils.isEmpty(courseBean.getName()))
                || (name != null && name.equals(courseBean.getName())))
                && ((TextUtils.isEmpty(roomAbbr) && TextUtils.isEmpty(courseBean.getRoomAbbr()))
                || (roomAbbr != null && roomAbbr.equals(courseBean.getRoomAbbr())));
    }

    protected BriefCourseBean(Parcel in) {
        name = in.readString();
        roomAbbr = in.readString();
        courseOrder = in.readInt();
        courseNum = in.readInt();
        timeStart = in.readLong();
        timeEnd = in.readLong();
        sleep = in.readByte() == 1;
    }

    public static final Creator<BriefCourseBean> CREATOR = new Creator<BriefCourseBean>() {
        @Override
        public BriefCourseBean createFromParcel(Parcel in) {
            return new BriefCourseBean(in);
        }

        @Override
        public BriefCourseBean[] newArray(int size) {
            return new BriefCourseBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(roomAbbr);
        parcel.writeInt(courseOrder);
        parcel.writeInt(courseNum);
        parcel.writeLong(timeStart);
        parcel.writeLong(timeEnd);
        parcel.writeByte((byte) (sleep ? 1 : 0));
    }
}
