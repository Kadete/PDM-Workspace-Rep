package isel.pdm.serie1.thothNews.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import isel.pdm.serie1.thothNews.R;
import isel.pdm.serie1.thothNews.model.ThothClass;

import static isel.pdm.serie1.thothNews.utils.Utils.readAllFrom;


public class SettingsFragment extends PreferenceFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        ExtractorClassesSettings c = new ExtractorClassesSettings(){
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
        Preference p = findPreference("reset_classes");
        p.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialogFragment dFragment = new AlertDialogFragment();
                dFragment.show(getFragmentManager(), "Dialog Fragment");
                return true;
            }
        });
    }
}

class ExtractorClassesSettings extends AsyncTask<Void ,Void,ThothClass[]> {

    @Override
    protected ThothClass[] doInBackground(Void... arg0) {
        try {
            URL url = new URL("http://thoth.cc.e.ipl.pt/api/v1/classes/");

            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            try {
                InputStream is = c.getInputStream();
                String data = readAllFrom(is);
                return parseFrom(data);
            } catch (JSONException e) {
                return null;
            } finally {
                c.disconnect();
            }
        } catch (IOException e) {
            return null;
        }
    }

    private ThothClass[] parseFrom(String s) throws JSONException {
        JSONObject root = new JSONObject(s);
        JSONArray jclasses = root.getJSONArray("classes");
        ThothClass[] classes = new ThothClass[jclasses.length()];
        for (int i = 0; i < jclasses.length(); ++i) {
            JSONObject jclass = jclasses.getJSONObject(i);
            ThothClass clazz = new ThothClass();
            clazz._id = jclass.getInt("id");
            clazz._fullname = jclass.getString("fullName");
            classes[i] = clazz;
        }
        return classes;
    }
}

