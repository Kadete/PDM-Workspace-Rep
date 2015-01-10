package pt.isel.pdm.grupo17.thothnews.fragments;

import android.accounts.Account;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncStatusObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.melnykov.fab.FloatingActionButton;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;

import java.util.Set;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.accounts.GenericAccountService;
import pt.isel.pdm.grupo17.thothnews.activities.ClassSectionsActivity;
import pt.isel.pdm.grupo17.thothnews.activities.ClassesPickActivity;
import pt.isel.pdm.grupo17.thothnews.activities.SettingsActivity;
import pt.isel.pdm.grupo17.thothnews.adapters.ClassesAdapter;
import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.models.ThothClass;
import pt.isel.pdm.grupo17.thothnews.services.SyncUtils;
import pt.isel.pdm.grupo17.thothnews.utils.TagUtils;
import pt.isel.pdm.grupo17.thothnews.view.MultiSwipeRefreshLayout;

public class ClassesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    static final int CLASSES_CURSOR_LOADER_ID = 0;
    static final String[] CURSOR_COLUMNS = {ThothContract.Classes._ID, ThothContract.Classes.FULL_NAME, ThothContract.Classes.TEACHER_NAME, ThothContract.Classes.SHORT_NAME,
            ThothContract.Classes.SEMESTER, ThothContract.Classes.COURSE, ThothContract.Classes.TEACHER_ID, ThothContract.Classes.UNREAD_NEWS};
    static final String ORDER_BY = ThothContract.Classes.SEMESTER + " DESC, "+ ThothContract.Classes.FULL_NAME + " ASC";

    private MultiSwipeRefreshLayout mSwipeRefreshLayout;
    private GridView mGridView;
    private View mEmptyView;
    private ClassesAdapter mListAdapter;

    private Object mSyncObserverHandle;

    static boolean isActive = false;
    private LoaderManager loader;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        isActive = true;
        loader = getLoaderManager();
    }

    @Override
    public void onDetach(){
        super.onDetach();
        loader = null;
        isActive = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_grid_classes, container, false);

        mSwipeRefreshLayout = (MultiSwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mGridView = (GridView) view.findViewById(android.R.id.list);
        mEmptyView = view.findViewById(android.R.id.empty);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mSyncStatusObserver.onStatusChanged(0);
        final int mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING | ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
        mSyncObserverHandle = ContentResolver.addStatusChangeListener(mask, mSyncStatusObserver);
        refreshLoader();
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListAdapter = new ClassesAdapter(getActivity());
        AlphaInAnimationAdapter animationAdapter = new AlphaInAnimationAdapter(mListAdapter);
        animationAdapter.setAbsListView(mGridView);

        mGridView.setAdapter(animationAdapter);
        mGridView.setEmptyView(mEmptyView);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ThothClass thothClass = (ThothClass) mListAdapter.getItem(position);
                Intent i = new Intent(getActivity(), ClassSectionsActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra(TagUtils.TAG_SERIALIZABLE_CLASS, thothClass);
                startActivity(i);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.attachToListView(mGridView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                Set<String> semestersSet = sharedPreferences.getStringSet(TagUtils.TAG_MULTILIST_SEMESTERS_PREF_KEY, null);
                startActivity(new Intent(getActivity(), (semestersSet == null || semestersSet.isEmpty())
                                                            ? SettingsActivity.class : ClassesPickActivity.class));
            }
        });

        mSwipeRefreshLayout.setSwipeableChildren(android.R.id.list, android.R.id.empty);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SyncUtils.TriggerRefresh();
                refreshLoader();
            }
        });
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN);

        getLoaderManager().initLoader(CLASSES_CURSOR_LOADER_ID, null, this);

    }

    private void refreshLoader() {
        if(isActive && loader != null)
            loader.restartLoader(CLASSES_CURSOR_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(),
                ThothContract.Classes.ENROLLED_URI, CURSOR_COLUMNS , null, null, ORDER_BY);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mListAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mListAdapter.swapCursor(null);
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
                    refreshLoader();
                }
            });
        }
    };
    
}