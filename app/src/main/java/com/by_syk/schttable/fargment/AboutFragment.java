package com.by_syk.schttable.fargment;

import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.text.TextUtils;
import android.view.View;

import com.by_syk.lib.text.AboutMsgRender;
import com.by_syk.schttable.IntroActivity;
import com.by_syk.schttable.R;
import com.by_syk.schttable.bean.ResResBean;
import com.by_syk.schttable.bean.WatchDogBean;
import com.by_syk.schttable.dialog.ApplyDialog;
import com.by_syk.schttable.dialog.PrivacyDialog;
import com.by_syk.schttable.dialog.SchoolsDialog;
import com.by_syk.schttable.util.C;
import com.by_syk.schttable.util.ExtraUtil;
import com.by_syk.schttable.util.RetrofitHelper;
import com.by_syk.schttable.util.impl.ServerService;
import com.stephentuso.welcome.WelcomeHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by By_syk on 2017-02-17.
 */

public class AboutFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    private Preference prefServer;

    private static final String PREFERENCE_WELCOME = "welcome";
    private static final String PREFERENCE_SCHOOLS = "schools";
    private static final String PREFERENCE_APPLY = "apply";
    private static final String PREFERENCE_SERVER = "server";
    private static final String PREFERENCE_CONTACT = "contact";
    private static final String PREFERENCE_DONATE = "donate";
    private static final String PREFERENCE_PRIVACY = "privacy";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        removeUnwantedPadding();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_about);

        init();

        checkServerStatus();
    }

    /**
     * Remove unwanted horizontal padding of android.preference.PreferenceScreen
     * (no necessary for android.support.v7.preference.PreferenceScreen).
     * Keep it before Android 5.0.
     */
    private void removeUnwantedPadding() {
        if (C.SDK < 21) {
            return;
        }

        View view = getView();
        if (view != null) {
            View viewList = view.findViewById(android.R.id.list);
            if (viewList != null) {
                viewList.setPadding(0, 0, 0, 0);
            }
        }
    }

    private void init() {
        Preference prefWelcome = findPreference(PREFERENCE_WELCOME);
        Preference prefSchools = findPreference(PREFERENCE_SCHOOLS);
        Preference prefApply = findPreference(PREFERENCE_APPLY);
        prefServer = findPreference(PREFERENCE_SERVER);
        Preference prefContact = findPreference(PREFERENCE_CONTACT);
        Preference prefDonate = findPreference(PREFERENCE_DONATE);
        Preference prefPrivacy = findPreference(PREFERENCE_PRIVACY);

        prefWelcome.setOnPreferenceClickListener(this);
        prefSchools.setOnPreferenceClickListener(this);
        prefApply.setOnPreferenceClickListener(this);
        prefServer.setOnPreferenceClickListener(this);
        prefContact.setOnPreferenceClickListener(this);
        prefDonate.setOnPreferenceClickListener(this);
        prefPrivacy.setOnPreferenceClickListener(this);

        String summary = AboutMsgRender.parseCode(getString(R.string.preference_summary_contact));
        if (!TextUtils.isEmpty(summary)) {
            prefContact.setSummary(summary);
        }
        summary = AboutMsgRender.parseCode(getString(R.string.preference_summary_donate));
        if (!TextUtils.isEmpty(summary)) {
            prefDonate.setSummary(summary);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case PREFERENCE_WELCOME:
                (new WelcomeHelper(getActivity(), IntroActivity.class)).forceShow();
                break;
            case PREFERENCE_SCHOOLS:
                SchoolsDialog.newInstance(null).show(getFragmentManager(), "schoolsDialog");
                break;
            case PREFERENCE_APPLY:
                (new ApplyDialog()).show(getFragmentManager(), "applyDialog");
                break;
            case PREFERENCE_SERVER:
                checkServerStatus();
                break;
            case PREFERENCE_CONTACT:
                AboutMsgRender.executeCode(getActivity(),
                        getString(R.string.preference_summary_contact));
                break;
            case PREFERENCE_DONATE:
                AboutMsgRender.executeCode(getActivity(),
                        getString(R.string.preference_summary_donate));
                break;
            case PREFERENCE_PRIVACY:
                (new PrivacyDialog()).show(getFragmentManager(), "privacyDialog");
                break;
        }
        return true;
    }

    private void checkServerStatus() {
        prefServer.setSummary(R.string.server_status_checking);

        if (!ExtraUtil.isNetworkConnected(getActivity())) {
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    prefServer.setSummary(R.string.server_status_check_failed);
                }
            }, 200);
            return;
        }

        ServerService service = RetrofitHelper.getInstance().getService(ServerService.class);
        Call<ResResBean<WatchDogBean>> call = service.watchDog();
        call.enqueue(new Callback<ResResBean<WatchDogBean>>() {
            @Override
            public void onResponse(Call<ResResBean<WatchDogBean>> call, Response<ResResBean<WatchDogBean>> response) {
                ResResBean<WatchDogBean> resResBean = response.body();
                if (resResBean == null || !resResBean.isStatusSuccess()) {
                    prefServer.setSummary(R.string.server_status_down);
                    return;
                }
                WatchDogBean bean = resResBean.getResult();
                prefServer.setSummary(getString(R.string.server_status_ok,
                        bean != null ? bean.getPort() : ""));
            }

            @Override
            public void onFailure(Call<ResResBean<WatchDogBean>> call, Throwable t) {
                prefServer.setSummary(R.string.server_status_check_failed);
            }
        });
    }
}