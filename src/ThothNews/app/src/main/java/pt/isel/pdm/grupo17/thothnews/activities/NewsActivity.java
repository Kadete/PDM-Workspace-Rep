package pt.isel.pdm.grupo17.thothnews.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.fragments.NewsListFragment;
import pt.isel.pdm.grupo17.thothnews.fragments.SingleNewFragment;
import pt.isel.pdm.grupo17.thothnews.models.ThothNew;
import pt.isel.pdm.grupo17.thothnews.services.ThothUpdateService;
import pt.isel.pdm.grupo17.thothnews.utils.TagUtils;

public class NewsActivity extends FragmentActivity implements NewsListFragment.Callbacks{

    static long sClassID;
    static String sClassName;
    static final int ARG_CLASS_ID_DEFAULT_VALUE = -1;
    public static boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_news);

        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentById(R.id.fragment_container_news_list) != null) {
            NewsListFragment newsListFragment = new NewsListFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container_news_list, newsListFragment)
                    .commit();
        }

        if (findViewById(R.id.fragment_container_detail_new) != null) {
            mTwoPane = true;
            ((NewsListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container_news_list)).setActivateOnItemClick(true);
        }

        Intent intent = getIntent();
        sClassID = intent.getLongExtra(TagUtils.TAG_SELECT_CLASS_ID, ARG_CLASS_ID_DEFAULT_VALUE);
        sClassName = intent.getStringExtra(TagUtils.TAG_SELECT_CLASS_NAME);
        getActionBar().setTitle(sClassName);
    }

    @Override
    public void onStart(){
        super.onStart();
        ActionBar actionbar = getActionBar();
        assert actionbar != null;
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onItemSelected(ThothNew thothNew) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putSerializable(TagUtils.TAG_SERIALIZABLE_NEW, thothNew);
            SingleNewFragment fragment = new SingleNewFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_detail_new, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_refresh:
                ThothUpdateService.startActionClassNewsUpdate(this, sClassID);
                ((NewsListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container_news_list)).refreshLoader();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, PreferencesActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
