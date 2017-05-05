package com.by_syk.schttable.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.widget.EditText;

import com.by_syk.lib.storage.SP;
import com.by_syk.lib.toast.GlobalToast;
import com.by_syk.schttable.R;
import com.by_syk.schttable.bean.CourseBean;
import com.by_syk.schttable.bean.ResResBean;
import com.by_syk.schttable.util.ExtraUtil;
import com.by_syk.schttable.util.RetrofitHelper;
import com.by_syk.schttable.util.impl.ServerService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by By_syk on 2017-05-02.
 */

public class SubmitBugDialog extends DialogFragment {
    private SP sp;

    private EditText etDesc;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        sp = new SP(getActivity(), false);

        ViewGroup viewGroup = (ViewGroup) getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_bug, null);
        etDesc = (EditText) viewGroup.findViewById(R.id.et_desc);
        etDesc.setText(sp.getString("lastBugDesc"));
        etDesc.selectAll();
        etDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                checkContent(editable.toString());
            }
        });

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(viewGroup)
                .setPositiveButton(R.string.dlg_bt_report, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        reportBug();
                    }
                })
                .setNegativeButton(R.string.dlg_bt_cancel, null)
                .create();
        alertDialog.setCanceledOnTouchOutside(false);

        return alertDialog;
    }

    private void reportBug() {
        String desc = etDesc.getText().toString();
        sp.save("lastBugDesc", desc);

        if (!ExtraUtil.isNetworkConnected(getActivity())) {
            GlobalToast.showToast(getActivity(), R.string.toast_bug_report_failed, true);
            return;
        }

        Bundle bundle = getArguments();
        CourseBean courseBean = (CourseBean) bundle.getSerializable("bean");
        String schoolCode = sp.getString("schoolCode");
        String stuNo = sp.getString("stuNo");
        if (courseBean == null || TextUtils.isEmpty(schoolCode) || TextUtils.isEmpty(stuNo)) {
            GlobalToast.showToast(getActivity(), R.string.toast_bug_report_failed, true);
            return;
        }

        GlobalToast.showToast(getActivity(), R.string.toast_bug_reporting, true);

        ServerService service = RetrofitHelper.getInstance().getService(ServerService.class);
        Call<ResResBean> call = service.reportBug(schoolCode, stuNo,
                courseBean.getTimeStart(), courseBean.getCourseOrder(), desc);
        call.enqueue(new Callback<ResResBean>() {
            @Override
            public void onResponse(Call<ResResBean> call, Response<ResResBean> response) {}

            @Override
            public void onFailure(Call<ResResBean> call, Throwable t) {}
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        checkContent(etDesc.getText().toString());
    }

    private void checkContent(String content) {
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE)
                .setEnabled(content != null && content.trim().length() > 0);
    }

    public static SubmitBugDialog newInstance(CourseBean bean) {
        SubmitBugDialog dialog = new SubmitBugDialog();

        Bundle bundle = new Bundle();
        bundle.putSerializable("bean", bean);
        dialog.setArguments(bundle);

        return dialog;
    }
}
