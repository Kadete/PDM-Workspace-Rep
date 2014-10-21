package isel.pdm.serie1.thothNews;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static isel.pdm.serie1.thothNews.Utils.readAllFrom;


public class NewViewActivity extends Activity {

    static private final String TAG = "New-Selected-ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_view);

        Log.d("DEBUG", "NewViewActivity, onCreate Called");

        Intent intent = getIntent();
        String newId = intent.getStringExtra(TAG);
        Toast.makeText(getBaseContext(), newId, Toast.LENGTH_SHORT).show();

        ThothNew c = new ThothNew(newId){
            @Override
            protected void onPostExecute(ThothClassNew result){

                TextView _tvTitle = (TextView) findViewById(R.id.new_view_title);
                if(result == null){
                    _tvTitle.setText("error");
                }else{
                    TextView _tvWhen = (TextView) findViewById(R.id.new_view_when);
                    TextView _tvContent = (TextView) findViewById(R.id.new_view_content);
                    _tvWhen.setText(result.when);
                    _tvTitle.setText(result.title);
                    _tvContent.setText(result.content);
                }
            }
        };
        c.execute();
    }

    @Override
    public void onBackPressed(){
        finish();
    }


    @Override
    protected void onStart(){
        super.onStart();
        Log.d("DEBUG", "NewViewActivity, onStart Called");
        ActionBar actionbar = this.getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d("DEBUG", "NewViewActivity, onResume Called");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d("DEBUG","NewViewActivity, onPause Called");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d("DEBUG","NewViewActivity, onStop Called");
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Log.d("DEBUG", "NewViewActivity, onRestart Called");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d("DEBUG", "NewViewActivity, onDestroy Called");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(NewViewActivity.this, PreferencesActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class ThothClassNew{
        public int id;
        public String title;
        public String when;
        public String content;
        public LinksClass _links;
    }

    class LinksClass{
        public String self;
        public String classNewsItems;
        public String clazz;
        public String root;
    }

    class ThothNew extends AsyncTask<Void,Void,ThothClassNew> {

        String urlString = "http://thoth.cc.e.ipl.pt/api/v1/newsitems/";
        private String newIdSelected;

        ThothNew(String newIdSelected){

            this.newIdSelected = newIdSelected;
        }

        @Override
        protected ThothClassNew doInBackground(Void... arg0) {
            try{

                URL url = new URL(urlString + newIdSelected);
                HttpURLConnection c = (HttpURLConnection)url.openConnection();

                try{
                    InputStream is = c.getInputStream();
                    String data = readAllFrom(is);
                    return parseFrom(data);

                } finally{
                    c.disconnect();
                }
            }catch(IOException e){
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        private ThothClassNew parseFrom(String s) throws ParseException {

            JSONObject root;
            ThothClassNew _new = new ThothClassNew();
            try {
                root = new JSONObject(s);
                _new.id = root.getInt("id");
                _new.title = root.getString("title");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                _new.when = dateFormat.parse(root.getString("when")).toString();
                _new.content = String.valueOf(Html.fromHtml(root.getString("content")));
                _new._links = new LinksClass();

                JSONObject links = root.getJSONObject("_links");
                _new._links.self = links.getString("self");
                _new._links.classNewsItems = links.getString("classNewsItems");
                _new._links.clazz = links.getString("class");
                _new._links.root = links.getString("root");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return _new;
        }

    }

}
