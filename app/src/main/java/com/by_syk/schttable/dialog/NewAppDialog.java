package com.by_syk.schttable.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import com.by_syk.schttable.R;
import com.by_syk.schttable.bean.AppVerBean;
import com.by_syk.schttable.util.DateUtil;
import com.by_syk.schttable.util.ExtraUtil;

/**
 * Created by By_syk on 2016-11-20.
 */

public class NewAppDialog extends DialogFragment {
    private AppVerBean appVerBean;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        appVerBean = (AppVerBean) getArguments().getSerializable("bean");

        ViewGroup viewGroup = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.dialog_app, null);

        if (appVerBean != null) {
            ((TextView) viewGroup.findViewById(R.id.tv_version)).setText("v" + appVerBean.getVerName());
            ((TextView) viewGroup.findViewById(R.id.tv_desc)).setText(appVerBean.getDesc());
            ((TextView) viewGroup.findViewById(R.id.tv_date))
                    .setText(DateUtil.getDateStr(appVerBean.getDate(), "yyyy-MM-dd"));
            ((TextView) viewGroup.findViewById(R.id.tv_size))
                    .setText(getString(R.string.app_size_desc, ExtraUtil.getReadableFileSize(appVerBean.getApkSize())));
        }

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dlg_title_new_app)
                .setView(viewGroup)
                .setPositiveButton(R.string.dlg_bt_download, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(appVerBean.getUrl()));
                        startActivity(intent);
//                        Bundle bundle = new Bundle();
//                        bundle.putSerializable("bean", appVerBean);
//                        UpdatingDialog updatingDialog = new UpdatingDialog();
//                        updatingDialog.setArguments(bundle);
//                        updatingDialog.show(getFragmentManager(), "updatingDialog");
                    }
                })
                .create();
    }

    public static NewAppDialog newInstance(AppVerBean bean) {
        NewAppDialog dialog = new NewAppDialog();

        Bundle bundle = new Bundle();
        bundle.putSerializable("bean", bean);
        dialog.setArguments(bundle);

        return dialog;
    }
}
