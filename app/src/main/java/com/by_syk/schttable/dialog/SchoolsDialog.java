package com.by_syk.schttable.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;

import com.by_syk.lib.toast.GlobalToast;
import com.by_syk.schttable.R;
import com.by_syk.schttable.bean.ResResBean;
import com.by_syk.schttable.bean.SchoolBean;
import com.by_syk.schttable.util.ExtraUtil;
import com.by_syk.schttable.util.RetrofitHelper;
import com.by_syk.schttable.util.impl.ServerService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by By_syk on 2016-11-16.
 */

public class SchoolsDialog extends DialogFragment {
    private boolean toLoad = false;

    private boolean isExecuted = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] items = getArguments().getStringArray("data");
        if (items == null) {
            items = new String[]{getString(R.string.status_loading)};
            toLoad = true;
        }
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dlg_title_schools)
                .setItems(items, null)
                .setPositiveButton(R.string.dlg_bt_got_it, null)
                .create();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!isExecuted) {
            isExecuted = true;
            if (toLoad) {
                loadSchools();
            }
        }
    }

    private void loadSchools() {
        if (!ExtraUtil.isNetworkConnected(getActivity())) {
            GlobalToast.showToast(getActivity(), R.string.toast_no_network);
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                }
            }, 400);
            return;
        }

        ServerService service = RetrofitHelper.getInstance().getService(ServerService.class);
        Call<ResResBean<ArrayList<SchoolBean>>> call = service.getSupportedSchools(true);
        call.enqueue(new Callback<ResResBean<ArrayList<SchoolBean>>>() {
            @Override
            public void onResponse(Call<ResResBean<ArrayList<SchoolBean>>> call, Response<ResResBean<ArrayList<SchoolBean>>> response) {
                ResResBean<ArrayList<SchoolBean>> resResBean = response.body();
                if (resResBean == null || !resResBean.isStatusSuccess()) {
                    dismiss();
                    return;
                }
                if (!isAdded()) {
                    return;
                }
                showData(resResBean.getResult());
            }

            @Override
            public void onFailure(Call<ResResBean<ArrayList<SchoolBean>>> call, Throwable t) {
                dismiss();
            }
        });
    }

    private void showData(ArrayList<SchoolBean> dataList) {
        if (dataList == null) {
            dismiss();
            return;
        }
        String[] schools = new String[dataList.size()];
        for (int i = 0, len = dataList.size(); i < len; ++i) {
            SchoolBean bean = dataList.get(i);
            schools[i] = getString(R.string.school_and_user_num,
                    bean.getName(), bean.getUserNum());
        }
        SchoolsDialog.newInstance(schools).show(getFragmentManager(), "schoolsDialog");
        dismiss();
    }

    public static SchoolsDialog newInstance(String[] schools) {
        SchoolsDialog dialog = new SchoolsDialog();

        Bundle bundle = new Bundle();
        bundle.putStringArray("data", schools);
        dialog.setArguments(bundle);

        return dialog;
    }
}
