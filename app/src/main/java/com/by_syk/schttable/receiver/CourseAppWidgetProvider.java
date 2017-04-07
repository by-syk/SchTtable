package com.by_syk.schttable.receiver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.by_syk.lib.storage.SP;
import com.by_syk.schttable.MainActivity;
import com.by_syk.schttable.R;
import com.by_syk.schttable.bean.BriefCourseBean;
import com.by_syk.schttable.service.AppWidgetService;
import com.by_syk.schttable.util.C;

import java.util.Calendar;
import java.util.List;

/**
 * Created by By_syk on 2016-11-27.
 */

public class CourseAppWidgetProvider extends AppWidgetProvider {
    private int[] ivHighlightIds;
    private int[] rlCourseIds;
    private int[] tvPeriodIds;
    private int[] tvCourseIds;
    private int[] tvRoomIds;

    private String[] weekdays;

    private boolean isInitOk = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(C.LOG_TAG, "CourseAppWidgetProvider - onReceive: " + intent.getAction());

        switch (intent.getAction()) {
            case C.ACTION_APPWIDGET_DATA_OK: {
                List<BriefCourseBean> dataList = intent.getParcelableArrayListExtra("data");
                int status = intent.getIntExtra("status", C.STATUS_ERROR);
                updateViews(context, AppWidgetManager.getInstance(context), dataList, status);
                if (status == C.STATUS_NO_LOGIN) {
                    stopService(context);
                }
                break;
            }
            case C.ACTION_APPWIDGET_UPDATE:
//                updateViews(context, AppWidgetManager.getInstance(context), null, C.STATUS_LOADING);
                startService(context, true);
                break;
            case C.ACTION_SIGN_OUT:
                updateViews(context, AppWidgetManager.getInstance(context), null, C.STATUS_NO_LOGIN);
                stopService(context);
                break;
            default:
                super.onReceive(context, intent);
        }
    }

    /**
     * 第一个小部件被添加
     */
    @Override
    public void onEnabled(Context context) {
        Log.d(C.LOG_TAG, "CourseAppWidgetProvider - onEnabled");

        super.onEnabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(C.LOG_TAG, "CourseAppWidgetProvider - onUpdate");

        super.onUpdate(context, appWidgetManager, appWidgetIds);

        startService(context, false);
    }

//    /**
//     * 单个小部件被移除
//     */
//    @Override
//    public void onDeleted(Context context, int[] appWidgetIds) {
//        Log.d(C.LOG_TAG, "CourseAppWidgetProvider - onDeleted");
//
//        super.onDeleted(context, appWidgetIds);
//    }

    /**
     * 所有小部件被移除
     */
    @Override
    public void onDisabled(Context context) {
        Log.d(C.LOG_TAG, "CourseAppWidgetProvider - onDisabled");

        super.onDisabled(context);

        stopService(context);
    }

