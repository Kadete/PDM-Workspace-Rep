package isel.pdm.serie1.thothNews;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static isel.pdm.serie1.thothNews.Utils.*;

public class ClassesActivity extends Activity {

    private static final int SETTINGS_CLASSES_REQUEST = 0;
//    private static final String FILE_NAME = "classesActivityData.txt";

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

        if (cAdapter.getCount() == 0)
            executeExtractor();

    }

    @Override
    public void onResume() {
        super.onResume();

        d("ClassesActivity: onResume()");
        if (cAdapter.getCount() == 0)
            executeExtractor();
            //loadItems();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //saveItems();
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
                    return;
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

//    private void loadItems() {
//        BufferedReader reader = null;
//        try {
//            FileInputStream fis = openFileInput(FILE_NAME);
//            reader = new BufferedReader(new InputStreamReader(fis));
//
//            String class_id;
//            String fullName;
//            String teacher;
//
//            while (null != (class_id = reader.readLine())) {
//                fullName = reader.readLine();
//                teacher = reader.readLine();
//                cAdapter.add(new ThothClass(Integer.valueOf(class_id), fullName, teacher));
//            }
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (null != reader) {
//                try {
//                    reader.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

//    private void saveItems(List<ThothClass> thothClassList) {
//        PrintWriter writer = null;
//        try {
//            FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
//            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
//                    fos)));
//
//            for(ThothClass thothClass : thothClassList){
//                writer.println(thothClass.GetInfoToStore());
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (null != writer) {
//                writer.close();
//            }
//        }
//    }

}
