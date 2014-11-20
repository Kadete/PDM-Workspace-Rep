package pt.isel.pdm.grupo17.anniversaryreminder.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

import pt.isel.pdm.grupo17.anniversaryreminder.fragments.SettingsFragment;

import static pt.isel.pdm.grupo17.anniversaryreminder.utils.Utils.*;

public class PreferencesActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        d(TAG_ACTIVITY,"PreferencesActivity, onCreate Called");
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
        d(TAG_ACTIVITY,"PreferencesActivity, onStart Called");
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
        d(TAG_ACTIVITY,"PreferencesActivity, onResume Called");
    }

    @Override
    protected void onPause(){
        super.onPause();
        d(TAG_ACTIVITY,"PreferencesActivity, onPause Called");
    }

    @Override
    protected void onStop(){
        super.onStop();
        d(TAG_ACTIVITY,"PreferencesActivity, onStop Called");
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        d(TAG_ACTIVITY,"PreferencesActivity, onRestart Called");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        d(TAG_ACTIVITY,"PreferencesActivity, onDestroy Called");
    }

}