package pt.isel.pdm.grupo17.thothnews.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Set;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
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
    public void onBackPressed() {
        Cursor cursor = getContentResolver().query(ThothContract.Clazz.ENROLLED_URI, null,null, null, null);
        if (cursor.moveToNext()) {
            startActivity(new Intent(this, ClassesActivity.class));
            cursor.close();
            return;
        }
        cursor.close();
        Toast.makeText(getApplication(), getString(R.string.request_select_class_toast) , Toast.LENGTH_LONG).show();
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
        ActionBar actionbar = this.getActionBar();
        assert actionbar != null;
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

}