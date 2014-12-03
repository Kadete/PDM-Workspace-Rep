package pt.isel.pdm.grupo17.thothnews.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.fragments.ClassesListFragment;
import pt.isel.pdm.grupo17.thothnews.services.ThothUpdateService;

public class ClassesActivity extends FragmentActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_classes);

        getActionBar().setTitle(R.string.label_activity_classes);

        FragmentManager fm = getSupportFragmentManager();
        if(fm.findFragmentById(R.id.fragment_container_classes) == null){
            ClassesListFragment f = new ClassesListFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container_classes, f)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_classes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                ThothUpdateService.startActionNewsUpdate(this);
                ((ClassesListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container_classes)).refreshLoader();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(new Intent(ClassesActivity.this, PreferencesActivity.class)));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}