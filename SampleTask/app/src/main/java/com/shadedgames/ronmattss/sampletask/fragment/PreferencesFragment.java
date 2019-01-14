package com.shadedgames.ronmattss.sampletask.fragment;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.text.method.DigitsKeyListener;

import com.shadedgames.ronmattss.sampletask.R;

public class PreferencesFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // construct the preferences screen from the xml config
        addPreferencesFromResource(R.xml.task_preferences);
        // Use the number keyboard when editing the time pref.
        EditTextPreference timeDefault = (EditTextPreference)findPreference(getString(R.string.pref_default_time_from_now_key));
        timeDefault.getEditText().setKeyListener(DigitsKeyListener.getInstance());

    }
}
