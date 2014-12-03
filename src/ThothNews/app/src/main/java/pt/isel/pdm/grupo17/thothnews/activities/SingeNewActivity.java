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
import pt.isel.pdm.grupo17.thothnews.fragments.SingleNewFragment;
import pt.isel.pdm.grupo17.thothnews.models.ThothNewList;
import pt.isel.pdm.grupo17.thothnews.utils.TagUtils;

public class SingeNewActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setTitle(getIntent().getStringExtra(TagUtils.TAG_SELECT_CLASS_NAME));
        ViewPager pager = new ViewPager(this);
        pager.setId(R.id.viewPager);
        setContentView(pager);

        Intent i = getIntent();
        final ThothNewList list = (ThothNewList)i.getExtras().getSerializable(TagUtils.TAG_SERIALIZABLE_LIST);
        int ix = i.getExtras().getInt("ix",0);

        pager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()){
            @Override
            public android.support.v4.app.Fragment getItem(int pos) {
                SingleNewFragment f = SingleNewFragment.newInstance(list.getItems().get(pos));
                return f;
            }

            @Override
            public int getCount() {
                return list.getItems().size();
            }
        });
        pager.setCurrentItem(ix);
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
        return true;
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

