package isel.pdm.serie1.thothNews;
/**
 * Created by Kadete on 08/10/2014.
 */
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Set;

public class PreferencesActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("DEBUG", "PreferencesActivity, onCreate Called");

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_prefs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.default_prefs:
                AlertDialogFragment dFragment = new AlertDialogFragment();
                dFragment.show(getFragmentManager(), "Dialog Fragment");
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
            Toast.makeText(getApplication(), "Please Select one Classe at least!" , Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("DEBUG", "PreferencesActivity, onStart Called");
        ActionBar actionbar = this.getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d("DEBUG", "PreferencesActivity, onResume Called");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d("DEBUG","PreferencesActivity, onPause Called");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d("DEBUG","PreferencesActivity, onStop Called");
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Log.d("DEBUG", "PreferencesActivity, onRestart Called");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d("DEBUG", "PreferencesActivity, onDestroy Called");
    }

}