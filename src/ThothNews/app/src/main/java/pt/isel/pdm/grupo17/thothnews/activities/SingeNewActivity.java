package pt.isel.pdm.grupo17.thothnews.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.fragments.NewsListFragment;
import pt.isel.pdm.grupo17.thothnews.fragments.SingleNewFragment;
import pt.isel.pdm.grupo17.thothnews.models.ThothNew;
import pt.isel.pdm.grupo17.thothnews.models.ThothNewsList;
import pt.isel.pdm.grupo17.thothnews.utils.TagUtils;

public class SingeNewActivity extends FragmentActivity {

    ViewPager mViewPager;
    static List<ThothNew> sThothNewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!NewsListFragment.isTwoPane()) {
            setContentView(R.layout.fragment_pager_news);
            mViewPager = (ViewPager) findViewById(R.id.pager);
            mViewPager.setId(R.id.viewPager);

            Intent intent = getIntent();
            getActionBar().setTitle(getIntent().getStringExtra(TagUtils.TAG_SELECT_CLASS_NAME));
            int thothNewPosition = intent.getExtras().getInt(TagUtils.TAG_SELECT_NEW_POSITION, 0);
            ThothNewsList list = (ThothNewsList) intent.getExtras().getSerializable(TagUtils.TAG_SERIALIZABLE_LIST);
            sThothNewList = list.getItems();

            mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
                @Override
                public android.support.v4.app.Fragment getItem(int position) {
                    return SingleNewFragment.newInstance(sThothNewList.get(position));
                }
                @Override
                public int getCount() {
                    return sThothNewList.size();
                }
                @Override
                public CharSequence getPageTitle(int position) {
                    return sThothNewList.get(position).getShortWhen();
                }
            });
            mViewPager.setCurrentItem(thothNewPosition);
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        ActionBar actionbar = this.getActionBar();
        assert actionbar != null;
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_single_new, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(SingeNewActivity.this, PreferencesActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

