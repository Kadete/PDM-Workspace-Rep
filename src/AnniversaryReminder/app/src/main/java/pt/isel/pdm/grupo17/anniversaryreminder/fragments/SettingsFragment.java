package pt.isel.pdm.grupo17.anniversaryreminder.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import pt.isel.pdm.grupo17.anniversaryreminder.R;


public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}



