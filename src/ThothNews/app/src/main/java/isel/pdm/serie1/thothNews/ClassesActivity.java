package isel.pdm.serie1.thothNews;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static isel.pdm.serie1.thothNews.Utils.d;

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
        d("ClassesActivity: onResume()");

        if(cAdapter.getCount() == 0)
            executeExtractor();
    }

    @Override
    protected void onPause() {
        super.onPause();
        d("ClassesActivity: onPause()");
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
        Set<String> classesIDSelected = sharedPrefs.getStringSet("multi_select_list_key", null);

        new ExtractorClasses() {

            @Override
            protected void onPostExecute (List < ThothClass > result) {
                if (result == null || result.size() < 1) {
                    Toast.makeText(
                        getApplicationContext(), "Thoth Connection Failed", Toast.LENGTH_SHORT).show();
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

        }.execute(classesIDSelected);
    }

}
