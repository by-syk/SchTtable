package com.by_syk.schttable.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.by_syk.schttable.bean.BriefCourseBean;
import com.by_syk.schttable.bean.CourseBean;
import com.by_syk.schttable.bean.ResResBean;
import com.by_syk.schttable.util.C;
import com.by_syk.schttable.util.CourseSQLiteHelper;
import com.by_syk.schttable.util.DateUtil;
import com.by_syk.schttable.util.ExtraUtil;
import com.by_syk.schttable.util.RetrofitHelper;
import com.by_syk.schttable.util.impl.ServerService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;

/**
 * Created by By_syk on 2016-11-29.
 */

public class AppWidgetService extends Service {
    private ArrayList<BriefCourseBean> dataList = null;

    // 由于多进程，考虑到 SharedPreference 数据安全，所以选择参数靠传递
    private String spUserKey = null;

    private Thread thread = null;

    private long lastLoadDataTime = 0; // 上次加载数据时间
    private long nextBroadcastTime = Long.MAX_VALUE; // 下次刷新小部件时间

    private boolean isRunning = true;

    private static final int THREAD_PERIOD = 5 * 60 * 1000;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    @Override
//    public void onCreate() {
//        Log.d(C.LOG_TAG, "AppWidgetService - onCreate");
//
//        super.onCreate();
//    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(C.LOG_TAG, "AppWidgetService - onStartCommand");

        boolean delay = false;
        if (intent != null) {
            spUserKey = intent.getStringExtra("userKey");
            delay = intent.getBooleanExtra("delay", false);
        }

        if (thread == null) {
            thread = new Thread(new MyThread(delay));
            thread.setDaemon(true);
            thread.start();
        } else {
            (new Thread(new MyTempThread(delay))).start();
        }

        // 被系统杀掉后在适合的时机自动重启并使用上次 Intent
//        return START_STICKY;
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        Log.d(C.LOG_TAG, "AppWidgetService - onDestroy");

        super.onDestroy();

        isRunning = false;
        thread = null;
    }

    /**
     * 线程核心任务
     * （同步锁）
     */
    private synchronized void task() {
        Log.d(C.LOG_TAG, "AppWidgetService - task");

        if (dataList == null || !DateUtil.isToday(lastLoadDataTime)) {
            loadData();
            broadcast();
            calculateNextBroadcastTime();
        } else if (System.currentTimeMillis() >= nextBroadcastTime) {
            broadcast();
            calculateNextBroadcastTime();
        }
    }

    /**
     * 加载数据
     */
    private void loadData() {
        Log.d(C.LOG_TAG, "AppWidgetService - loadData");

        lastLoadDataTime = System.currentTimeMillis();

        if (TextUtils.isEmpty(spUserKey)) { // 未登录
            dataList = null;
            return;
        }

        CourseSQLiteHelper sqLiteHelper = CourseSQLiteHelper.getInstance(this);

        Date date = DateUtil.toDayDate();
        List<CourseBean> list = sqLiteHelper.getCourses(spUserKey, date.getTime());
        if (list == null || list.isEmpty()) {
            if (ExtraUtil.isNetworkConnected(this)) {
//                list = TimetableTool.getCourses(spUserKey, date.getTime());
                ServerService service = RetrofitHelper.getInstance().getService(ServerService.class);
                Call<ResResBean<List<CourseBean>>> call = service.getDayCourses(spUserKey, date.getTime());
                try {
                    ResResBean<List<CourseBean>> resResBean = call.execute().body();
                    if (resResBean != null && resResBean.isStatusSuccess()) {
                        list = resResBean.getResult();
                        sqLiteHelper.saveDayCourses(spUserKey, list);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                list = null;
            }
        }
        if (list == null) {
            return;
        }

        dataList = new ArrayList<>();
        for (CourseBean courseBean : list) {
            if (!dataList.isEmpty()) {
                BriefCourseBean briefCourseBean = dataList.get(dataList.size() - 1);
                if (briefCourseBean.isSame(courseBean)) {
                    briefCourseBean.setCourseNum(briefCourseBean.getCourseNum() + 1);
                    briefCourseBean.setTimeEnd(courseBean.getTimeEnd());
                    continue;
                }
            }
            BriefCourseBean briefCourseBean = new BriefCourseBean();
            briefCourseBean.parse(courseBean);
            dataList.add(briefCourseBean);
        }
    }

    /**
     * 计算下次广播时间
     */
    private void calculateNextBroadcastTime() {
        nextBroadcastTime = Long.MAX_VALUE;
        if (dataList == null || dataList.isEmpty()) {
            return;
        }

        for (BriefCourseBean bean : dataList) {
            if (System.currentTimeMillis() < bean.getTimeStart()) {
                nextBroadcastTime = bean.getTimeStart();
                break;
            } else if (System.currentTimeMillis() < bean.getTimeEnd()) {
                nextBroadcastTime = bean.getTimeEnd();
                break;
            }
        }

        Log.d(C.LOG_TAG, "AppWidgetService - calculateNextBroadcastTime: "
                + DateUtil.getDateStr(nextBroadcastTime, "HH:mm"));
    }

    /**
     * 发送广播通知小部件刷新
     */
    private void broadcast() {
        Intent intent = new Intent(C.ACTION_APPWIDGET_DATA_OK);
        intent.putParcelableArrayListExtra("data", dataList);

        intent.putExtra("status", C.STATUS_ERROR);
        if (TextUtils.isEmpty(spUserKey)) { // 未登录
            intent.putExtra("status", C.STATUS_NO_LOGIN);
        } else if (dataList == null) { // 数据获取失败
            if (!ExtraUtil.isNetworkConnected(this)) { // 未联网
                intent.putExtra("status", C.STATUS_NO_NETWORK);
            }
        } else if (dataList.isEmpty()) { // 无数据
            intent.putExtra("status", C.STATUS_EMPTY);
        } else {
            boolean noLessons = true;
            for (BriefCourseBean bean : dataList) {
                if (!bean.isSleep()) {
                    noLessons = false;
                    break;
                }
            }
            if (noLessons) {
                intent.putExtra("status", C.STATUS_NO_LESSONS);
            } else {
                intent.putExtra("status", C.STATUS_OK);
            }
        }

        sendBroadcast(intent);
    }

    private class MyThread implements Runnable {
        private boolean delay = false;

        MyThread(boolean delay) {
            this.delay = delay;
        }

        @Override
        public void run() {
            if (delay) {
                SystemClock.sleep(6 * 1000);
            }
            while (true) {
                if (!isRunning) {
                    return;
                }
                task();
                SystemClock.sleep(THREAD_PERIOD);
            }
        }
    }

    private class MyTempThread implements Runnable {
        private boolean delay = false;

        MyTempThread(boolean delay) {
            this.delay = delay;
        }

        @Override
        public void run() {
            if (delay) {
                SystemClock.sleep(6 * 1000);
            }
            if (!isRunning) {
                return;
            }
            nextBroadcastTime = System.currentTimeMillis() - 1;
            task();
        }
    }
}
