package isel.pdm.serie1.thothNews;

import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceFragment;
import android.util.Log;

/**
 * Created by Kadete on 14/10/2014.
 */
public class SettingsFragment extends PreferenceFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        ExtractorThothClassesSettings c = new ExtractorThothClassesSettings(){
            @Override
            protected void onPostExecute(ThothClass[] result){
                if(result == null){
                    Log.e("", "ERROR: onPostExecute(..) -> result == null");
                }else{

                    CharSequence[] entries = new CharSequence[result.length], entryValues = new CharSequence[result.length];

                    for(int i = 0 ; i < result.length ; ++i){
                        entries[i] = result[i]._fullname;
                        entryValues[i] = String.valueOf(result[i]._id);
                    }

                    MultiSelectListPreference lp = (MultiSelectListPreference)findPreference("multi_select_list_key");

                    lp.setEntries(entries);
                    lp.setEntryValues(entryValues);

                }
            }

        };
        c.execute();
    }



}

