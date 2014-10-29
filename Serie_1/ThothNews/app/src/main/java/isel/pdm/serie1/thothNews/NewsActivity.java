package isel.pdm.serie1.thothNews;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

import static isel.pdm.serie1.thothNews.ClassesListAdapter.TAG_SELECT_CLASS_ID;
import static isel.pdm.serie1.thothNews.ClassesListAdapter.TAG_SELECT_CLASS_NAME;
import static isel.pdm.serie1.thothNews.ThothClassNewListItem.Status;
import static isel.pdm.serie1.thothNews.Utils.*;
import static isel.pdm.serie1.thothNews.Utils.d;

public class NewsActivity extends Activity {

    private ListView _listView;
    ArrayList<ThothClassNewListItem> rowItems;
    NewsListAdapter nAdapter;

    static String classId;
    private static String FILE_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_news);

        d("MainActivity, onCreate Called");

        settingListAdapter();
        loadItems();

        if(nAdapter.getCount()== 0){
            extractThothNews();
        }

    }

    protected void settingListAdapter(){

        _listView = (ListView) findViewById(R.id.listView1);
        rowItems = new ArrayList<ThothClassNewListItem>();

        Intent intent = getIntent();
        classId = intent.getStringExtra(TAG_SELECT_CLASS_ID);
        String className = intent.getStringExtra(TAG_SELECT_CLASS_NAME);
        FILE_NAME= "NewsActivityData" + classId + ".txt";

        TextView tv_title = (TextView) this.findViewById(R.id.class_news_title);
        tv_title.setText(className);

        nAdapter = new NewsListAdapter(NewsActivity.this, R.layout.layout_new_item, rowItems);
        _listView.setAdapter(nAdapter);
    }

    private void extractThothNews(){
        new ExtractorThothNews(){
            @Override
            protected void onPostExecute(ArrayList<ThothClassNewListItem> result){

                if (result == null || result.size() < 1) {
                    Toast.makeText(getApplicationContext(),
                            "Last News Update Failed", Toast.LENGTH_SHORT).show();
                    return;
                }

                rowItems.clear();
                rowItems.addAll(result);

                sortAdapter();

                nAdapter.notifyDataSetChanged();

                _listView.setAdapter(nAdapter);
                _listView.refreshDrawableState();

                Toast.makeText(getApplicationContext(), "Last News Updated", Toast.LENGTH_SHORT).show();
            }
        }.execute(classId);
    }

    @Override
    protected void onStart(){
        super.onStart();
        ActionBar actionbar = this.getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        d("MainActivity, onStart Called");
    }

    @Override
    protected void onResume(){
        super.onResume();
        d("MainActivity, onResume Called");

        if(_listView == null || _listView.getAdapter() == null){
            d("_listView.getAdapter() == null");
            settingListAdapter();
        }

        if(nAdapter.getCount() == 0)
            loadItems();
        else
            sortAdapter();
    }

    @Override
    protected void onPause(){
        super.onPause();
        d("MainActivity, onPause Called");
        if(nAdapter.getCount() != 0)
            saveItems();
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
        switch (id){
            case R.id.action_settings:
                startActivity(new Intent(NewsActivity.this, PreferencesActivity.class));
                return true;
            case R.id.action_refresh_all:
                try {
                    FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
                    PrintWriter writer = new PrintWriter(fos);
                    writer.print("");
                    writer.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                extractThothNews();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadItems() {
        BufferedReader reader = null;
        try {
            FileInputStream fis = openFileInput(FILE_NAME);
            reader = new BufferedReader(new InputStreamReader(fis));

            String class_id;
            String new_id;
            String title;
            Date when;
            Status status;

//            rowItems.clear();

            while (null != (class_id = reader.readLine())) {
                if(classId.compareToIgnoreCase(class_id) != 0)
                    continue;

                new_id = reader.readLine();
                title = reader.readLine();

                /*TODO Fix Exception */
                when = Utils.SIMPLE_DATE_FORMAT.parse(reader.readLine());

                status = (reader.readLine().compareToIgnoreCase(String.valueOf(Status.READ)) == 0)
                        ? Status.READ
                        : Status.NOTREAD;

                rowItems.add(new ThothClassNewListItem(Integer.valueOf(new_id), title, when, status));
            }

            if(rowItems.isEmpty())
                return;

            nAdapter = new NewsListAdapter(NewsActivity.this, R.layout.layout_new_item, rowItems);
            _listView.setAdapter(nAdapter);

            sortAdapter();

            nAdapter.notifyDataSetChanged();
            _listView.refreshDrawableState();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sortAdapter() {

        nAdapter.sort(new Comparator<ThothClassNewListItem>() {
            public int compare(ThothClassNewListItem tc1, ThothClassNewListItem tc2) {
                return tc2.getWhen().compareTo(tc1.getWhen());
            };
        });

        nAdapter.sort(new Comparator<ThothClassNewListItem>() {
            public int compare(ThothClassNewListItem tc1, ThothClassNewListItem tc2) {
                return tc1.getStatus().compareTo(tc2.getStatus());
            };
        });
    }

    private void saveItems() {
        PrintWriter writer = null;
        try {
            FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    fos)));

            for(ThothClassNewListItem thothClass : rowItems){
                writer.println(thothClass.GetInfoToStore(classId));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != writer) {
                writer.close();
            }
        }
    }

}