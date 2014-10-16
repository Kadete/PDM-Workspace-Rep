package isel.pdm.serie1.thothNews;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceFragment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static isel.pdm.serie1.thothNews.Utils.readAllFrom;

/**
 * Created by Kadete on 14/10/2014.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        //verifica se a lista já foi preenchida
//        if(getPreferenceScreen().findPreference("multi_select_list_key") != null)
//            return;

        ThothClasses c = new ThothClasses(){
            @Override
            protected void onPostExecute(ThothClass[] result){
                if(result == null){
                    Log.e("", "ERROR: onPostExecute(..) -> result == null");
                }else{

                    CharSequence[] entries = new CharSequence[result.length], entryValues = new CharSequence[result.length];

                    for(int i = 0 ; i < result.length ; ++i){
                        entries[i] = result[i].name;
                        entryValues[i] = String.valueOf(result[i].id);
                    }

                    MultiSelectListPreference lp = (MultiSelectListPreference)findPreference("multi_select_list_key");

                    lp.setEntries(entries);
                    lp.setEntryValues(entryValues);
                }
            }
        };
        c.execute();
    }


    class ThothClass {
        public int id;
        public String name;
    }

    class ThothClasses extends AsyncTask<Void,Void,ThothClass[]> {

        @Override
        protected ThothClass[] doInBackground(Void... arg0) {
            try{
                URL url = new URL("http://thoth.cc.e.ipl.pt/api/v1/classes");
                HttpURLConnection c = (HttpURLConnection)url.openConnection();
                try{
                    InputStream is = c.getInputStream();
                    String data = readAllFrom(is);
                    return parseFrom(data);
                } catch (JSONException e) {
                    return null;
                }finally{
                    c.disconnect();
                }
            }catch(IOException e){
                return null;
            }
        }

        private ThothClass[] parseFrom(String s) throws JSONException{
            JSONObject root = new JSONObject(s);
            JSONArray jclasses = root.getJSONArray("classes");
            ThothClass[] classes = new ThothClass[jclasses.length()];
            for(int i = 0 ; i<jclasses.length() ; ++i){
                JSONObject jclass = jclasses.getJSONObject(i);
                ThothClass clazz = new ThothClass();
                clazz.id = jclass.getInt("id");
                clazz.name = jclass.getString("fullName");
                classes[i] = clazz;
            }
            return classes;
        }

    }
}

