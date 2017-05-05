package com.by_syk.schttable.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Window;
import android.view.WindowManager;

import com.by_syk.lib.toast.GlobalToast;
import com.by_syk.schttable.MainActivity;
import com.by_syk.schttable.R;
import com.by_syk.schttable.bean.ResResBean;
import com.by_syk.schttable.bean.StatusBean;
import com.by_syk.schttable.util.CourseSQLiteHelper;
import com.by_syk.schttable.util.ExtraUtil;
import com.by_syk.schttable.util.RetrofitHelper;
import com.by_syk.schttable.util.impl.ServerService;

import java.io.IOException;

import retrofit2.Call;

/**
 * Created by By_syk on 2016-11-16.
 */

public class RefreshingDialog extends DialogFragment {
    private String userKey;

    private boolean isExecuted = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setMessage(R.string.refreshing_desc)
                .setPositiveButton(R.string.dlg_bt_cancel, null)
                .create();

        alertDialog.setCanceledOnTouchOutside(false);

        return alertDialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!isExecuted) {
            isExecuted = true;

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

            Window window = getDialog().getWindow();
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }

            Bundle bundle = getArguments();
            if (bundle != null) {
                userKey = bundle.getString("userKey");
            }

            (new RefreshTask()).execute();
        }
    }

    private class RefreshTask extends AsyncTask<String, Integer, StatusBean> {
        private final int TRY_TOTAL_TIMES = 10;
        private final int TRY_PERIOD = 6000;

        @Override
        protected StatusBean doInBackground(String... strings) {
            ServerService service = RetrofitHelper.getInstance().getService(ServerService.class);
            Call<ResResBean<StatusBean>> call = service.refresh(userKey);
            StatusBean statusBean = new StatusBean();
            try {
                ResResBean<StatusBean> resResBean = call.execute().body();
                if (resResBean == null || !resResBean.isStatusSuccess()) {
                    return statusBean;
                }
                statusBean = resResBean.getResult();
                if (statusBean == null) {
                    return new StatusBean();
                }
                int tryTimes = 0;
                while (tryTimes < TRY_TOTAL_TIMES) {
                    if (statusBean.getCode() != StatusBean.STATUS_CODE_WAIT) {
                        break;
                    }
                    SystemClock.sleep(TRY_PERIOD);
                    if (!isAdded()) {
                        break;
                    }
                    call = service.getStatus(statusBean.getId());
                    resResBean = call.execute().body();
                    if (!resResBean.isStatusSuccess()) {
                        return statusBean;
                    }
                    statusBean = resResBean.getResult();
                    ++tryTimes;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (statusBean.getCode() == StatusBean.STATUS_CODE_SUCCESS) { // 服务端数据刷新成功
                // 清空本地缓存
                CourseSQLiteHelper sqLiteHelper = CourseSQLiteHelper.getInstance(getActivity());
                sqLiteHelper.deleteCourses(userKey);
            }

            return statusBean;
        }

        @Override
        protected void onPostExecute(StatusBean statusBean) {
            super.onPostExecute(statusBean);

            if (!isAdded()) {
                return;
            }

            if (statusBean.getCode() == StatusBean.STATUS_CODE_SUCCESS) {
                dismiss();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
            } else {
                dismiss();
                StatusDialog.newInstance(statusBean).show(getFragmentManager(), "statusDialog");
            }
        }
    }
}
