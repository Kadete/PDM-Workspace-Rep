package pt.isel.pdm.grupo17.thothnews.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.activities.ClassesSelectionActivity;
import pt.isel.pdm.grupo17.thothnews.fragments.dialogs.CleanPreferencesDialogFragment;


public class SettingsFragment extends PreferenceFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        Preference classPreference = findPreference("pick_classes");
        classPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(getActivity(), ClassesSelectionActivity.class);
                startActivity(i);
                return true;
            }
        });

        Preference p = findPreference("reset_classes");
        p.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
            CleanPreferencesDialogFragment dFragment = new CleanPreferencesDialogFragment();
            dFragment.show(getFragmentManager(), "Dialog Fragment");
            return true;
            }
        });
    }
}

