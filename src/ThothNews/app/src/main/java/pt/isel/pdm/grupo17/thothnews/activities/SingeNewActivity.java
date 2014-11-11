package pt.isel.pdm.grupo17.thothnews.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.models.LinksClass;
import pt.isel.pdm.grupo17.thothnews.models.ThothClassNew;
import pt.isel.pdm.grupo17.thothnews.utils.DateUtils;

import static pt.isel.pdm.grupo17.thothnews.adapters.NewsListAdapter.TAG_SELECT_NEW_ID;
import static pt.isel.pdm.grupo17.thothnews.utils.ParseUtils.TAG_ACTIVITY;
import static pt.isel.pdm.grupo17.thothnews.utils.ParseUtils.TAG_ASYNC_TASK;
import static pt.isel.pdm.grupo17.thothnews.utils.ParseUtils.d;
import static pt.isel.pdm.grupo17.thothnews.utils.ParseUtils.e;
import static pt.isel.pdm.grupo17.thothnews.utils.ParseUtils.readAllFrom;

public class SingeNewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_new_view);

        d(TAG_ACTIVITY, "NewViewActivity, onCreate Called");

        Intent intent = getIntent();
        String newId = intent.getStringExtra(TAG_SELECT_NEW_ID);

        new ExtractorSingleNew(){
            @Override
            protected void onPostExecute(ThothClassNew result){

                TextView _tvTitle = (TextView) findViewById(R.id.new_view_title);
                if(result == null){
                    _tvTitle.setText("error");
                }else{
                    TextView _tvWhen = (TextView) findViewById(R.id.new_view_when);
                    TextView _tvContent = (TextView) findViewById(R.id.new_view_content);
                    _tvWhen.setText(result.getFormattedWhen());
                    _tvTitle.setText(result.title);
                    _tvContent.setText(result.content);
                }
            }
        }.execute(newId);
    }

    @Override
    public void onBackPressed(){
        finish();
    }


    @Override
    protected void onStart(){
        super.onStart();
        d(TAG_ACTIVITY, "NewViewActivity, onStart Called");
        ActionBar actionbar = this.getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume(){
        super.onResume();
        d(TAG_ACTIVITY, "NewViewActivity, onResume Called");
    }

    @Override
    protected void onPause(){
        super.onPause();
        d(TAG_ACTIVITY, "NewViewActivity, onPause Called");
    }

    @Override
    protected void onStop(){
        super.onStop();
        d(TAG_ACTIVITY, "NewViewActivity, onStop Called");
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        d(TAG_ACTIVITY, "NewViewActivity, onRestart Called");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        d(TAG_ACTIVITY, "NewViewActivity, onDestroy Called");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(SingeNewActivity.this, PreferencesActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

class ExtractorSingleNew extends AsyncTask<String,Void,ThothClassNew> {

    String urlString = "http://thoth.cc.e.ipl.pt/api/v1/newsitems/";

    @Override
    protected ThothClassNew doInBackground(String... arg0) {
        try{

            URL url = new URL(urlString + arg0[0]);
            HttpURLConnection c = (HttpURLConnection)url.openConnection();

            try{
                InputStream is = c.getInputStream();
                String data = readAllFrom(is);
                return parseFrom(data);

            } finally{
                c.disconnect();
            }
        }catch(IOException e){
            e(TAG_ASYNC_TASK, e.getMessage());
        } catch (ParseException e) {
            e(TAG_ASYNC_TASK, e.getMessage());
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
            _new.when = DateUtils.SAVE_DATE_FORMAT.parse(root.getString("when"));
            _new.content = String.valueOf(Html.fromHtml(root.getString("content")));
            _new._links = new LinksClass();

            JSONObject links = root.getJSONObject("_links");
            _new._links.self = links.getString("self");
            _new._links.classNewsItems = links.getString("classNewsItems");
            _new._links.clazz = links.getString("class");
            _new._links.root = links.getString("root");
        } catch (JSONException e) {
            e(TAG_ASYNC_TASK, e.getMessage());
            return null;
        }
        return _new;
    }

}
