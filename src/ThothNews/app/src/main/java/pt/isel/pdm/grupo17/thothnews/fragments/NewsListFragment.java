package pt.isel.pdm.grupo17.thothnews.fragments;

import android.accounts.Account;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SyncStatusObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.accounts.GenericAccountService;
import pt.isel.pdm.grupo17.thothnews.activities.ClassSectionsActivity;
import pt.isel.pdm.grupo17.thothnews.activities.SingeNewActivity;
import pt.isel.pdm.grupo17.thothnews.adapters.NewsAdapter;
import pt.isel.pdm.grupo17.thothnews.broadcastreceivers.NetworkReceiver;
import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.models.ThothClass;
import pt.isel.pdm.grupo17.thothnews.models.ThothNew;
import pt.isel.pdm.grupo17.thothnews.services.SyncUtils;
import pt.isel.pdm.grupo17.thothnews.utils.TagUtils;
import pt.isel.pdm.grupo17.thothnews.view.MultiSwipeRefreshLayout;

public class NewsListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int NEWS_CURSOR_LOADER_ID = 2;

    private static final String[] CURSOR_COLUMNS = {ThothContract.News._ID, ThothContract.News.TITLE,
            ThothContract.News.WHEN_CREATED, ThothContract.News.READ, ThothContract.News.CONTENT};
    private static final String SELECTION = ThothContract.News.CLASS_ID + " = ? ";

    private static final String STATE_ACTIVATED_POSITION_NEW_ITEM = "STATE_ACTIVATED_POSITION_NEW_ITEM";

    private static int mActivatedPosition = ListView.INVALID_POSITION;
    private static ThothClass sThothClass;

    private static MultiSwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mListView;
    private NewsAdapter mListAdapter;

    private Object mSyncObserverHandle;

    public interface CallbackNew {
        public void onItemSelected(ThothNew thothNew);
    }
    private CallbackNew mCallback = sDummyCallback;
    private static CallbackNew sDummyCallback = new CallbackNew() {
        @Override
        public void onItemSelected(ThothNew thothNew) {
        }
    };

    public static NewsListFragment newInstance() {
        Bundle bundle = new Bundle();
        NewsListFragment fragment = new NewsListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View sNewsView = inflater.inflate(R.layout.fragment_section_news, container, false);
        mSwipeRefreshLayout = (MultiSwipeRefreshLayout) sNewsView.findViewById(R.id.swipe_refresh);
        mListView = (ListView) sNewsView.findViewById(android.R.id.list);
        return sNewsView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION_NEW_ITEM))
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION_NEW_ITEM));

        sThothClass = ClassSectionsActivity.getThothClass();

        mListAdapter = new NewsAdapter(getActivity());
        AlphaInAnimationAdapter animationAdapter = new AlphaInAnimationAdapter(mListAdapter);
        animationAdapter.setAbsListView(mListView);
        mListView.setAdapter(animationAdapter);

        mSwipeRefreshLayout.setSwipeableChildren(android.R.id.list, android.R.id.empty);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshAndUpdate();
            }
        });
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN);

        getLoaderManager().initLoader(NEWS_CURSOR_LOADER_ID, null, this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof CallbackNew)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }
        SyncUtils.CreateSyncAccount(activity);  // Create account, if needed

        mCallback = (CallbackNew) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = sDummyCallback;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            outState.putInt(STATE_ACTIVATED_POSITION_NEW_ITEM, mActivatedPosition);
        }
    }

    private void setActivatedPosition(int position) {
        getListView().setItemChecked((position == ListView.INVALID_POSITION) ? mActivatedPosition : position, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSyncStatusObserver.onStatusChanged(0);

        final int mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING | ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
        mSyncObserverHandle = ContentResolver.addStatusChangeListener(mask, mSyncStatusObserver);
        getLoaderManager().restartLoader(NEWS_CURSOR_LOADER_ID, null, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSyncObserverHandle != null) {
            ContentResolver.removeStatusChangeListener(mSyncObserverHandle);
            mSyncObserverHandle = null;
        }
    }

    @Override
    public void onListItemClick(ListView l, View view, int position, long id) {
        super.onListItemClick(l, view, position, id);
        ThothNew thothNew = (ThothNew) mListAdapter.getItem(position);
        if (ClassSectionsActivity.isTwoPane()) {
            mListAdapter.setSelectedNewID(thothNew.getID());
            mCallback.onItemSelected((ThothNew) mListAdapter.getItem(position));
        } else {
            Intent i = new Intent(getActivity(), SingeNewActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra(TagUtils.TAG_SELECT_CLASS_NAME, sThothClass.getFullName());
            i.putExtra(TagUtils.TAG_SERIALIZABLE_LIST, mListAdapter.getNewsList());
            i.putExtra(TagUtils.TAG_SELECT_NEW_POSITION, position);
            startActivity(i);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String [] SELECTION_ARGS = new String[]{ String.valueOf(sThothClass.getID()) };
        return new CursorLoader(getActivity(), ThothContract.News.CONTENT_URI,
                CURSOR_COLUMNS, SELECTION, SELECTION_ARGS, ThothContract.News.READ +", "+ ThothContract.News.WHEN_CREATED + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mListAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mListAdapter.swapCursor(null);
    }

    public void refreshAndUpdate() {
        if(!NetworkReceiver.checkConnection(getActivity(), true)){
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }
//        ThothUpdateService.startActionClassNewsUpdate(getActivity(), sThothClass.getID());
        getLoaderManager().restartLoader(NEWS_CURSOR_LOADER_ID, null, this);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private SyncStatusObserver mSyncStatusObserver = new SyncStatusObserver() {
        @Override
        public void onStatusChanged(int which) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Account account = GenericAccountService.GetAccount();
                    if (account == null) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        return;
                    }
                    boolean syncActive = ContentResolver.isSyncActive(account, ThothContract.CONTENT_AUTHORITY);
                    boolean syncPending = ContentResolver.isSyncPending(account, ThothContract.CONTENT_AUTHORITY);
                    mSwipeRefreshLayout.setRefreshing(syncActive || syncPending);
                }
            });
        }
    };

}