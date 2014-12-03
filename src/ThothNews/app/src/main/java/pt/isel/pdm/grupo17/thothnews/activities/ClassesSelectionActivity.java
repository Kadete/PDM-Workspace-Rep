package pt.isel.pdm.grupo17.thothnews.activities;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.fragments.ClassesSelectionListFragment;
import pt.isel.pdm.grupo17.thothnews.services.ThothUpdateService;

public class ClassesSelectionActivity extends FragmentActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_classes_selection);

        FragmentManager fm = getSupportFragmentManager();
        if(fm.findFragmentById(R.id.fragment_container_classes_selection) == null){
            ClassesSelectionListFragment f = new ClassesSelectionListFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container_classes_selection, f)
                    .commit();
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_refresh:
                ThothUpdateService.startActionClassesUpdate(this);
                ((ClassesSelectionListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container_classes_selection)).refreshLoader();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}