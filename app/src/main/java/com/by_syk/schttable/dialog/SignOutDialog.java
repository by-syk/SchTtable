package com.by_syk.schttable.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.by_syk.lib.storage.SP;
import com.by_syk.schttable.HelloActivity;
import com.by_syk.schttable.R;
import com.by_syk.schttable.util.C;
import com.by_syk.schttable.util.CourseSQLiteHelper;
import com.by_syk.schttable.util.ExtraUtil;

/**
 * Created by By_syk on 2016-11-17.
 */

public class SignOutDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.sign_out_desc)
                .setPositiveButton(R.string.dlg_bt_sign_out, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        signOut();
                    }
                })
                .create();
    }

    private void signOut() {
        SP sp = new SP(getActivity(), false);

        // 清空本地缓存
        CourseSQLiteHelper sqLiteHelper = CourseSQLiteHelper.getInstance(getActivity());
        sqLiteHelper.deleteCourses(sp.getString("userKey"));

        ExtraUtil.clearDir(getActivity().getCacheDir());

        sp.delete("userKey");

        // 发送广播注销成功（如果有小部件则会收到并处理）
        getActivity().sendBroadcast(new Intent(C.ACTION_SIGN_OUT));

        Intent intent = new Intent(getActivity(), HelloActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
