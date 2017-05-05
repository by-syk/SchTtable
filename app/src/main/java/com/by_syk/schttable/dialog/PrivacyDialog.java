package com.by_syk.schttable.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;

import com.by_syk.schttable.R;
import com.by_syk.schttable.util.ExtraUtil;

import java.io.IOException;

/**
 * Created by By_syk on 2016-11-16.
 */

public class PrivacyDialog extends DialogFragment {
    private boolean isExecuted = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dlg_title_privacy)
                .setMessage(R.string.loading)
                .setPositiveButton(R.string.dlg_bt_got_it, null)
                .create();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!isExecuted) {
            isExecuted = true;

            (new LoadTextTask()).execute();
        }
    }

    private class LoadTextTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                return ExtraUtil.readFile(getActivity().getAssets().open("privacy.txt"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null) {
                ((AlertDialog) getDialog()).setMessage(s);
            }
        }
    }
}
