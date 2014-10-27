package isel.pdm.serie1.anniversaryreminder;

import android.os.Bundle;
import android.preference.PreferenceFragment;


/**
* Created by Kadete on 14/10/2014.
*/
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}



