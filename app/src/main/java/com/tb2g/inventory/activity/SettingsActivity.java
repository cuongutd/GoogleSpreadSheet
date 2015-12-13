package com.tb2g.inventory.activity;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.tb2g.inventory.R;
import com.tb2g.inventory.util.Constants;

import java.util.List;

public class SettingsActivity extends AppCompatPreferenceActivity implements View.OnClickListener {


    ImageView invimg;
    ImageView tranimg;
    ImageView fullinvimg;
    ImageView fulltranimg;



    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            preference.setSummary(stringValue);
            return true;
        }
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        setContentView(R.layout.activity_settings);
        addPreferencesFromResource(R.xml.pref_general);

        bindPreferenceSummaryToValue(findPreference(Constants.SHARED_PREF_UPCDB_KEY));
        bindPreferenceSummaryToValue(findPreference(Constants.SHARED_PREF_INVENTORY_FILE_NAME));
        bindPreferenceSummaryToValue(findPreference(Constants.SHARED_PREF_TRANSACTION_FILE_NAME));

        invimg = (ImageView)findViewById(R.id.invimg);
        invimg.setOnClickListener(this);
        tranimg = (ImageView)findViewById(R.id.tranimg);
        tranimg.setOnClickListener(this);
        fullinvimg = (ImageView)findViewById(R.id.fullinvimg);
        fullinvimg.setOnClickListener(this);
        fulltranimg = (ImageView)findViewById(R.id.fulltranimg);
        fulltranimg.setOnClickListener(this);

    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName);
    }


    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<PreferenceActivity.Header> target) {
        //loadHeadersFromResource(R.xml.pref_headers, target);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.invimg:
                fullinvimg.setVisibility(View.VISIBLE);
                break;
            case R.id.tranimg:
                fulltranimg.setVisibility(View.VISIBLE);
                break;
            case R.id.fullinvimg:
                fullinvimg.setVisibility(View.INVISIBLE);
                break;
            case R.id.fulltranimg:
                fulltranimg.setVisibility(View.INVISIBLE);
                break;


        }
    }
}
