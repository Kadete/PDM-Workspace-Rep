package pt.isel.pdm.grupo17.thothnews.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.models.ThothNew;
import pt.isel.pdm.grupo17.thothnews.utils.TagUtils;

import static pt.isel.pdm.grupo17.thothnews.utils.TagUtils.TAG_ACTIVITY;
import static pt.isel.pdm.grupo17.thothnews.utils.ParseUtils.d;

public class SingeNewActivity extends Activity {

    static ThothNew sThothNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_single_new_view);
        getActionBar().setTitle(getIntent().getStringExtra(TagUtils.TAG_SELECT_CLASS_NAME));

        sThothNew = (ThothNew) getIntent().getExtras().getSerializable(TagUtils.TAG_SELECT_NEW);
        final TextView title = (TextView) findViewById(R.id.new_view_title);
        title.setText(sThothNew.getTitle());
        final TextView when = (TextView) findViewById(R.id.new_view_when);
        when.setText(sThothNew.getFormattedWhen());
        final TextView content = (TextView) findViewById(R.id.new_view_content);
        content.setText(sThothNew.getContent());
    }

    @Override
    public void onBackPressed(){
        finish();
    }


    @Override
    protected void onStart(){
        super.onStart();
        ActionBar actionbar = this.getActionBar();
        assert actionbar != null;
        actionbar.setDisplayHomeAsUpEnabled(true);
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

