package isel.pdm.serie1.anniversaryreminder;
/**
* Created by Kadete on 08/10/2014.
*/
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.MenuItem;

import static isel.pdm.serie1.anniversaryreminder.Utils.*;

public class PreferencesActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        d("PreferencesActivity, onCreate Called");
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        d( "PreferencesActivity, onStart Called");
        ActionBar actionbar = this.getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onResume(){
        super.onResume();
        d("PreferencesActivity, onResume Called");
    }

    @Override
    protected void onPause(){
        super.onPause();
        d("PreferencesActivity, onPause Called");
    }

    @Override
    protected void onStop(){
        super.onStop();
        d("PreferencesActivity, onStop Called");
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        d("PreferencesActivity, onRestart Called");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        d("PreferencesActivity, onDestroy Called");
    }

}