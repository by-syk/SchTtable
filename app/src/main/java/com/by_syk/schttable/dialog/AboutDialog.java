package com.by_syk.schttable.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.by_syk.schttable.IntroActivity;
import com.by_syk.schttable.R;
import com.by_syk.schttable.util.C;
import com.stephentuso.welcome.WelcomeHelper;

/**
 * Created by By_syk on 2016-11-16.
 */

public class AboutDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.about_desc)
                .setPositiveButton(R.string.dlg_bt_ok, null)
                .setNegativeButton(R.string.dlg_bt_welcome, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        (new WelcomeHelper(getActivity(), IntroActivity.class)).forceShow();
                    }
                })
                .create();
    }
}
