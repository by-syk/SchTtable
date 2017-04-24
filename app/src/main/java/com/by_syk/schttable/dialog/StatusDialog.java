package com.by_syk.schttable.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.by_syk.schttable.R;
import com.by_syk.schttable.bean.StatusBean;

/**
 * Created by By_syk on 2016-12-06.
 */

public class StatusDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        StatusBean statusBean = null;
        Bundle bundle = getArguments();
        if (bundle != null) {
            statusBean = (StatusBean) bundle.getSerializable("bean");
        }
        if (statusBean == null) {
            statusBean = new StatusBean();
        }
        String text = getString(R.string.status_error2, statusBean.getCode(), statusBean.getDesc());

        return new AlertDialog.Builder(getActivity())
                .setMessage(text)
                .setPositiveButton(R.string.dlg_bt_close, null)
                .create();
    }

    public static StatusDialog newInstance(StatusBean bean) {
        StatusDialog dialog = new StatusDialog();

        Bundle bundle = new Bundle();
        bundle.putSerializable("bean", bean);
        dialog.setArguments(bundle);

        return dialog;
    }
}
