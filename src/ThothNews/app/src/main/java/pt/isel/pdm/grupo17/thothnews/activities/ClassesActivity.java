package pt.isel.pdm.grupo17.thothnews.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import pt.isel.pdm.grupo17.thothnews.adapters.ClassesListAdapter;
import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.models.ThothClass;
import pt.isel.pdm.grupo17.thothnews.utils.ParseUtils;

import static pt.isel.pdm.grupo17.thothnews.utils.ParseUtils.*;
import static pt.isel.pdm.grupo17.thothnews.utils.ParseUtils.TAG_ACTIVITY;
import static pt.isel.pdm.grupo17.thothnews.utils.ParseUtils.d;
import static pt.isel.pdm.grupo17.thothnews.utils.ParseUtils.readAllFrom;

public class ClassesActivity extends Activity {

    private ListView _listView;
    ArrayList<ThothClass> rowItems;
    ClassesListAdapter cAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_classes_list);
        settingListAdapter();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        final Set<String> classesIDSelected = sharedPrefs.getStringSet("multi_select_list_key", null);

        if (classesIDSelected == null || classesIDSelected.isEmpty()) {
            startActivity(new Intent(ClassesActivity.this, PreferencesActivity.class));
            finish();
        }
    }

    protected void settingListAdapter() {

        _listView = (ListView) findViewById(R.id.listView1);
        rowItems = new ArrayList<ThothClass>();

        cAdapter = new ClassesListAdapter(ClassesActivity.this, R.layout.layout_class_item, rowItems);
        _listView.setAdapter(cAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        d(TAG_ACTIVITY, "ClassesActivity: onResume()");

        if(cAdapter.getCount() == 0)
            executeExtractor();
    }

    @Override
    protected void onPause() {
        super.onPause();
        d(TAG_ACTIVITY, "ClassesActivity: onPause()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_classes_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity( new Intent(new Intent(ClassesActivity.this, PreferencesActivity.class)));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void executeExtractor() {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Object arrayList[] = sharedPrefs.getStringSet("multi_select_list_key", null).toArray();

        new ExtractorClasses() {

            @Override
            protected void onPostExecute (List < ThothClass > result) {
                if (result == null || result.size() < 1) {
                    Toast.makeText(
                        getApplicationContext(), getString(R.string.thoth_connection_failed_toast), Toast.LENGTH_SHORT).show();
                    return;
                }
                rowItems.clear();

                for (ThothClass thothClass : result)
                    rowItems.add(thothClass);

                cAdapter = new ClassesListAdapter(getApplicationContext(), R.layout.layout_class_item, rowItems);
                _listView.setAdapter(cAdapter);
                cAdapter.notifyDataSetChanged();
                _listView.refreshDrawableState();
            }

        }.execute(arrayList);
    }

}

class ExtractorClasses extends AsyncTask< Object[], Void, List<ThothClass>> {

    @Override
    protected List<ThothClass> doInBackground(Object[]... sets) {

        try {
            Object[] aux = sets[0];
            List<ThothClass> newItems = new LinkedList<ThothClass>();
            URL url;

            if(aux == null){
                return null;
            }

            for(int i=0; i < aux.length; i++){
                url = new URL("http://thoth.cc.e.ipl.pt/api/v1/classes/" + aux[i]);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                try {
                    InputStream is = c.getInputStream();
                    String data = readAllFrom(is);
                    newItems.add(parseFrom(data));

                } catch (JSONException e) {
                    d(TAG_ASYNC_TASK, e.getMessage());
                    return null;
                } finally {
                    c.disconnect();
                }
            }
            return newItems;
        } catch (IOException e) {
            e(TAG_ASYNC_TASK, e.getMessage());
            return null;
        }
    }
    private ThothClass parseFrom(String s) throws JSONException {
        JSONObject root = new JSONObject(s);

        return new ThothClass(
                root.getInt("id"),
                root.getString("fullName"),
                root.getString("mainTeacherShortName")
        );
    }
}
