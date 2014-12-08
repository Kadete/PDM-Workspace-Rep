package pt.isel.pdm.grupo17.thothnews.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.fragments.ClassesFragment;

public class ClassesActivity extends FragmentActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_classes);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            ClassesFragment fragment = new ClassesFragment();
            transaction.replace(R.id.fragment_container_classes, fragment);
            transaction.commit();
        }

        getActionBar().setTitle(R.string.label_activity_classes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_classes, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container_classes);
                if(fragment != null)
                    ((ClassesFragment) fragment).refreshLoader();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(new Intent(ClassesActivity.this, PreferencesActivity.class)));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}