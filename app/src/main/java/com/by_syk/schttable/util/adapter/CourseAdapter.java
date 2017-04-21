package com.by_syk.schttable.util.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.by_syk.lib.toast.GlobalToast;
import com.by_syk.schttable.R;
import com.by_syk.schttable.bean.CourseBean;
import com.by_syk.schttable.util.C;
import com.by_syk.schttable.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by By_syk on 2017-04-19.
 */

public class CourseAdapter extends RecyclerView.Adapter {
    private Context context;

    private LayoutInflater layoutInflater;

    private List<CourseBean> dataList = new ArrayList<>();

    private int sectionMorningPosInItems = -1;
    private int sectionAfternoonPosInItems = -1;
    private int sectionEveningPosInItems = -1;
    private final int[] SECTION_ICON_ID_ARR = {
            R.drawable.ic_section_morning,
            R.drawable.ic_section_afternoon,
            R.drawable.ic_section_evening
    };
    private final int[] SECTION_TEXT_ID_ARR = {
            R.string.section_morning,
            R.string.section_afternoon,
            R.string.section_evening
    };

//    private int highlightPosInItems = -1;

    private static final int VIEW_TYPE_SECTION = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onClick(int pos, CourseBean bean);
    }

    public CourseAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SECTION) {
            View contentView = layoutInflater.inflate(R.layout.list_section_course, parent, false);
            return new SectionViewHolder(contentView);
        }
        View contentView = layoutInflater.inflate(R.layout.list_item_course, parent, false);
        return new ItemViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SectionViewHolder) {
            SectionViewHolder viewHolder = (SectionViewHolder) holder;
            int posInSections = posInAll2PosInSections(position);
            viewHolder.ivSection.setImageResource(SECTION_ICON_ID_ARR[posInSections]);
            viewHolder.tvSection.setText(SECTION_TEXT_ID_ARR[posInSections]);
            return;
        }

        ItemViewHolder viewHolder = (ItemViewHolder) holder;
        final int POS_IN_ITEMS = posInAll2PosInItems(position);

        CourseBean courseBean = dataList.get(POS_IN_ITEMS);

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

        viewHolder.viewHighlight.setBackgroundResource(0);
        if (!courseBean.isNoCourseTime()) { // 如果有上下课时间，计算是否需要高亮
            highlight(viewHolder.viewHighlight, courseBean, POS_IN_ITEMS);
        }

        if (onItemClickListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onClick(POS_IN_ITEMS, dataList.get(POS_IN_ITEMS));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        int count = dataList.size();
        count += sectionMorningPosInItems >= 0 ? 1 : 0;
        count += sectionAfternoonPosInItems >= 0 ? 1 : 0;
        count += sectionEveningPosInItems >= 0 ? 1 : 0;
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        int posInSections = posInAll2PosInSections(position);
        return posInSections >= 0 ? VIEW_TYPE_SECTION : VIEW_TYPE_ITEM;
    }

    public CourseBean getItem(int position) {
        if (position >= 0 && position < dataList.size()) {
            return dataList.get(position);
        }
        return null;
    }

    public void refresh(List<CourseBean> dataList) {
        Log.d(C.LOG_TAG, "CourseAdapter - refresh");

        this.dataList.clear();
        if (dataList != null) {
            this.dataList.addAll(dataList);
            int lastInternal = CourseBean.INTERVAL_DAY;
            for (int i = 0, len = dataList.size(); i < len; ++i) {
                CourseBean bean = dataList.get(i);
                if (bean.getInterval() != lastInternal) {
                    switch (bean.getInterval()) {
                        case CourseBean.INTERVAL_MORNING:
                            sectionMorningPosInItems = i;
                            break;
                        case CourseBean.INTERVAL_AFTERNOON:
                            sectionAfternoonPosInItems = i;
                            break;
                        case CourseBean.INTERVAL_EVENING:
                            sectionEveningPosInItems = i;
                    }
                }
                lastInternal = bean.getInterval();
            }
        }
        notifyDataSetChanged();
    }

