package com.by_syk.schttable.fargment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.by_syk.lib.storage.SP;
import com.by_syk.schttable.R;
import com.by_syk.schttable.bean.CourseBean;
import com.by_syk.schttable.bean.ResResBean;
import com.by_syk.schttable.dialog.CourseDialog;
import com.by_syk.schttable.util.C;
import com.by_syk.schttable.util.CourseSQLiteHelper;
import com.by_syk.schttable.util.DateUtil;
import com.by_syk.schttable.util.ExtraUtil;
import com.by_syk.schttable.util.RetrofitHelper;
import com.by_syk.schttable.util.adapter.CourseAdapter;
import com.by_syk.schttable.util.impl.ServerService;
import com.by_syk.schttable.widget.DividerItemDecoration;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import retrofit2.Call;

/**
 * Created by By_syk on 2016-11-15.
 */

public class DayCoursesFragment extends Fragment {
    private SP sp;

    private View contentView;
    private TextView tvHint;
    private RecyclerView recyclerView;

    private CourseAdapter adapter;

    private long dateMillis;

    private CourseSQLiteHelper sqLiteHelper;

    private boolean isTaskRunning = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        sp = new SP(context, false);

        sqLiteHelper = CourseSQLiteHelper.getInstance(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater.inflate(R.layout.fargment_course, container, false);

            init();

            (new LoadTimetableTask()).execute();
        }

        return contentView;
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//
//        if (getUserVisibleHint() && adapter != null && adapter.getItemCount() > 0) {
//            adapter.updateHighlight();
//        }
//    }

    private void init() {
        tvHint = (TextView) contentView.findViewById(R.id.tv_hint);
        tvHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isTaskRunning) {
                    Log.d(C.LOG_TAG, "Tap to reload");
                    (new LoadTimetableTask()).execute();
                }
            }
        });

        recyclerView = (RecyclerView) contentView.findViewById(R.id.rv_courses);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        adapter = new CourseAdapter(getActivity());
        adapter.setOnItemClickListener(new CourseAdapter.OnItemClickListener() {
            @Override
            public void onClick(int pos, CourseBean bean) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("bean", adapter.getItem(pos));
                CourseDialog courseDialog = new CourseDialog();
                courseDialog.setArguments(bundle);
                courseDialog.show(getActivity().getFragmentManager(), "courseDialog");
            }
        });
        recyclerView.setAdapter(adapter);

        Bundle bundle = getArguments();
        if (bundle != null) {
            dateMillis = bundle.getLong("date");
        }
    }

    public static DayCoursesFragment newInstance(Date date) {
        DayCoursesFragment dayCoursesFragment = new DayCoursesFragment();

        Bundle bundle = new Bundle();
        bundle.putLong("date", DateUtil.toDayDate(date).getTime());
        dayCoursesFragment.setArguments(bundle);

        return dayCoursesFragment;
    }

    private class LoadTimetableTask extends AsyncTask<String, Integer, List<CourseBean>> {
        private boolean isNetworkOk;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            isTaskRunning = true;

            tvHint.setText(R.string.status_loading);
        }

        @Override
        protected List<CourseBean> doInBackground(String... strings) {
            isNetworkOk = ExtraUtil.isNetworkConnected(getActivity());

            String userKey = sp.getString("userKey");
            List<CourseBean> list = sqLiteHelper.getCourses(userKey, dateMillis);
            if (list == null || list.isEmpty()) {
                if (isNetworkOk) {
                    list = getDataList(userKey, dateMillis);
                    sqLiteHelper.saveDayCourses(userKey, list);
                } else {
                    list = null;
                }
            }

            return list;
        }

        @Override
        protected void onPostExecute(List<CourseBean> list) {
            super.onPostExecute(list);

            isTaskRunning = false;

            if (!isAdded()) { // 已退出
                return;
            }

            if (list == null) {
                if (isNetworkOk) {
                    tvHint.setText(R.string.status_error);
                } else {
                    tvHint.setText(R.string.status_no_network);
                }
            } else if (list.isEmpty()) {
                tvHint.setText(R.string.status_empty);
            } else {
                tvHint.setVisibility(View.GONE);
                adapter.refresh(list);
            }
        }

        private List<CourseBean> getDataList(String userKey, long dateMillis) {
            ServerService service = RetrofitHelper.getInstance().getService(ServerService.class);
            Call<ResResBean<List<CourseBean>>> call = service.getDayCourses(userKey, dateMillis);
            try {
                ResResBean<List<CourseBean>> resResBean = call.execute().body();
                if (resResBean != null && resResBean.isStatusSuccess()) {
                    return resResBean.getResult();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
