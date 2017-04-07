package com.by_syk.schttable;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

import com.by_syk.lib.storage.SP;
import com.by_syk.lib.toast.GlobalToast;
import com.by_syk.schttable.bean.ResResBean;
import com.by_syk.schttable.bean.SchoolBean;
import com.by_syk.schttable.util.ExtraUtil;
import com.by_syk.schttable.util.RetrofitHelper;
import com.by_syk.schttable.util.impl.ServerService;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;

/**
 * Created by By_syk on 2016-11-24.
 */

public class HelloActivity extends Activity {
    private View viewTitle;
    private View viewCopyright;

    private boolean isAnimEnd = false;
    private boolean isRunning = true;

    private long startTime = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);

        init();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        viewTitle.postDelayed(new Runnable() {
            @Override
            public void run() {
                anim();
            }
        }, 200);

        (new LoadSchoolsTask()).execute();
    }

    @Override
    protected void onDestroy() {
        isRunning = false;

        super.onDestroy();
    }

    private void init() {
        viewTitle = findViewById(R.id.tv_title);
        viewCopyright = findViewById(R.id.tv_copyright);
    }

    private void anim() {
        viewTitle.setVisibility(View.VISIBLE);
        viewTitle.animate()
                .alpha(1f)
                .setDuration(400)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        viewCopyright.setVisibility(View.VISIBLE);
                        viewCopyright.animate()
                                .alpha(1f)
                                .setDuration(300)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        isAnimEnd = true;
                                    }
                                })
                                .start();
                    }
                })
                .start();
    }

    private class LoadSchoolsTask extends AsyncTask<String, Integer, Bundle> {
        private boolean isNetworkOk = true;

        @Override
        protected Bundle doInBackground(String... strings) {
            isNetworkOk = ExtraUtil.isNetworkConnected(HelloActivity.this);
            if (!isNetworkOk) {
                waitMoment();
                return null;
            }

            ServerService service = RetrofitHelper.getInstance().getService(ServerService.class);
            Call<ResResBean<ArrayList<SchoolBean>>> call = service.getSupportedSchools();
            try {
                ResResBean<ArrayList<SchoolBean>> resResBean = call.execute().body();
                if (!resResBean.isStatusSuccess()) {
                    waitMoment();
                    return null;
                }
                ArrayList<SchoolBean> beanList = resResBean.getResult();
                if (beanList == null) {
                    waitMoment();
                    return null;
                }
                ArrayList<String> schoolList = new ArrayList<>(beanList.size());
                String savedSchoolCode = (new SP(HelloActivity.this, false)).getString("schoolCode");
                int selected = -1;
                for (int i = 0, len = beanList.size(); i < len; ++i) {
                    SchoolBean schoolBean = beanList.get(i);
                    schoolList.add(schoolBean.getName());
                    if (selected < 0 && schoolBean.getCode().equals(savedSchoolCode)) {
                        selected = i;
                    }
                }
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("schoolBeanList", beanList);
                bundle.putStringArrayList("schoolList", schoolList);
                bundle.putInt("selected", selected);
                waitMoment();
                return bundle;
            } catch (IOException e) {
                e.printStackTrace();
            }

            waitMoment();
            return null;
        }

        @Override
        protected void onPostExecute(Bundle result) {
            super.onPostExecute(result);

            if (!isRunning) {
                return;
            }

            if (!isNetworkOk) {
                GlobalToast.showToast(HelloActivity.this, R.string.toast_no_network, true);
            } else if (result == null) {
                GlobalToast.showToast(HelloActivity.this, R.string.toast_error, true);
            }

            Intent intent = new Intent(HelloActivity.this, SignInActivity.class);
            intent.putExtra("data", result);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    private void waitMoment() {
        while (!isAnimEnd && isRunning) {
            SystemClock.sleep(100);
        }
        long time = System.currentTimeMillis() - startTime;
        if (time < 1000) {
            SystemClock.sleep(1000 - time);
        }
    }
}
