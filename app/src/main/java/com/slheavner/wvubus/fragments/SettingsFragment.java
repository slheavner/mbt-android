package com.slheavner.wvubus.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import com.slheavner.wvubus.R;

/**
 * Created by Sam on 12/18/2015.
 *
 * The settings fragment. Uses support library
 */
public class SettingsFragment extends PreferenceFragmentCompat {
    public static final String TAG = SettingsFragment.class.getSimpleName();

    SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        //add xml
        addPreferencesFromResource(R.xml.preferences);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

    }
}
