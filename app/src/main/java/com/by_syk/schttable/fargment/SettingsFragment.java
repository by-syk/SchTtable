package com.by_syk.schttable.fargment;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.by_syk.schttable.R;
import com.by_syk.schttable.util.C;

/**
 * Created by By_syk on 2016-12-12.
 */

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

//        findPreference("host").setOnPreferenceChangeListener(this);
//        findPreference("host").setDefaultValue(C.DEF_HOST);
//        findPreference("host").setSummary(getPreferenceManager()
//                .getSharedPreferences().getString("host", C.DEF_HOST));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        switch (preference.getKey()) {
            case "host":
                preference.setSummary(o.toString());
                return true;
        }
        return false;
    }
}
