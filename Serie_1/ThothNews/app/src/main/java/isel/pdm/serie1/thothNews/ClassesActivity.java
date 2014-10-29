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

    private static final int SETTINGS_CLASSES_REQUEST = 0;

    private ListView _listView;
    ArrayList<ThothClass> rowItems;
    ClassesListAdapter cAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_classes);

        settingListAdapter();
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
        if (cAdapter.getCount() == 0)
            executeExtractor();

    }

    @Override
    protected void onPause() {
        super.onPause();
        d("ClassesActivity: onPause()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(new Intent(ClassesActivity.this, PreferencesActivity.class));

            startActivityForResult(intent, SETTINGS_CLASSES_REQUEST);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        d("Entered onActivityResult()");

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SETTINGS_CLASSES_REQUEST:
                    d("SETTINGS_CLASSES_REQUEST");
                    executeExtractor();
                    return;
                default:
            }
        }
    }

    private void executeExtractor() {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> classesIDSelected = sharedPrefs.getStringSet("multi_select_list_key", null);

        new ExtractorThothClasses() {

            @Override
            protected void onPostExecute (List < ThothClass > result) {

                if (result == null || result.size() < 1) {
                    Toast.makeText(getApplicationContext(),
                            "Last News Update Failed", Toast.LENGTH_SHORT).show();
                    return;
                }
                rowItems.clear();

                for (ThothClass thothClass : result)
                    rowItems.add(thothClass);

                cAdapter = new ClassesListAdapter(getApplicationContext(), R.layout.layout_class_item, rowItems);
                _listView.setAdapter(cAdapter);
                cAdapter.notifyDataSetChanged();
                _listView.refreshDrawableState();

                Toast.makeText(getApplicationContext(), "Last News Updated", Toast.LENGTH_SHORT).show();
            }

        }.execute(classesIDSelected);
    }

}
