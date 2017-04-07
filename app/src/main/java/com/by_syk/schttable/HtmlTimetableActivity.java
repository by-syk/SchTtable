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

    private class LoadTimetableHtmlTask extends AsyncTask<Boolean, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            webView.loadData(getString(R.string.status_loading), "text/plain;charset=UTF-8", null);

            isRefreshing = true;
        }

        @Override
        protected String doInBackground(Boolean... booleans) {
//            return TimetableTool.getCoursePage(HtmlTimetableActivity.this, sp.getString("userKey"),
//                    booleans[0], ExtraUtil.isNetworkConnected(HtmlTimetableActivity.this));
            // TODO
            return null;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);

            if (data != null) {
//                webView.loadData(data, "text/html", "UTF-8");
                webView.loadData(data, "text/html;charset=UTF-8", null);
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

        CustomTabsIntent.Builder customTabsIntentBuilder = new CustomTabsIntent.Builder();
        if (C.SDK >= 21) {
            customTabsIntentBuilder.setToolbarColor(getResources().getColor(R.color.color_primary));
        }
        customTabsIntentBuilder.build().launchUrl(this, Uri.parse(url));
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
