package com.by_syk.schttable.util.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.by_syk.lib.toast.GlobalToast;
import com.by_syk.schttable.R;
import com.by_syk.schttable.bean.CourseBean;
import com.by_syk.schttable.util.DateUtil;

import java.util.List;

/**
 * Created by By_syk on 2016-11-15.
 */

public class CourseAdapter extends BaseAdapter {
    private Context context;

    private List<CourseBean> dataList;

    private LayoutInflater layoutInflater;

    public CourseAdapter(Context context, List<CourseBean> dataList) {
        this.context = context;
        this.dataList = dataList;

        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public CourseBean getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.list_item_course, viewGroup, false);

            viewHolder = new ViewHolder();
            viewHolder.viewHighlight = view.findViewById(R.id.view_highlight);
            viewHolder.viewBusy = view.findViewById(R.id.ll_busy);
            viewHolder.tvTimeStart = (TextView) view.findViewById(R.id.tv_course_time_start);
            viewHolder.tvTimeEnd = (TextView) view.findViewById(R.id.tv_course_time_end);
            viewHolder.tvName = (TextView) view.findViewById(R.id.tv_course_name);
            viewHolder.tvRoom = (TextView) view.findViewById(R.id.tv_course_room);
            viewHolder.tvLecturer = (TextView) view.findViewById(R.id.tv_course_lecturer);
            viewHolder.ivMerge = view.findViewById(R.id.iv_merge);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        CourseBean courseBean = dataList.get(i);
        viewHolder.tvTimeStart.setText(courseBean.getTimeStartReadable());
        viewHolder.tvTimeEnd.setText(courseBean.getTimeEndReadable());
        if (!courseBean.isSleep()) {
            viewHolder.viewBusy.setVisibility(View.VISIBLE);
            viewHolder.tvName.setText(courseBean.getName());
            viewHolder.tvRoom.setText(courseBean.getRoomAbbr());
            viewHolder.tvLecturer.setText(courseBean.getLecturer());
            viewHolder.ivMerge.setVisibility(courseBean.isMerge() ? View.VISIBLE : View.GONE);
        } else {
            viewHolder.viewBusy.setVisibility(View.GONE);
        }

        switch (courseBean.getInterval()) {
            case CourseBean.INTERVAL_MORNING:
//                viewHolder.viewHighlight.setBackgroundResource(R.drawable.tag_morning);
                viewHolder.viewHighlight.setBackgroundColor(Color.TRANSPARENT);
                break;
            case CourseBean.INTERVAL_AFTERNOON:
                viewHolder.viewHighlight.setBackgroundResource(R.drawable.tag_afternoon);
                break;
            case CourseBean.INTERVAL_EVENING:
//                viewHolder.viewHighlight.setBackgroundResource(R.drawable.tag_evening);
                viewHolder.viewHighlight.setBackgroundColor(Color.TRANSPARENT);
                break;
            default:
                viewHolder.viewHighlight.setBackgroundColor(Color.TRANSPARENT);
        }
        if (!courseBean.isNoCourseTime()) { // 如果有上下课时间，计算是否需要高亮
            highlight(viewHolder.viewHighlight, courseBean, i);
        }

        return view;
    }

    private void highlight(View viewHighlight, CourseBean courseBean, int pos) {
        long time = System.currentTimeMillis();
        long yesterdayTimeEnd = DateUtil.addDateMillis(dataList
                .get(dataList.size() - 1).getTimeEnd(), -1); // 昨天最后一节课结束时间
        if (time >= yesterdayTimeEnd && time < courseBean.getTimeEnd()
                && (pos == 0 || time >= dataList.get(pos - 1).getTimeEnd())) { // 需要高亮该节课
            if (time >= courseBean.getTimeStart()) {
                viewHighlight.setBackgroundResource(R.drawable.tag_cur);

                if (!courseBean.isSleep()) {
                    toastTime(courseBean.getTimeEnd() - time, false);
                }
            } else {
                viewHighlight.setBackgroundResource(R.drawable.tag_next);

                if (!courseBean.isSleep()) {
                    toastTime(courseBean.getTimeStart() - time, true);
                }
            }
        }
    }

    private void toastTime(long periodTime, boolean isBegin) {
        if (periodTime >= 60 * 60 * 1000) {
            return;
        }

        GlobalToast.showToast(context, context.getString(isBegin ? R.string.toast_time_to_class_on
                : R.string.toast_time_to_class_over, ((periodTime - 1) / (60 * 1000) + 1)));
    }

    private static class ViewHolder {
        View viewHighlight;
        View viewBusy;
        TextView tvTimeStart;
        TextView tvTimeEnd;
        TextView tvName;
        TextView tvRoom;
        TextView tvLecturer;
        View ivMerge;
    }
}
