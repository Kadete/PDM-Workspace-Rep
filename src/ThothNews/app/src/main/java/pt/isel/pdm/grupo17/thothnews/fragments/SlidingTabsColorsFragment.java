package pt.isel.pdm.grupo17.thothnews.fragments;

import android.content.ContentValues;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.utils.ParseUtils;
import pt.isel.pdm.grupo17.thothnews.view.SlidingTabLayout;

import static pt.isel.pdm.grupo17.thothnews.data.providers.SQLiteUtils.TRUE;

public class SlidingTabsColorsFragment extends Fragment {

    static class SamplePagerItem {
        private final CharSequence mTitle;
        private final int mIndicatorColor;
        private final int mDividerColor;

        SamplePagerItem(CharSequence title, int indicatorColor, int dividerColor) {
            mTitle = title;
            mIndicatorColor = indicatorColor;
            mDividerColor = dividerColor;
        }

        ParticipantsFragment createParticipantsFragment() { return ParticipantsFragment.newInstance(); }
        NewsListFragment createNewsListFragment() {
            return NewsListFragment.newInstance();
        }
        WorkItemsListFragment createWorkItemsListFragment() {
            return WorkItemsListFragment.newInstance();
        }

        CharSequence getTitle() {
            return mTitle;
        }
        int getIndicatorColor() {
            return mIndicatorColor;
        }
        int getDividerColor() {
            return mDividerColor;
        }
    }

    SampleFragmentPagerAdapter sampleFragmentPagerAdapter;
    private List<SamplePagerItem> mTabs = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        for(int idx = 0; idx < TOTAL_FRAGMENTS;idx++) {
            switch (idx){
                case NEWS_LIST_FRAGMENT_POSITION:
                    mTabs.add(new SamplePagerItem(
                            getString(R.string.tab_news), // Title
                            Color.RED, // Indicator color
                            Color.GRAY // Divider color
                    ));
                    break;
                case PARTICIPANTS_FRAGMENT_POSITION:
                    mTabs.add(new SamplePagerItem(
                            getString(R.string.tab_participants), // Title
                            Color.BLUE, // Indicator color
                            Color.GRAY // Divider color
                    ));
                    break;
                case WORK_ITEMS_LIST_FRAGMENT_POSITION:
                    mTabs.add(new SamplePagerItem(
                            getString(R.string.tab_work_items), // Title
                            Color.GREEN, // Indicator color
                            Color.GRAY // Divider color
                    ));
                    break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pager_class_sections, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {

        ViewPager mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        sampleFragmentPagerAdapter = new SampleFragmentPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(sampleFragmentPagerAdapter);

        final SlidingTabLayout mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);

        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {

            @Override
            public int getIndicatorColor(int position) {
                return mTabs.get(position).getIndicatorColor();
            }

            @Override
            public int getDividerColor(int position) {
                return mTabs.get(position).getDividerColor();
            }

        });
    }
    static final int TOTAL_FRAGMENTS = 3;
    public static final int NEWS_LIST_FRAGMENT_POSITION = 0;
    public static final int PARTICIPANTS_FRAGMENT_POSITION = 1;
    public static final int WORK_ITEMS_LIST_FRAGMENT_POSITION = 2;

    private void refreshNewsLoader(){
        if(sampleFragmentPagerAdapter.newsListFragment != null)
            sampleFragmentPagerAdapter.newsListFragment.refreshAndUpdate();
    }

    public void updateReadAll(long classID) {
        ContentValues values = new ContentValues();
        values.put(ThothContract.News.READ, TRUE);
        getActivity().getContentResolver().update(ParseUtils.Classes.parseNewsFromClassID(classID), values, null, null );
        refreshNewsLoader();
    }

    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {

        SampleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private NewsListFragment newsListFragment = null;

        @Override
        public Fragment getItem(int i) {
            switch (i){
                case NEWS_LIST_FRAGMENT_POSITION:
                    return newsListFragment = mTabs.get(NEWS_LIST_FRAGMENT_POSITION).createNewsListFragment();
                case PARTICIPANTS_FRAGMENT_POSITION:
                    return mTabs.get(PARTICIPANTS_FRAGMENT_POSITION).createParticipantsFragment();
                case WORK_ITEMS_LIST_FRAGMENT_POSITION:
                    return mTabs.get(WORK_ITEMS_LIST_FRAGMENT_POSITION).createWorkItemsListFragment();
                default:
                    throw new IllegalStateException();
            }
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabs.get(position).getTitle();
        }

    }
}