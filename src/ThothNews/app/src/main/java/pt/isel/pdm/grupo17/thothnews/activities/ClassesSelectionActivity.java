package pt.isel.pdm.grupo17.thothnews.activities;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.fragments.ClassesSelectionFragment;

public class ClassesSelectionActivity extends FragmentActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_classes);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            ClassesSelectionFragment fragment = new ClassesSelectionFragment();
            transaction.replace(R.id.fragment_container_classes, fragment);
            transaction.commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ActionBar actionbar = this.getActionBar();
        assert actionbar != null;
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_classes_selection, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_refresh:
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container_classes);
                if(fragment != null)
                    ((ClassesSelectionFragment) fragment).refreshLoader();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container_classes);
        if(fragment != null)
            ((ClassesSelectionFragment) fragment).updateClassesSelection(ClassesSelectionActivity.this, true);
    }

}