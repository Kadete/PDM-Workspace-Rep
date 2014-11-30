package pt.isel.pdm.grupo17.thothnews.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Set;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.fragments.SettingsFragment;

import static pt.isel.pdm.grupo17.thothnews.utils.ParseUtils.d;
import static pt.isel.pdm.grupo17.thothnews.utils.TagUtils.TAG_ACTIVITY;

public class PreferencesActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle(R.string.label_activity_preferences);

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
    public void onBackPressed()
    {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        final Set<String> classesIDSelected = sharedPrefs.getStringSet("multi_select_list_key", null);

        if (classesIDSelected != null && !classesIDSelected.isEmpty()){
            Intent intent = new Intent(this, ClassesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else
            Toast.makeText(getApplication(), getString(R.string.request_select_class_toast) , Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        d(TAG_ACTIVITY, "PreferencesActivity, onStart Called");
        ActionBar actionbar = this.getActionBar();
        assert actionbar != null;
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume(){
        super.onResume();
        d(TAG_ACTIVITY, "PreferencesActivity, onResume Called");
    }

    @Override
    protected void onPause(){
        super.onPause();
        d(TAG_ACTIVITY, "PreferencesActivity, onPause Called");
    }

    @Override
    protected void onStop(){
        super.onStop();
        d(TAG_ACTIVITY, "PreferencesActivity, onStop Called");
    }

    @Override
    protected void onRestart(){
        super.onRestart();
       d(TAG_ACTIVITY, "PreferencesActivity, onRestart Called");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        d(TAG_ACTIVITY, "PreferencesActivity, onDestroy Called");
    }

}