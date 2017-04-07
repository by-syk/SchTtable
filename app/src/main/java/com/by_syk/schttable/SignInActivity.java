package com.by_syk.schttable;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.by_syk.lib.storage.SP;
import com.by_syk.lib.text.AboutMsgRender;
import com.by_syk.lib.toast.GlobalToast;
import com.by_syk.schttable.bean.AppVerBean;
import com.by_syk.schttable.bean.ResResBean;
import com.by_syk.schttable.bean.SchoolBean;
import com.by_syk.schttable.bean.StatusBean;
import com.by_syk.schttable.bean.UserBean;
import com.by_syk.schttable.dialog.AboutDialog;
import com.by_syk.schttable.dialog.ApplyDialog;
import com.by_syk.schttable.dialog.DonateDialog;
import com.by_syk.schttable.dialog.NewAppDialog;
import com.by_syk.schttable.dialog.StatusDialog;
import com.by_syk.schttable.util.AccountInputFilter;
import com.by_syk.schttable.util.C;
import com.by_syk.schttable.util.RetrofitHelper;
import com.by_syk.schttable.util.impl.ServerService;
import com.by_syk.schttable.util.net.NetEncryptUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by By_syk on 2016-11-14.
 */

public class SignInActivity extends Activity {
    private SP sp;

    private Spinner spinnerSchools;
    private EditText etStuNo;
    private EditText etPassword;
    private Button btSignIn;
    private TextView tvVisitOfficial;

    @NonNull
    private List<SchoolBean> schoolBeanList = new ArrayList<>();
    @NonNull
    private List<String> schoolList = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapterSchools;

    private AppVerBean appVerBean;