//    /**
//     * 小部件尺寸改变
//     */
//    @Override
//    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
//        Log.d(C.LOG_TAG, "CourseAppWidgetProvider - onAppWidgetOptionsChanged");
//
//        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
//    }

    private void init(Context context) {
        ivHighlightIds = new int[]{R.id.iv_highlight1, R.id.iv_highlight2, R.id.iv_highlight3,
                R.id.iv_highlight4, R.id.iv_highlight5};
        rlCourseIds = new int[]{R.id.rl_course1, R.id.rl_course2, R.id.rl_course3,
                R.id.rl_course4, R.id.rl_course5};
        tvPeriodIds = new int[]{R.id.tv_period1, R.id.tv_period2, R.id.tv_period3,
                R.id.tv_period4, R.id.tv_period5};
        tvCourseIds = new int[]{R.id.tv_course1, R.id.tv_course2, R.id.tv_course3,
                R.id.tv_course4, R.id.tv_course5};
        tvRoomIds = new int[]{R.id.tv_room1, R.id.tv_room2, R.id.tv_room3,
                R.id.tv_room4, R.id.tv_room5};

        weekdays = context.getResources().getStringArray(R.array.weekdays_brief);
    }

    /**
     * 更新小部件
     *
     * @param dataList 须是按时间升序排好
     */
    private void updateViews(Context context, AppWidgetManager appWidgetManager,
                             List<BriefCourseBean> dataList, int status) {
        Log.d(C.LOG_TAG, "CourseAppWidgetProvider - updateViews");

        if (!isInitOk) { // 初始化
            init(context);
            isInitOk = true;
        }

        String weekday = weekdays[(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 7 - 2) % 7];

        // 启动服务
        PendingIntent piService = PendingIntent.getService(context, 0,
                getServiceIntent(context, false), PendingIntent.FLAG_UPDATE_CURRENT);

        // 打开主界面
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("updateService", true); // 使点击时启动或更新服务
        PendingIntent piActivity = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,
                CourseAppWidgetProvider.class));
        for (int appWidgetId : appWidgetIds) { // 循环更新所有显示的小部件
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget);
            remoteViews.setTextViewText(R.id.tv_weekday, weekday);
            remoteViews.setOnClickPendingIntent(R.id.tv_weekday, piService);

            if (status == C.STATUS_OK) { // 数据正常
                long time = System.currentTimeMillis();
                boolean noLessons = true;
                for (int i = 0; i < 5; ++i) {
                    if (i > dataList.size() - 1) {
                        remoteViews.setViewVisibility(rlCourseIds[i], View.GONE);
                        continue;
                    }
                    BriefCourseBean bean = dataList.get(i);
                    if (bean.isSleep()) { // 无课不显示
                        remoteViews.setViewVisibility(rlCourseIds[i], View.GONE);
                        continue;
                    }
                    remoteViews.setViewVisibility(rlCourseIds[i], View.VISIBLE);
                    noLessons = false;

                    // 高亮标签
                    if (time < bean.getTimeStart()) {
                        remoteViews.setImageViewResource(ivHighlightIds[i], R.drawable.tag_next_small);
                        time = Long.MAX_VALUE;
                    } else if (time < bean.getTimeEnd()) {
                        remoteViews.setImageViewResource(ivHighlightIds[i], R.drawable.tag_cur_small);
                        time = Long.MAX_VALUE;
                    } else {
                        remoteViews.setImageViewBitmap(ivHighlightIds[i], null);
                    }
                    // 时间段
                    String period = String.valueOf(bean.getCourseOrder());
                    if (bean.getCourseNum() > 1) {
                        period += " - " + (bean.getCourseOrder() + bean.getCourseNum() - 1);
                    }
                    remoteViews.setTextViewText(tvPeriodIds[i], period);
                    // 课程名
                    remoteViews.setTextViewText(tvCourseIds[i], bean.getName());
                    remoteViews.setOnClickPendingIntent(tvCourseIds[i], piActivity);
                    // 教室
                    remoteViews.setTextViewText(tvRoomIds[i], bean.getRoomAbbr());
                }
                if (!noLessons) {
                    showContent(remoteViews);
                } else {
                    showHint(remoteViews, context, piActivity, C.STATUS_NO_LESSONS);
                }
            } else {
                showHint(remoteViews, context, piActivity, status);
            }

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    private void showHint(RemoteViews remoteViews, Context context, PendingIntent pendingIntent, int status) {
        String text;
        switch (status) {
            case C.STATUS_LOADING:
                text = context.getString(R.string.status_loading);
                break;
            case C.STATUS_NO_LOGIN:
                text = context.getString(R.string.status_no_login);
                break;
            case C.STATUS_NO_LESSONS:
                text = context.getString(R.string.status_no_lessons);
                break;
            case C.STATUS_EMPTY:
                text = context.getString(R.string.status_empty);
                break;
            case C.STATUS_NO_NETWORK:
                text = context.getString(R.string.status_no_network);
                break;
            case C.STATUS_ERROR:
            default:
                text = context.getString(R.string.status_error);
        }

        remoteViews.setTextViewText(R.id.tv_hint, text);
        remoteViews.setOnClickPendingIntent(R.id.tv_hint, pendingIntent);
        remoteViews.setViewVisibility(R.id.tv_hint, View.VISIBLE);
        remoteViews.setViewVisibility(R.id.ll_content, View.GONE);
    }

    private void showContent(RemoteViews remoteViews) {
        remoteViews.setViewVisibility(R.id.tv_hint, View.GONE);
        remoteViews.setViewVisibility(R.id.ll_content, View.VISIBLE);
    }

    private Intent getServiceIntent(Context context, boolean delay) {
        SP sp = new SP(context, false);
        Intent intent = new Intent(context, AppWidgetService.class);
        intent.putExtra("userKey", sp.getString("userKey"));
        intent.putExtra("delay", delay);
        return intent;
    }

    /**
     * 如果未启动刷新服务则启动；已启动则发送命令立即刷新
     */
    private void startService(Context context, boolean delay) {
        context.startService(getServiceIntent(context, delay));
    }

    /**
     * 结束刷新服务
     */
    private void stopService(Context context) {
        context.stopService(new Intent(context, AppWidgetService.class));
    }
}
