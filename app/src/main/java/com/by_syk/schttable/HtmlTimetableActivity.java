package com.by_syk.schttable;

import android.app.ActionBar;
import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.by_syk.lib.storage.SP;
import com.by_syk.lib.toast.GlobalToast;
import com.by_syk.schttable.util.C;
import com.by_syk.schttable.util.ExtraUtil;
import com.by_syk.schttable.util.RetrofitHelper;
import com.by_syk.schttable.util.impl.ServerService;

import java.io.File;
import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by By_syk on 2016-11-14.
 */

public class HtmlTimetableActivity extends Activity {
    private SP sp;

    private WebView webView;

    private boolean isRefreshing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html_timetable);

        init();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        (new LoadTimetableHtmlTask()).execute(false);
    }

    private void init() {
        sp = new SP(this, false);

        if (getIntent().getBooleanExtra("backable", false)) {
            ActionBar actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        webView = (WebView) findViewById(R.id.web_view);
        WebSettings webSettings = webView.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
    }

    private class LoadTimetableHtmlTask extends AsyncTask<Boolean, Integer, File> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            webView.loadData(getString(R.string.status_loading), "text/plain;charset=UTF-8", null);

            isRefreshing = true;
        }

        @Override
        protected File doInBackground(Boolean... booleans) {
            boolean forceRefresh = booleans[0];
            String userKey = sp.getString("userKey");
            File zipFile = new File(getCacheDir(), "timetable_" + userKey + ".zip");
            File timetableDir = new File(getCacheDir(), "timetable_" + userKey);
            File indexHtmlFile = new File(timetableDir, "index.html");
            if (!forceRefresh && indexHtmlFile.exists()) {
                return indexHtmlFile;
            }

            if (!ExtraUtil.isNetworkConnected(HtmlTimetableActivity.this)) {
                return null;
            }

            Call<ResponseBody> call = RetrofitHelper.getInstance().getService(ServerService.class).
                    getCoursePage(userKey);
            try {
                ResponseBody responseBody = call.execute().body();
                boolean ok = RetrofitHelper.downloadFile(responseBody, zipFile);
                if (ok) {
                    ok = ExtraUtil.unzip(zipFile, timetableDir);
                    if (ok && indexHtmlFile.exists()) {
                        return indexHtmlFile;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(File result) {
            super.onPostExecute(result);

            if (result != null) {
//                webView.loadData(result, "text/html", "UTF-8");
                webView.loadUrl(Uri.fromFile(result).toString());
            } else {
                webView.loadData(getString(R.string.status_error), "text/html;charset=UTF-8", null);
            }

            isRefreshing = false;
        }
    }

    private void visitOfficialPage(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        // https://developer.chrome.com/multidevice/android/customtabs

        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                .setShowTitle(true)
                .setToolbarColor(getResources().getColor(R.color.color_primary))
                .setSecondaryToolbarColor(getResources().getColor(R.color.color_primary_dark))
                .build();
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_html_timetable, menu);

        if (TextUtils.isEmpty(sp.getString("eduUrl"))) {
            menu.getItem(1).setEnabled(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_refresh:
                GlobalToast.showToast(HtmlTimetableActivity.this, R.string.toast_refreshing, true);
                if (!isRefreshing) {
                    (new LoadTimetableHtmlTask()).execute(true);
                }
                return true;
            case R.id.menu_official_page:
                visitOfficialPage(sp.getString("eduUrl"));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
