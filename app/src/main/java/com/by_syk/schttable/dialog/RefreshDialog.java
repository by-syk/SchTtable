package com.by_syk.schttable.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.by_syk.lib.toast.GlobalToast;
import com.by_syk.schttable.R;
import com.by_syk.schttable.util.ExtraUtil;

/**
 * Created by By_syk on 2016-12-02.
 */

public class RefreshDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.refresh_desc)
                .setPositiveButton(R.string.dlg_bt_refresh, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!ExtraUtil.isNetworkConnected(getActivity())) {
                            GlobalToast.showToast(getActivity(), R.string.toast_no_network);
                            return;
                        }

                        RefreshingDialog refreshingDialog = new RefreshingDialog();
                        refreshingDialog.setArguments(getArguments());
                        refreshingDialog.show(getActivity().getFragmentManager(), "refreshingDialog");
                    }
                })
                .create();
    }
}
