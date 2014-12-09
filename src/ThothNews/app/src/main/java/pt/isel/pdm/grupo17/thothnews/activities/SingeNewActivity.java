package pt.isel.pdm.grupo17.thothnews.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.fragments.NewsListFragment;
import pt.isel.pdm.grupo17.thothnews.fragments.SingleNewFragment;
import pt.isel.pdm.grupo17.thothnews.models.ThothNew;
import pt.isel.pdm.grupo17.thothnews.models.ThothNewsList;
import pt.isel.pdm.grupo17.thothnews.utils.TagUtils;

public class SingeNewActivity extends FragmentActivity {

    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!NewsListFragment.isTwoPane()) {
            setContentView(R.layout.fragment_pager_news);
            mViewPager = (ViewPager) findViewById(R.id.pager);
            mViewPager.setId(R.id.viewPager);

            Intent intent = getIntent();
            getActionBar().setTitle(getIntent().getStringExtra(TagUtils.TAG_SELECT_CLASS_NAME));
            final ThothNewsList list = (ThothNewsList) intent.getExtras().getSerializable(TagUtils.TAG_SERIALIZABLE_LIST);
            int newClickPosition = intent.getExtras().getInt(TagUtils.TAG_SELECT_NEW_POS, 0);

            mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
                @Override
                public android.support.v4.app.Fragment getItem(int pos) {
                    return SingleNewFragment.newInstance(list.getItems().get(pos));
                }

                @Override
                public int getCount() {
                    return list.getItems().size();
                }
                @Override
                public CharSequence getPageTitle(int position) {
                    return ((ThothNew)list.get(position)).getShortWhen();
                }
            });
            mViewPager.setCurrentItem(newClickPosition);
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

