package pt.isel.pdm.grupo17.thothnews.fragments;

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
import pt.isel.pdm.grupo17.thothnews.view.SlidingTabLayout;

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

        ParticipantsFragment createParticipantsFragment() {
            return ParticipantsFragment.newInstance();
        }

        NewsListFragment createNewsListFragment() {
            return NewsListFragment.newInstance();
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
                case NEWS_FRAGMENT_POSITION:
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
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pager_class_sections, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        ViewPager mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        sampleFragmentPagerAdapter = new SampleFragmentPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(sampleFragmentPagerAdapter);

        SlidingTabLayout mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
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
    static final int TOTAL_FRAGMENTS = 2;
    static final int NEWS_FRAGMENT_POSITION = 0;
    static final int PARTICIPANTS_FRAGMENT_POSITION = 1;

    public void refreshLoader() {
        if(sampleFragmentPagerAdapter.newsListFragment != null)
            sampleFragmentPagerAdapter.newsListFragment.refreshLoader();
        if(sampleFragmentPagerAdapter.participantsFragment != null)
            sampleFragmentPagerAdapter.participantsFragment.refreshLoader();
    }

    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {

        SampleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private NewsListFragment newsListFragment = null;
        private ParticipantsFragment participantsFragment = null;

        @Override
        public Fragment getItem(int i) {
            switch (i){
                case NEWS_FRAGMENT_POSITION:
                    return mTabs.get(NEWS_FRAGMENT_POSITION).createNewsListFragment();
                case PARTICIPANTS_FRAGMENT_POSITION:
                    return mTabs.get(PARTICIPANTS_FRAGMENT_POSITION).createParticipantsFragment();
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