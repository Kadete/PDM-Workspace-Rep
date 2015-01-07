package pt.isel.pdm.grupo17.thothnews.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.activities.ClassesPickActivity;
import pt.isel.pdm.grupo17.thothnews.broadcastreceivers.NetworkReceiver;
import pt.isel.pdm.grupo17.thothnews.services.ThothUpdateService;
import pt.isel.pdm.grupo17.thothnews.utils.ConnectionUtils;
import pt.isel.pdm.grupo17.thothnews.utils.TagUtils;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{
    /** multiSelectListPreference.getEntries(); // list with all items **/
    /** multiSelectListPreference.getValues(); // list with select items **/
    private MultiSelectListPreference multiSelectListPreference;
    private Preference classPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        setPreferenceScreen(null);
        addPreferencesFromResource(R.xml.preferences);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        multiSelectListPreference = (MultiSelectListPreference) findPreference(TagUtils.TAG_MULTILIST_SEMESTERS_PREF_KEY);
        multiSelectListPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                boolean hasValues = ((MultiSelectListPreference)preference).getEntryValues().length > 0;
                if(ConnectionUtils.checkConnection(getActivity(), !hasValues))
                    ThothUpdateService.startActionSemestersUpdate(getActivity());
                if(!hasValues)
                    ((MultiSelectListPreference)preference).getDialog().dismiss();
                return true;
            }
        });

        setSemestersSummary();

        classPreference = findPreference(TagUtils.TAG_PICK_CLASSES_KEY);
        classPreference.setEnabled(!multiSelectListPreference.getValues().isEmpty());
        setClassesSummary(sharedPreferences);
        classPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(getActivity(), ClassesPickActivity.class);
                startActivity(i);
                return true;
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case TagUtils.TAG_LIST_ALL_SEMESTERS:
                setEntriesToMultiSelectListPref(sharedPreferences);
                return;
            case TagUtils.TAG_MULTILIST_SEMESTERS_PREF_KEY:
                setSemestersSummary();
                return;
            case TagUtils.TAG_SELECTED_CLASSES:
                setClassesSummary(sharedPreferences);
                return;
            case TagUtils.TAG_VIBRATION_SWITCH_PREF_KEY:
                ThothUpdateService.isToVibrate = sharedPreferences.getBoolean(TagUtils.TAG_VIBRATION_SWITCH_PREF_KEY, true);
                return;
            case TagUtils.TAG_DATA_MOBILE_SWITCH_PREF_KEY:
                Intent intent = new Intent(NetworkReceiver.ACTION_DATA_MOBILE_CHANGE);
                intent.putExtra(NetworkReceiver.DATA_MOBILE_EXTRA, sharedPreferences.getBoolean(TagUtils.TAG_DATA_MOBILE_SWITCH_PREF_KEY, true));
                getActivity().sendBroadcast(intent);
                return;
            default:
        }
    }

    public void setEntriesToMultiSelectListPref(SharedPreferences sharedPreferences){
        final Set<String> semestersSet = sharedPreferences.getStringSet(TagUtils.TAG_LIST_ALL_SEMESTERS, null);
        if(semestersSet != null && !semestersSet.isEmpty()){
            List<String> semesters = new ArrayList<>(semestersSet);
            Collections.sort(semesters, new Comparator<String>() {
                @Override
                public int compare(String semester1, String semester2) {
                    return semester2.compareTo(semester1);
                }
            });
            CharSequence[] semestersCharSeq = semesters.toArray(new CharSequence[semesters.size()]);
            if(semestersCharSeq.length != 0) {
                multiSelectListPreference.setEntries(semestersCharSeq);
                multiSelectListPreference.setEntryValues(semestersCharSeq);
            }
        }
    }

    private void setSemestersSummary(){
        List<String> semesters = new ArrayList<>(multiSelectListPreference.getValues());
        Collections.sort(semesters, new Comparator<String>() {
            @Override
            public int compare(String semester1, String semester2) {
                return semester2.compareTo(semester1);
            }
        });

        boolean hasSemesterSelected = !semesters.isEmpty();

        String summary = "";
        if (hasSemesterSelected) {
            Iterator<String> iterator = semesters.iterator();
            while (iterator.hasNext()) {
                summary += iterator.next();
                if (iterator.hasNext())
                    summary += ", ";
            }
        } else{
            summary = getResources().getString(R.string.thoth_semesters_summary);
            if(classPreference != null) {
                classPreference.setSummary(getResources().getString(R.string.thoth_classes_summary_no_semester));
            }
        }
        multiSelectListPreference.setSummary(summary);
        if(classPreference != null) {
            classPreference.setEnabled(hasSemesterSelected);
        }
    }

    private void setClassesSummary(SharedPreferences sharedPreferences){
        Set<String> classesSelected = sharedPreferences.getStringSet(TagUtils.TAG_SELECTED_CLASSES, null);
        String summary = (classesSelected != null)
                ? classesSelected.size() + " " + getResources().getString(R.string.thoth_classes_summary_enrolled)
                : getResources().getString(R.string.thoth_classes_summary);
        classPreference.setSummary(summary);
    }

}