    private boolean isRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        init();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        checkAppUpdate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        isRunning = false;
    }

    private void init() {
        sp = new SP(this, false);

        spinnerSchools = (Spinner) findViewById(R.id.spinner_schools);
        spinnerSchools.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(C.LOG_TAG, "spinnerSchools - onItemSelected " + i);

                resetInput(schoolBeanList.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        arrayAdapterSchools = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, schoolList);
        spinnerSchools.setAdapter(arrayAdapterSchools);

        etStuNo = (EditText) findViewById(R.id.et_stu_no);
        etPassword = (EditText) findViewById(R.id.et_password);
        btSignIn = (Button) findViewById(R.id.bt_sign_in);

        tvVisitOfficial = (TextView) findViewById(R.id.tv_visit_official);
        tvVisitOfficial.setMovementMethod(LinkMovementMethod.getInstance());

        extractData();
    }

    private void extractData() {
        Bundle bundle = getIntent().getBundleExtra("data");
        if (bundle == null) {
            return;
        }
        List<SchoolBean> schoolBeanList = bundle.getParcelableArrayList("schoolBeanList");
        List<String> schoolList = bundle.getStringArrayList("schoolList");
        int selectedIndex = bundle.getInt("selected", 0);
        if (schoolBeanList == null || schoolList == null) {
            return;
        }
        this.schoolBeanList = schoolBeanList;
        this.schoolList.clear();
        this.schoolList.addAll(schoolList);
        arrayAdapterSchools.notifyDataSetChanged();
        spinnerSchools.setSelection(selectedIndex);
    }

    public void onSignInClick(View view) {
        switch (view.getId()) {
            case R.id.bt_sign_in:
                signIn();
        }
    }

    private void signIn() {
        if (schoolList.isEmpty()) {
            GlobalToast.showToast(this, R.string.toast_no_school);
            return;
        }
        if (etStuNo.length() == 0) {
            etStuNo.setError(getString(R.string.error_empty_student_no));
            etStuNo.requestFocus();
            return;
        }
        if (etPassword.length() == 0) {
            etPassword.setError(getString(R.string.error_empty_password));
            etPassword.requestFocus();
            return;
        }

        String stuNo = etStuNo.getText().toString().trim();
        String pwd = etPassword.getText().toString().trim();

        (new SignInTask()).execute(String.valueOf(spinnerSchools
                .getSelectedItemPosition()), stuNo, pwd);
    }

    private void resetInput(SchoolBean schoolBean) {
        if (schoolBean == null) {
            return;
        }

        etStuNo.setError(null);
        etPassword.setError(null);

        if (TextUtils.isEmpty(schoolBean.getStuNoRegex())) {
            etStuNo.setInputType(InputType.TYPE_CLASS_NUMBER);
            etStuNo.setFilters(new InputFilter[]{new InputFilter
                    .LengthFilter(schoolBean.getStuNoLen())});
        } else {
            etStuNo.setInputType(InputType.TYPE_CLASS_TEXT);
            etStuNo.setFilters(new InputFilter[]{new InputFilter
                    .LengthFilter(schoolBean.getStuNoLen()),
                    new AccountInputFilter(schoolBean.getStuNoRegex())});
        }

//                if (schoolBean.getCode().equals(sp.getString("schoolCode"))) {
//                    etStuNo.setText(sp.getString("stuNo"));
//                } else {
//                    etStuNo.setText("");
//                }
//
//                etPassword.setText("");
        // TODO DEBUG
        String stuNo = sp.getString("stuNo-" + schoolBean.getCode());
        if (!TextUtils.isEmpty(stuNo)) {
            etStuNo.setText(stuNo);
            String password = NetEncryptUtil.decrypt(sp.getString("pwd-" + stuNo));
            if (password.length() > stuNo.length()) {
                etPassword.setText(password.substring(stuNo.length()));
            }
        } else {
            etStuNo.setText("");
            etPassword.setText("");
        }

        // 显示访问官网的链接
        String linkText = getString(R.string.visit_official_page, schoolBean.getUrl());
        tvVisitOfficial.setText(AboutMsgRender.render(SignInActivity.this, linkText));
        tvVisitOfficial.setVisibility(View.VISIBLE);
    }

    private void checkAppUpdate() {
        String verName = null;
        int verCode = 0;
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            verName = packageInfo.versionName;
            verCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        ServerService service = RetrofitHelper.getInstance().getService(ServerService.class);
        Call<ResResBean<AppVerBean>> call = service.getLatestAppInfo(sp.getString("userKey"),
                Build.BRAND, Build.MODEL, C.SDK, verName, verCode);
        call.enqueue(new Callback<ResResBean<AppVerBean>>() {
            @Override
            public void onResponse(Call<ResResBean<AppVerBean>> call, Response<ResResBean<AppVerBean>> response) {
                ResResBean<AppVerBean> resResBean = response.body();
                if (!resResBean.isStatusSuccess()) {
                    return;
                }
                appVerBean = resResBean.getResult();
                if (appVerBean == null) {
                    return;
                }
                invalidateOptionsMenu();
            }

            @Override
            public void onFailure(Call<ResResBean<AppVerBean>> call, Throwable t) {}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sign_in, menu);

        if (appVerBean != null && appVerBean.isNew(this)) {
            menu.getItem(0).setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_new_app:
                NewAppDialog.newInstance(appVerBean)
                        .show(getFragmentManager(), "newAppDialog");
                return true;
            case R.id.menu_apply:
                ApplyDialog.newInstance(schoolBeanList.size())
                        .show(getFragmentManager(), "applyDialog");
                return true;
            case R.id.menu_about:
                AboutDialog aboutDialog = new AboutDialog();
                aboutDialog.show(getFragmentManager(), "aboutDialog");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class SignInTask extends AsyncTask<String, Integer, StatusBean> {
        private ProgressDialog progressDialog;

        private final int TRY_TOTAL_TIMES = 10;
        private final int TRY_PERIOD = 6000;

        private boolean reqCancel = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            btSignIn.setEnabled(false);

            progressDialog = new ProgressDialog(SignInActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getString(R.string.logining_desc));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setButton(ProgressDialog.BUTTON_POSITIVE,
                    getString(R.string.dia_bt_cancel),
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    GlobalToast.showToast(SignInActivity.this, R.string.toast_cancel_login);
                }
            });
            progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    reqCancel = true;
                }
            });
            progressDialog.show();

            Window window = progressDialog.getWindow();
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }

        @Override
        protected StatusBean doInBackground(String... strings) {
            SchoolBean schoolBean = schoolBeanList.get(Integer.parseInt(strings[0]));
            String stuNo = strings[1];
            String pwd = strings[2];
            String enStuNo = NetEncryptUtil.encrypt(stuNo);
            String enPwd = NetEncryptUtil.encrypt(stuNo + pwd);

            ServerService service = RetrofitHelper.getInstance().getService(ServerService.class);

            StatusBean statusBean = new StatusBean();
            try {
                statusBean = signIn(service, schoolBean.getCode(), enStuNo, enPwd);
                if (statusBean.getCode() != StatusBean.STATUS_CODE_SUCCESS) {
                    return statusBean;
                }
                Call<ResResBean<UserBean>> call = service.getUserInfo(schoolBean.getCode(), enStuNo, enPwd);
                ResResBean<UserBean> resResBean = call.execute().body();
                if (!resResBean.isStatusSuccess()) {
                    return statusBean;
                }
                UserBean userBean = resResBean.getResult();
                if (userBean == null || userBean.getUserKey() == null || userBean.getUserName() == null) {
                    return statusBean;
                }
                sp.put("userKey", userBean.getUserKey()).put("schoolName", schoolBean.getName())
                        .put("schoolCode", schoolBean.getCode())
                        .put("eduUrl", schoolBean.getUrl())
                        .put("academy", userBean.getAcademy()).put("major", userBean.getMajor())
                        .put("userName", userBean.getUserName()).put("stuNo", stuNo).save();

                // TODO DEBUG
                sp.put("stuNo-" + schoolBean.getCode(), stuNo)
                        .put("pwd-" + stuNo, enPwd).save();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return statusBean;
        }

        @Override
        protected void onPostExecute(@NonNull StatusBean statusBean) {
            super.onPostExecute(statusBean);

            if (!isRunning) {
                return;
            }

            btSignIn.setEnabled(true);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            if (reqCancel) { // 已手动取消
                return;
            }

            if (statusBean.getCode() != StatusBean.STATUS_CODE_SUCCESS) {
                StatusDialog.newInstance(statusBean).show(getFragmentManager(), "statusDialog");
                return;
            }

            GlobalToast.showToast(SignInActivity.this, R.string.toast_welcome);

            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        @NonNull
        private StatusBean signIn(@NonNull ServerService serverService,
                                  String schoolCode, String enStuNo, String enPwd) throws IOException {
            Call<ResResBean<StatusBean>> call = serverService.signIn(schoolCode, enStuNo, enPwd);
            ResResBean<StatusBean> resResBean = call.execute().body();
            if (!resResBean.isStatusSuccess()) {
                return new StatusBean();
            }
            StatusBean statusBean = resResBean.getResult();
            int tryTimes = 0;
            while (tryTimes < TRY_TOTAL_TIMES) {
                if (statusBean == null) {
                    return new StatusBean();
                }
                if (statusBean.getCode() != StatusBean.STATUS_CODE_WAIT) {
                    break;
                }
                SystemClock.sleep(TRY_PERIOD);
                if (!isRunning || reqCancel) {
                    break;
                }
                call = serverService.getStatus(statusBean.getId());
                resResBean = call.execute().body();
                if (!resResBean.isStatusSuccess()) {
                    return statusBean;
                }
                statusBean = resResBean.getResult();
                ++tryTimes;
            }
            return statusBean;
        }
    }
}