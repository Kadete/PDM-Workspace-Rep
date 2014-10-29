package isel.pdm.serie1.thothNews;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static isel.pdm.serie1.thothNews.ClassesListAdapter.*;
import static isel.pdm.serie1.thothNews.Utils.*;

public class NewsActivity extends Activity {

    private ListView _listView;
    ArrayList<ThothClassNewItem> rowItems;
    NewsListAdapter nAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_news);

        d("MainActivity, onCreate Called");

        settingListAdapter();

    }

    protected void settingListAdapter(){

        _listView = (ListView) findViewById(R.id.listView1);
        rowItems = new ArrayList<ThothClassNewItem>();

        Intent intent = getIntent();
        String classId = intent.getStringExtra(TAG_SELECT_CLASS_ID);
        String className = intent.getStringExtra(TAG_SELECT_CLASS_NAME);

        TextView tv_title = (TextView) this.findViewById(R.id.class_news_title);
        tv_title.setText(className);

        new ExtractorThothNews(){
            @Override
            protected void onPostExecute(ArrayList<ThothClassNewItem> result){

                if (result == null || result.size() < 1) {
                    Toast.makeText(getApplicationContext(),
                            "Last News Update Failed", Toast.LENGTH_SHORT).show();
                    return;
                }

                sortRowItemsFromResult(result);

                nAdapter = new NewsListAdapter( getApplicationContext(), R.layout.layout_new_item, rowItems);
                _listView.setAdapter(nAdapter);
                nAdapter.notifyDataSetChanged();
                _listView.refreshDrawableState();

                Toast.makeText(getApplicationContext(), "Last News Updated", Toast.LENGTH_SHORT).show();
            }
        }.execute(classId);

        nAdapter = new NewsListAdapter(NewsActivity.this, R.layout.layout_new_item, rowItems);
        _listView.setAdapter(nAdapter);
    }

    @Override
    protected void onStart(){
        super.onStart();
        ActionBar actionbar = this.getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        Log.d("DEBUG", "MainActivity, onStart Called");
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(_listView != null && _listView.getAdapter() != null){
            d("_listView.getAdapter() != null");

            nAdapter = new NewsListAdapter( getApplicationContext(), R.layout.layout_new_item, rowItems);
            _listView.setAdapter(nAdapter);
            nAdapter.notifyDataSetChanged();
            _listView.refreshDrawableState();
            sortRowItems();

        }else{
            settingListAdapter();
            sortRowItems();
        }

        d("MainActivity, onResume Called");
    }

    @Override
    protected void onPause(){
        super.onPause();
        d("MainActivity, onPause Called");
    }

    @Override
    protected void onStop(){
        super.onStop();
        d("MainActivity, onStop Called");
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        d("MainActivity, onRestart Called");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        d("MainActivity, onDestroy Called");
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
            startActivity(new Intent(NewsActivity.this, PreferencesActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void sortRowItems(){
        if (rowItems == null || rowItems.isEmpty())
            return;

        Collections.sort(rowItems, new Comparator<ThothClassNewItem>() {
            public int compare(ThothClassNewItem tc1, ThothClassNewItem tc2) {
                return tc1.getStatus().compareTo(tc2.getStatus());
            }
        });
    }

    protected void sortRowItemsFromResult(ArrayList<ThothClassNewItem> result){

        List readItems = new ArrayList();

        for(ThothClassNewItem newItem : result){
            if(newItem.getStatus() == ThothClassNewItem.Status.READ)
                readItems.add(newItem);
            else
                rowItems.add(newItem);
        }

        Collections.sort(rowItems, new Comparator<ThothClassNewItem>() {
            public int compare(ThothClassNewItem tc1, ThothClassNewItem tc2) {
                return tc2.getWhen().compareTo(tc1.getWhen());
            }
        });

        Collections.sort(readItems, new Comparator<ThothClassNewItem>() {
            public int compare(ThothClassNewItem tc1, ThothClassNewItem tc2) {
                return tc2.getWhen().compareTo(tc1.getWhen());
            }
        });

        rowItems.addAll(readItems);
    }

}