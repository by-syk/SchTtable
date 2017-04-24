package com.by_syk.schttable.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.by_syk.lib.storage.SP;
import com.by_syk.lib.text.AboutMsgRender;
import com.by_syk.schttable.R;

/**
 * Created by By_syk on 2016-11-04.
 */

public class DonateDialog extends DialogFragment {
    private SP sp;

    private boolean isExecuted = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String text = getString(R.string.donate_desc);
        SpannableString message = AboutMsgRender.render(getActivity(), text);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton(R.string.dlg_bt_ok, null);

        sp = new SP(getActivity(), false);
        if (!sp.getBoolean("donated")) {
            builder.setNegativeButton(R.string.dlg_bt_donated, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    sp.save("donated", true);
                    // 如果是主界面，通知隐藏捐赠菜单项
                    getActivity().invalidateOptionsMenu();
                }
            });
        }

        return builder.create();
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
        }
    }
}
