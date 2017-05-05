package com.by_syk.schttable.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import com.by_syk.lib.toast.GlobalToast;
import com.by_syk.schttable.R;
import com.by_syk.schttable.bean.CourseBean;
import com.by_syk.schttable.util.DateUtil;
import com.by_syk.schttable.util.ExtraUtil;

/**
 * Created by By_syk on 2016-11-16.
 */

public class CourseDialog extends DialogFragment {
    private CourseBean courseBean;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        courseBean = (CourseBean) bundle.getSerializable("bean");

        ViewGroup viewGroup = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.dialog_course, null);

        if (courseBean != null) {
            ((TextView) viewGroup.findViewById(R.id.tv_name)).setText(courseBean.getName());
            ((TextView) viewGroup.findViewById(R.id.tv_room)).setText(courseBean.getRoom());
            ((TextView) viewGroup.findViewById(R.id.tv_lecturer)).setText(courseBean.getLecturer());
            ((TextView) viewGroup.findViewById(R.id.tv_order)).setText(getCourseOrder());
            ((TextView) viewGroup.findViewById(R.id.tv_time))
                    .setText(courseBean.getTimeStartReadable() + " - " + courseBean.getTimeEndReadable());
            ((TextView) viewGroup.findViewById(R.id.tv_date))
                    .setText(DateUtil.getDateStr(courseBean.getTimeStart(), "yyyy-MM-dd E"));
            ((TextView) viewGroup.findViewById(R.id.tv_week))
                    .setText(getString(R.string.course_order_week, getOrderAbbr(courseBean.getWeekOrder())));
        }

        int titleId = courseBean != null && courseBean.isMerge()
                ? R.string.dlg_title_detail_conflict : R.string.dlg_title_detail;

        return new AlertDialog.Builder(getActivity())
                .setTitle(titleId)
                .setView(viewGroup)
                .setPositiveButton(R.string.dlg_bt_got_it, null)
                .setNegativeButton(R.string.dlg_bt_bug, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!ExtraUtil.isNetworkConnected(getActivity())) {
                            GlobalToast.showToast(getActivity(), R.string.toast_no_network);
                            return;
                        }
                        SubmitBugDialog.newInstance(courseBean)
                                .show(getFragmentManager(), "submitBugDialog");
                    }
                })
                .create();
    }

    private String getOrderAbbr(int order) {
        int index = order > 3 ? 3 : (order - 1);
        return String.format(getResources().getStringArray(R.array.days)[index], order);
    }

    private String getCourseOrder() {
        switch (courseBean.getInterval()) {
            case CourseBean.INTERVAL_MORNING:
                return getString(R.string.course_order_morning,
                        getOrderAbbr(courseBean.getCourseOrder()));
            case CourseBean.INTERVAL_AFTERNOON:
                return getString(R.string.course_order_afternoon,
                        getOrderAbbr(courseBean.getIntervalCourseOrder()));
            case CourseBean.INTERVAL_EVENING:
                return getString(R.string.course_order_evening,
                        getOrderAbbr(courseBean.getIntervalCourseOrder()));
        }

        return getString(R.string.course_order, getOrderAbbr(courseBean.getCourseOrder()));
    }

    public static CourseDialog newInstance(CourseBean bean) {
        CourseDialog dialog = new CourseDialog();

        Bundle bundle = new Bundle();
        bundle.putSerializable("bean", bean);
        dialog.setArguments(bundle);

        return dialog;
    }
}
