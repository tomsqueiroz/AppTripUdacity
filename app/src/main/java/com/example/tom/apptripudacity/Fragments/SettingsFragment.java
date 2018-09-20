package com.example.tom.apptripudacity.Fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.SwitchPreferenceCompat;

import com.example.tom.apptripudacity.R;

/**
 * Created by tom on 05/09/18.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_apptrip);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen prefScreen = getPreferenceScreen();
        int count = prefScreen.getPreferenceCount();


        for (int i = 0; i < count; i++) {
            Preference p = prefScreen.getPreference(i);
            if(p instanceof EditTextPreference){
                String value = sharedPreferences.getString(p.getKey(), "");
                p.setSummary(value);
            }else if(p instanceof SwitchPreferenceCompat){
                boolean value = sharedPreferences.getBoolean(p.getKey(), true);
                ((SwitchPreferenceCompat) p).setChecked(value);
            }
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if(preference != null){
            if(preference instanceof CheckBoxPreference){

            }else if (preference instanceof EditTextPreference) {
                String value = sharedPreferences.getString(preference.getKey(), "");
                preference.setSummary(value);
            }else if(preference instanceof SwitchPreferenceCompat){
                boolean value = sharedPreferences.getBoolean(preference.getKey(), true);
                ((SwitchPreferenceCompat) preference).setChecked(value);
            }
        }
    }
}
