package com.shadedgames.ronmattss.sampletask.activity;

import android.app.Activity;
import android.os.Bundle;

import com.shadedgames.ronmattss.sampletask.fragment.PreferencesFragment;

public class PreferencesActivity extends Activity {
    @Override
    protected  void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferencesFragment()).commit();
    }
}
