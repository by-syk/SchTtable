package com.by_syk.schttable.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.by_syk.schttable.bean.CourseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by By_syk on 2016-11-16.
 */

public class CourseSQLiteHelper extends SQLiteOpenHelper {
    private static CourseSQLiteHelper courseSQLiteHelper = null;

    private static final String DB_NAME = "courses.db";
    private static final int DB_VERSION = 6;

    public static CourseSQLiteHelper getInstance(Context context) {
        if (courseSQLiteHelper == null) {
            courseSQLiteHelper = new CourseSQLiteHelper(context);
        }
        return courseSQLiteHelper;
    }

    private CourseSQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        init(sqLiteDatabase, false);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.d(C.LOG_TAG, "CourseSQLiteHelper - onUpgrade");

        init(sqLiteDatabase, true);
    }

    private void init(SQLiteDatabase sqLiteDatabase, boolean recreate) {
        if (recreate) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS courses");
        }
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS courses("
                + "user_key VARCHAR, "
                + "name VARCHAR, "
                + "room VARCHAR, "
                + "room_abbr VARCHAR, "
                + "lecturer VARCHAR, "
                + "week_order INTEGER, "
                + "course_order INTEGER, "
                + "interval INTEGER, "
                + "interval_course_order INTEGER, "
                + "time_start INTEGER, "
                + "time_end INTEGER, "
                + "sleep INTEGER, "
                + "merge INTEGER, "
                + "PRIMARY KEY(user_key, time_start, time_end))");
    }

    public boolean saveDayCourses(String userKey, List<CourseBean> list) {
        if (userKey == null || list == null) {
            return false;
        }

        try {
            for (CourseBean courseBean : list) {
                getWritableDatabase().execSQL("REPLACE INTO courses(user_key, name, room, room_abbr," +
                        " lecturer, week_order, course_order, interval, interval_course_order," +
                        " time_start, time_end, sleep, merge)" +
                        " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        new String[]{userKey, courseBean.getName(), courseBean.getRoom(), courseBean.getRoomAbbr(),
                        courseBean.getLecturer(), String.valueOf(courseBean.getWeekOrder()),
                        String.valueOf(courseBean.getCourseOrder()), String.valueOf(courseBean.getInterval()),
                        String.valueOf(courseBean.getIntervalCourseOrder()), String.valueOf(courseBean.getTimeStart()),
                        String.valueOf(courseBean.getTimeEnd()), courseBean.isSleep() ? "1" : "0",
                        courseBean.isMerge() ? "1" : "0"});
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<CourseBean> getCourses(String userKey, long startDate) {
        if (TextUtils.isEmpty(userKey)) {
            return null;
        }

        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query("courses",
                    new String[]{"name", "room", "room_abbr", "lecturer", "week_order", "course_order",
                            "interval", "interval_course_order", "time_start", "time_end", "sleep", "merge"},
                    "user_key = ? and time_start >= ? and time_start < ?",
                    new String[]{userKey, String.valueOf(startDate), String.valueOf(DateUtil.addDateMillis(startDate, 1))},
                    null, null, "course_order");
            List<CourseBean> list = new ArrayList<>();
            while (cursor.moveToNext()) {
                CourseBean courseBean = new CourseBean();
                courseBean.setName(cursor.getString(0));
                courseBean.setRoom(cursor.getString(1));
                courseBean.setRoomAbbr(cursor.getString(2));
                courseBean.setLecturer(cursor.getString(3));
                courseBean.setWeekOrder(cursor.getInt(4));
                courseBean.setCourseOrder(cursor.getInt(5));
                courseBean.setInterval(cursor.getInt(6));
                courseBean.setIntervalCourseOrder(cursor.getInt(7));
                courseBean.setTimeStart(cursor.getLong(8));
                courseBean.setTimeEnd(cursor.getLong(9));
                courseBean.setSleep(cursor.getInt(10) == 1);
                courseBean.setMerge(cursor.getInt(11) == 1);
                list.add(courseBean);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    public boolean deleteCourses(String userKey) {
        try {
            getWritableDatabase().delete("courses", "user_key = ?", new String[]{userKey});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