//    public void updateHighlight() {
//        Log.d(C.LOG_TAG, "CourseAdapter - updateHighlight");
//
//        long time = System.currentTimeMillis();
//        long yesterdayTimeEnd = DateUtil.addDateMillis(dataList
//                .get(dataList.size() - 1).getTimeEnd(), -1); // 昨天最后一节课结束时间
//        for (int i = 0, len = dataList.size(); i < len; ++i) {
//            CourseBean bean = dataList.get(i);
//            if (time >= yesterdayTimeEnd && time < bean.getTimeEnd()
//                    && (i == 0 || time >= dataList.get(i - 1).getTimeEnd())) { // 需要高亮该节课
//                Log.d(C.LOG_TAG, "CourseAdapter - updateHighlight target " + i);
//                notifyItemChanged(posInItems2PosInAll(i));
//                if (highlightPosInItems >= 0 && highlightPosInItems != i) {
//                    notifyItemChanged(posInItems2PosInAll(highlightPosInItems));
//                }
//                break;
//            }
//        }
//    }

    private int posInAll2PosInSections(int posInAll) {
        if (sectionMorningPosInItems >= 0 && posInAll == sectionMorningPosInItems) {
            return 0;
        }
        if (sectionAfternoonPosInItems >= 0 && posInAll - 1 == sectionAfternoonPosInItems) {
            return 1;
        }
        if (sectionEveningPosInItems >= 0 && posInAll - 2 == sectionEveningPosInItems) {
            return 2;
        }
        return -1;
    }

    private int posInAll2PosInItems(int posInAll) {
        int posInSections = posInAll2PosInSections(posInAll);
        if (posInSections >= 0) {
            return -1;
        }
        if (posInAll < sectionMorningPosInItems) {
            return posInAll;
        }
        if (posInAll - 1 < sectionAfternoonPosInItems) {
            return posInAll - 1;
        }
        if (posInAll - 2 < sectionEveningPosInItems) {
            return posInAll - 2;
        }
        return posInAll - 3;
    }

//    private int posInItems2PosInAll(int posInItems) {
//        if (posInItems < sectionMorningPosInItems) {
//            return posInItems;
//        }
//        if (posInItems < sectionAfternoonPosInItems) {
//            return posInItems + 1;
//        }
//        if (posInItems < sectionEveningPosInItems) {
//            return posInItems + 2;
//        }
//        return posInItems + 3;
//    }

    private void highlight(View viewHighlight, CourseBean courseBean, int posInItems) {
        long time = System.currentTimeMillis();
        long yesterdayTimeEnd = DateUtil.addDateMillis(dataList
                .get(dataList.size() - 1).getTimeEnd(), -1); // 昨天最后一节课结束时间
        if (time >= yesterdayTimeEnd && time < courseBean.getTimeEnd()
                && (posInItems == 0 || time >= dataList.get(posInItems - 1).getTimeEnd())) { // 需要高亮该节课
//            highlightPosInItems = posInItems;
            viewHighlight.setBackgroundResource(time >= courseBean.getTimeStart()
                    ? R.drawable.tag_cur : R.drawable.tag_next);
            if (!courseBean.isSleep()) {
                if (time >= courseBean.getTimeStart()) {
                    toastTime(courseBean.getTimeEnd() - time, false);
                } else {
                    toastTime(courseBean.getTimeStart() - time, true);
                }
            }
        }
    }

    private void toastTime(long periodTime, boolean isToBeginOrEnd) {
        Log.d(C.LOG_TAG, "CourseAdapter - toastTime " + isToBeginOrEnd);

        if (periodTime >= 60 * 60 * 1000) {
            return;
        }

        GlobalToast.showToast(context, context.getString(isToBeginOrEnd ? R.string.toast_time_to_class_on
                : R.string.toast_time_to_class_over, ((periodTime - 1) / (60 * 1000) + 1)));
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        View viewHighlight;
        View viewBusy;
        TextView tvTimeStart;
        TextView tvTimeEnd;
        TextView tvName;
        TextView tvRoom;
        TextView tvLecturer;
        View ivMerge;

        ItemViewHolder(View itemView) {
            super(itemView);

            viewHighlight = itemView.findViewById(R.id.view_highlight);
            viewBusy = itemView.findViewById(R.id.ll_busy);
            tvTimeStart = (TextView) itemView.findViewById(R.id.tv_course_time_start);
            tvTimeEnd = (TextView) itemView.findViewById(R.id.tv_course_time_end);
            tvName = (TextView) itemView.findViewById(R.id.tv_course_name);
            tvRoom = (TextView) itemView.findViewById(R.id.tv_course_room);
            tvLecturer = (TextView) itemView.findViewById(R.id.tv_course_lecturer);
            ivMerge = itemView.findViewById(R.id.iv_merge);
        }
    }

    private static class SectionViewHolder extends RecyclerView.ViewHolder {
        ImageView ivSection;
        TextView tvSection;

        SectionViewHolder(View itemView) {
            super(itemView);

            ivSection = (ImageView) itemView.findViewById(R.id.iv_section);
            tvSection = (TextView) itemView.findViewById(R.id.tv_section);
        }
    }
}
