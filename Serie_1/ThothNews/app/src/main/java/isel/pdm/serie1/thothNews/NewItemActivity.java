package isel.pdm.serie1.thothNews;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import static isel.pdm.serie1.thothNews.NewsListAdapter.*;
import static isel.pdm.serie1.thothNews.Utils.*;


public class NewItemActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_new);

        d("NewViewActivity, onCreate Called");

        Intent intent = getIntent();
        String newId = intent.getStringExtra(TAG_SELECT_NEW_ID);
        Toast.makeText(getBaseContext(), newId, Toast.LENGTH_SHORT).show();

        new ExtractorThothNew(){
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
        d("NewViewActivity, onStart Called");
        ActionBar actionbar = this.getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume(){
        super.onResume();
        d("NewViewActivity, onResume Called");
    }

    @Override
    protected void onPause(){
        super.onPause();
        d("NewViewActivity, onPause Called");
    }

    @Override
    protected void onStop(){
        super.onStop();
        d("NewViewActivity, onStop Called");
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        d( "NewViewActivity, onRestart Called");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        d("NewViewActivity, onDestroy Called");
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
                startActivity(new Intent(NewItemActivity.this, PreferencesActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
