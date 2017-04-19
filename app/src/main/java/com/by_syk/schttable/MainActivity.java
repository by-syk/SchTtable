package com.by_syk.schttable;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.by_syk.lib.storage.SP;
import com.by_syk.schttable.bean.AppVerBean;
import com.by_syk.schttable.bean.FullUserBean;
import com.by_syk.schttable.bean.ResResBean;
import com.by_syk.schttable.dialog.AboutDialog;
import com.by_syk.schttable.dialog.AboutMeDialog;
import com.by_syk.schttable.fargment.DayCoursesFragment;
import com.by_syk.schttable.dialog.DonateDialog;
import com.by_syk.schttable.dialog.NewAppDialog;
import com.by_syk.schttable.dialog.RefreshDialog;
import com.by_syk.schttable.dialog.SignOutDialog;
import com.by_syk.schttable.util.C;
import com.by_syk.schttable.util.DateUtil;
import com.by_syk.schttable.util.RetrofitHelper;
import com.by_syk.schttable.util.impl.ServerService;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends FragmentActivity {
    private SP sp;

    private AppVerBean appVerBean = new AppVerBean();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = new SP(this, false);
        if (!sp.contains("userKey")) {
            startActivity(new Intent(this, HelloActivity.class));
            finish();
            return;
        }

        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);

        init();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // 发送广播通知小部件（如果有）立即刷新数据
        sendBroadcast(new Intent(C.ACTION_APPWIDGET_UPDATE));

        checkAppUpdate();
    }

    private void init() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);

        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(myPagerAdapter);
    }

    private void aboutMe() {
        FullUserBean bean = new FullUserBean();
        bean.setSchoolCode(sp.getString("schoolCode"));
        bean.setSchoolName(sp.getString("schoolName"));
        bean.setAcademy(sp.getString("academy"));
        bean.setMajor(sp.getString("major"));
        bean.setUserName(sp.getString("userName"));
        bean.setStuNo(sp.getString("stuNo"));
        bean.setUserKey(sp.getString("userKey"));
        AboutMeDialog.newInstance(bean).show(getFragmentManager(), "aboutMeDialog");
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
                if (resResBean == null || !resResBean.isStatusSuccess()) {
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
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if (appVerBean != null && appVerBean.isNew(MainActivity.this)) {
            menu.getItem(0).setVisible(true);
        }

        if (!sp.getBoolean("donated")) {
            menu.getItem(4).setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_new_app: {
                Bundle bundle = new Bundle();
                bundle.putSerializable("bean", appVerBean);
                NewAppDialog newAppDialog = new NewAppDialog();
                newAppDialog.setArguments(bundle);
                newAppDialog.show(getFragmentManager(), "newAppDialog");
                return true;
            }
            case R.id.menu_refresh: {
                Bundle bundle = new Bundle();
                bundle.putString("userKey", sp.getString("userKey"));
                RefreshDialog refreshDialog = new RefreshDialog();
                refreshDialog.setArguments(bundle);
                refreshDialog.show(getFragmentManager(), "refreshDialog");
                return true;
            }
            case R.id.menu_html: {
                item.setIntent(new Intent(this, HtmlTimetableActivity.class)
                        .putExtra("backable", true));
                return super.onOptionsItemSelected(item);
            }
            case R.id.menu_about_me:
                aboutMe();
                return true;
            case R.id.menu_donate:
                DonateDialog donateDialog = new DonateDialog();
                donateDialog.show(getFragmentManager(), "donateDialog");
                return true;
            case R.id.menu_about:
                AboutDialog aboutDialog = new AboutDialog();
                aboutDialog.show(getFragmentManager(), "aboutDialog");
                return true;
            case R.id.menu_sign_out:
                SignOutDialog signOutDialog = new SignOutDialog();
                signOutDialog.show(getFragmentManager(), "signOutDialog");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        private final int DAYS = 7;

        private String[] titles;
        private int offset;

        MyPagerAdapter(FragmentManager fm) {
            super(fm);

            titles = getResources().getStringArray(R.array.weekdays);
            offset = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1; // 计算偏移
            titles[offset] = titles[0]; // 今日即显示“今日”
        }

        @Override
        public Fragment getItem(int position) {
            return DayCoursesFragment.newInstance(DateUtil.addDate(position));
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position + offset];
        }

        @Override
        public int getCount() {
            return DAYS;
        }
    }
}
