package isel.pdm.serie1.thothNews;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
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
import java.util.Iterator;
import java.util.Set;

import static android.widget.AdapterView.OnItemClickListener;
import static isel.pdm.serie1.thothNews.Utils.readAllFrom;

public class MainActivity extends Activity {

    private ListView _listView;
    ArrayList<ThothClassNewItem> rowItems;


    static private final String TAG = "New-Selected-ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("DEBUG", "MainActivity, onCreate Called");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> classesSelected = sharedPrefs.getStringSet("multi_select_list_key", null);


        _listView = (ListView) findViewById(R.id.listView1);
        rowItems = new ArrayList<ThothClassNewItem>();

        new ThothNewsExtractor().execute(classesSelected);

        //TODO : guardar estado das noticias já lidas

        CustomListAdapter cAdapter = new CustomListAdapter(getApplicationContext(), R.layout.new_item_layout, rowItems);
        _listView.setAdapter(cAdapter);

        _listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.new_item_id);
                TextView tv_title = (TextView) view.findViewById(R.id.new_item_title);
                TextView tv_when = (TextView) view.findViewById(R.id.new_item_when);

                tv_title.setTypeface(null, Typeface.NORMAL);
                tv_when.setTypeface(null, Typeface.NORMAL);

                String newId = textView.getText().toString();

                Intent i = new Intent(MainActivity.this, NewViewActivity.class);
                i.putExtra(TAG, newId);
                startActivity(i);
            }
        });

    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.d("DEBUG", "MainActivity, onStart Called");
    }

    @Override
    protected void onResume(){
        super.onResume();
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

    public class ThothNewsExtractor extends AsyncTask<Set<String>, Void, ArrayList<ThothClassNewItem>> {

        final static String urlString = "http://thoth.cc.e.ipl.pt/api/v1/classes/";
        int newsCount = 0;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
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

            for(ThothClassNewItem newItem : result)
                rowItems.add(newItem);

            CustomListAdapter adapter = new CustomListAdapter( getApplicationContext(), R.layout.new_item_layout, rowItems);
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
                String when = dateFormat.parse(jnew.getString("when")).toString();
                String self = jnew.getJSONObject("_links").getString("self");

                news.add(new ThothClassNewItem(id, title, when, self));
            }
            return news;
        }
    }
}