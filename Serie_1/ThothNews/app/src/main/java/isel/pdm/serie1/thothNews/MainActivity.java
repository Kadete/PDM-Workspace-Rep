package isel.pdm.serie1.thothNews;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static isel.pdm.serie1.thothNews.Utils.readAllFrom;

public class MainActivity extends Activity {

    private ListView _listView;
    ArrayList<ThothClassNewItem> rowItems;
    CustomListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("DEBUG", "MainActivity, onCreate Called");

        settingListAdapter();

    }

    protected void settingListAdapter(){

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> classesIDSelected = sharedPrefs.getStringSet("multi_select_list_key", null);

        if(classesIDSelected == null)
            return;

        Log.d("DEBUG","Classes selected: " + String.valueOf(classesIDSelected.size()));

        _listView = (ListView) findViewById(R.id.listView1);
        rowItems = new ArrayList<ThothClassNewItem>();

        new ThothNewsExtractor().execute(classesIDSelected);

        adapter = new CustomListAdapter(MainActivity.this, R.layout.new_item_layout, rowItems);
        _listView.setAdapter(adapter);
    }

    @Override
    protected void onStart(){
        super.onStart();

        Log.d("DEBUG", "MainActivity, onStart Called");
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(_listView.getAdapter() != null){
            Log.d("DEBUG","_listView.getAdapter() != null");

            adapter = new CustomListAdapter( getApplicationContext(), R.layout.new_item_layout, rowItems);
            _listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            _listView.refreshDrawableState();

        }else{
            settingListAdapter();
        }
        sortRowItems();
        Log.d("DEBUG", "MainActivity, onResume Called");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d("DEBUG","MainActivity, onPause Called");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d("DEBUG","MainActivity, onStop Called");
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Log.d("DEBUG", "MainActivity, onRestart Called");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d("DEBUG", "MainActivity, onDestroy Called");
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
            startActivity(new Intent(MainActivity.this, PreferencesActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void sortRowItems(){
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


    public class ThothNewsExtractor extends AsyncTask<Set<String>, Void, ArrayList<ThothClassNewItem>> {

        final static String urlString = "http://thoth.cc.e.ipl.pt/api/v1/classes/";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<ThothClassNewItem> doInBackground(Set<String>... arg0) {
            try {
                ArrayList<ThothClassNewItem> newItems = new ArrayList<ThothClassNewItem>();
                URL url;

                Iterator it = arg0[0].iterator();
                while(it.hasNext()){

                    url = new URL(urlString + it.next() + "/newsitems");
                    HttpURLConnection c = (HttpURLConnection) url.openConnection();
                    try {
                        InputStream is = c.getInputStream();
                        String data = readAllFrom(is);

                        for(ThothClassNewItem newItem : parseFrom(data))
                            newItems.add(newItem);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        return null;
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return null;
                    } finally {
                        c.disconnect();
                    }
                }
                return newItems;
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<ThothClassNewItem> result) {
            if (result == null || result.size() < 1) {
                Toast.makeText(getApplicationContext(),
                        "Last News Update Failed", Toast.LENGTH_SHORT).show();
                return;
            }

            sortRowItemsFromResult(result);

            adapter = new CustomListAdapter( getApplicationContext(), R.layout.new_item_layout, rowItems);
            _listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            _listView.refreshDrawableState();

            Toast.makeText(getApplicationContext(), "Last News Updated", Toast.LENGTH_SHORT).show();

        }

        private ArrayList<ThothClassNewItem> parseFrom(String s) throws JSONException, ParseException {
            JSONObject root = new JSONObject(s);
            JSONArray jnews = root.getJSONArray("newsItems");
            ArrayList<ThothClassNewItem> news = new ArrayList<ThothClassNewItem>(jnews.length());
            for (int i = 0; i < jnews.length(); ++i) {

                JSONObject jnew = jnews.getJSONObject(i);

                int id = jnew.getInt("id");
                String title = jnew.getString("title");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                Date when = dateFormat.parse(jnew.getString("when"));
                String self = jnew.getJSONObject("_links").getString("self");

                news.add(new ThothClassNewItem(id, title, when, self));
            }
            return news;
        }
    }
}