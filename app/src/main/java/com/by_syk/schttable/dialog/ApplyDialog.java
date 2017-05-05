package com.by_syk.schttable.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.StrikethroughSpan;
import android.widget.TextView;

import com.by_syk.lib.text.AboutMsgRender;
import com.by_syk.schttable.R;
import com.by_syk.schttable.bean.ResResBean;
import com.by_syk.schttable.bean.SchoolTodoBean;
import com.by_syk.schttable.util.ExtraUtil;
import com.by_syk.schttable.util.RetrofitHelper;
import com.by_syk.schttable.util.impl.ServerService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by By_syk on 2016-12-02.
 */

public class ApplyDialog extends DialogFragment {
    private boolean isExecuted = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String text = getString(R.string.apply_school_desc, "N", "...");
        SpannableString message = AboutMsgRender.render(getActivity(), text);

        return new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton(R.string.dlg_bt_send_email, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ExtraUtil.sendEmail(getActivity(), getString(R.string.my_email),
                                getString(R.string.email_subject_apply));
                    }
                }).create();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!isExecuted) {
            isExecuted = true;
            // 使内容中的链接可以被点击
            TextView tvMessage = (TextView) getDialog().findViewById(android.R.id.message);
            if (tvMessage != null) {
                tvMessage.setMovementMethod(LinkMovementMethod.getInstance());
            }

            loadApplicationInfo();
        }
    }

    private void loadApplicationInfo() {
        if (!ExtraUtil.isNetworkConnected(getActivity())) {
            return;
        }

        ServerService service = RetrofitHelper.getInstance().getService(ServerService.class);
        Call<ResResBean<List<SchoolTodoBean>>> call = service.getTodoSchools();
        call.enqueue(new Callback<ResResBean<List<SchoolTodoBean>>>() {
            @Override
            public void onResponse(Call<ResResBean<List<SchoolTodoBean>>> call, Response<ResResBean<List<SchoolTodoBean>>> response) {
                ResResBean<List<SchoolTodoBean>> resResBean = response.body();
                if (resResBean == null || !resResBean.isStatusSuccess()) {
                    return;
                }
                if (!isAdded()) {
                    return;
                }
                fillApplicationData(resResBean.getResult());
            }

            @Override
            public void onFailure(Call<ResResBean<List<SchoolTodoBean>>> call, Throwable t) {}
        });
    }

    private void fillApplicationData(List<SchoolTodoBean> beanList) {
        if (beanList == null || beanList.isEmpty()) {
            return;
        }

        StringBuilder sbSchools = new StringBuilder();
        String split = getString(R.string.char_split);
        for (SchoolTodoBean bean : beanList) {
            sbSchools.append(bean.getName()).append(split);
        }
        if (sbSchools.length() > 0) {
            sbSchools.setLength(sbSchools.length() - split.length());
        }
        String text = getString(R.string.apply_school_desc,
                String.valueOf(beanList.size()), sbSchools);
        SpannableString message = AboutMsgRender.render(getActivity(), text);
        for (SchoolTodoBean bean : beanList) {
            if (bean.isDeprecated() && !bean.getName().isEmpty()) {
                int index = text.indexOf(bean.getName());
                message.setSpan(new StrikethroughSpan(), index, index + bean.getName().length(),
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }
        ((AlertDialog) getDialog()).setMessage(message);
    }
}
