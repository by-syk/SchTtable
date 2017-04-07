package com.by_syk.schttable.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import com.by_syk.schttable.R;
import com.by_syk.schttable.bean.FullUserBean;
import com.by_syk.schttable.bean.ResResBean;
import com.by_syk.schttable.bean.TermBean;
import com.by_syk.schttable.util.ExtraUtil;
import com.by_syk.schttable.util.RetrofitHelper;
import com.by_syk.schttable.util.impl.ServerService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by By_syk on 2016-11-17.
 */

public class AboutMeDialog extends DialogFragment {
    private TextView tvTerm;
    private TextView tvTagDays;
    private TextView tvDays;

    private FullUserBean bean;

    private boolean isExecuted = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            bean = (FullUserBean) bundle.getSerializable("bean");
        }

        ViewGroup viewGroup = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.dialog_about_me, null);
        if (bean.getSchoolName() != null) {
            ((TextView) viewGroup.findViewById(R.id.tv_school)).setText(bean.getSchoolName());
        }
        if (bean.getAcademy() != null) {
            ((TextView) viewGroup.findViewById(R.id.tv_academy)).setText(bean.getAcademy());
        }
        if (bean.getMajor() != null) {
            ((TextView) viewGroup.findViewById(R.id.tv_major)).setText(bean.getMajor());
        }
        if (bean.getUserName() != null) {
            ((TextView) viewGroup.findViewById(R.id.tv_name)).setText(bean.getUserName());
        }
        if (bean.getStuNo() != null) {
            ((TextView) viewGroup.findViewById(R.id.tv_student_no)).setText(bean.getStuNo());
        }

        tvTerm = (TextView) viewGroup.findViewById(R.id.tv_term);
        tvDays = (TextView) viewGroup.findViewById(R.id.tv_before_vocation);
        tvTagDays = (TextView) viewGroup.findViewById(R.id.tv_tag_before_vocation);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dia_title_about_me)
                .setView(viewGroup)
                .setPositiveButton(R.string.dia_bt_got_it, null)
                .create();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!isExecuted) {
            isExecuted = true;
            loadTermInfo();
        }
    }

    private void loadTermInfo() {
        if (!ExtraUtil.isNetworkConnected(getActivity())) {
            return;
        }

        ServerService service = RetrofitHelper.getInstance().getService(ServerService.class);
        Call<ResResBean<TermBean>> call = service.getTermInfo(bean.getUserKey());
        call.enqueue(new Callback<ResResBean<TermBean>>() {
            @Override
            public void onResponse(Call<ResResBean<TermBean>> call, Response<ResResBean<TermBean>> response) {
                ResResBean<TermBean> resResBean = response.body();
                if (!resResBean.isStatusSuccess()) {
                    return;
                }
                fillTermData(resResBean.getResult());
            }

            @Override
            public void onFailure(Call<ResResBean<TermBean>> call, Throwable t) {}
        });
    }

    private void fillTermData(TermBean bean) {
        if (bean == null || !isAdded()) {
            return;
        }

        if (bean.getTerm() < 10000) {
            return;
        }

        if (bean.getTerm() % 10 == 1) {
            tvTerm.setText(getString(R.string.term1, bean.getTerm() / 10, (bean.getTerm() / 10) + 1));
        } else if (bean.getTerm() % 10 == 2) {
            tvTerm.setText(getString(R.string.term2, bean.getTerm() / 10, (bean.getTerm() / 10) + 1));
        }
        if (bean.getDaysBeforeEnd() > 0) {
            if (bean.getDaysFromStart() >= 0) {
                if (bean.getTerm() % 10 == 1) {
                    tvTagDays.setText(R.string.tag_me_before_winter_vocation);
                } else if (bean.getTerm() % 10 == 2) {
                    tvTagDays.setText(R.string.tag_me_before_summer_vocation);
                }
                tvDays.setText(getString(R.string.days_left, bean.getDaysBeforeEnd()));
            } else {
                tvTagDays.setText(R.string.tag_me_before_term);
                tvDays.setText(getString(R.string.days_left, -bean.getDaysFromStart()));
            }
        }
    }

    public static AboutMeDialog newInstance(FullUserBean bean) {
        AboutMeDialog dialog = new AboutMeDialog();

        Bundle bundle = new Bundle();
        bundle.putSerializable("bean", bean);
        dialog.setArguments(bundle);

        return dialog;
    }
}
