package com.by_syk.schttable.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.by_syk.schttable.R;

/**
 * Created by By_syk on 2016-11-16.
 */

public class AboutDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.about_desc)
                .setPositiveButton(R.string.dia_bt_ok, null)
                .create();
    }
}
