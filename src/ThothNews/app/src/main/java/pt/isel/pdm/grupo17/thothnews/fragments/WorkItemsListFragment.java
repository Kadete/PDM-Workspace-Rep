package pt.isel.pdm.grupo17.thothnews.fragments;

import android.app.Activity;
import android.content.ContentResolver;
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
import pt.isel.pdm.grupo17.thothnews.activities.ClassSectionsActivity;
import pt.isel.pdm.grupo17.thothnews.adapters.WorkItemsAdapter;
import pt.isel.pdm.grupo17.thothnews.broadcastreceivers.NetworkReceiver;
import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.models.ThothClass;
import pt.isel.pdm.grupo17.thothnews.models.ThothWorkItem;
import pt.isel.pdm.grupo17.thothnews.services.ThothUpdateService;
import pt.isel.pdm.grupo17.thothnews.utils.ParseUtils;
import pt.isel.pdm.grupo17.thothnews.view.MultiSwipeRefreshLayout;

public class WorkItemsListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int WORK_ITEMS_CURSOR_LOADER_ID = 3;

    private static final String[] CURSOR_COLUMNS = {ThothContract.WorkItems._ID, ThothContract.WorkItems.TITLE,
            ThothContract.WorkItems.START_DATE, ThothContract.WorkItems.DUE_DATE, ThothContract.WorkItems.URL, ThothContract.WorkItems.EVENT_ID};
    private static final String SELECTION = ThothContract.WorkItems.CLASS_ID + " = ? ";

    private static final String STATE_ACTIVATED_POSITION_WORK_ITEM = "STATE_ACTIVATED_POSITION_WORK_ITEM";

    private static int mActivatedPosition = ListView.INVALID_POSITION;
    private static ThothClass sThothClass;

    private CallbackWorkItem mCallbacks = sDummyCallback;

    private static MultiSwipeRefreshLayout mSwipeRefreshLayout;
    private static ContentResolver sContentResolver;
    private ListView mListView;
    private WorkItemsAdapter mListAdapter;

    public interface CallbackWorkItem {
        public void onItemSelected(ThothWorkItem workItem);
    }

    private static CallbackWorkItem sDummyCallback = new CallbackWorkItem() {
        @Override
        public void onItemSelected(ThothWorkItem workItem) {
        }
    };

    public static WorkItemsListFragment newInstance() {
        Bundle bundle = new Bundle();
        WorkItemsListFragment fragment = new WorkItemsListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View sNewsView = inflater.inflate(R.layout.fragment_section_workitems, container, false);
        mSwipeRefreshLayout = (MultiSwipeRefreshLayout) sNewsView.findViewById(R.id.swipe_refresh);
        mListView = (ListView) sNewsView.findViewById(android.R.id.list);
        sContentResolver = getActivity().getContentResolver();
        return sNewsView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION_WORK_ITEM))
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION_WORK_ITEM));

        sThothClass = ClassSectionsActivity.getThothClass();

        mListAdapter = new WorkItemsAdapter(getActivity());
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

        Cursor workItemsCursor = sContentResolver.query(ParseUtils.Classes.parseWorkItemsFromClassID(sThothClass.getID()), null, null, null, null);
        if(workItemsCursor.moveToNext()){
            workItemsCursor.close();
            getLoaderManager().initLoader(WORK_ITEMS_CURSOR_LOADER_ID, null, this);
        }
        else {
            workItemsCursor.close();
            refreshAndUpdate();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof CallbackWorkItem)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }
        mCallbacks = (CallbackWorkItem) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDummyCallback;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            outState.putInt(STATE_ACTIVATED_POSITION_WORK_ITEM, mActivatedPosition);
        }
    }

    private void setActivatedPosition(int position) {
        getListView().setItemChecked((position == ListView.INVALID_POSITION) ? mActivatedPosition : position, false);
        mActivatedPosition = position;
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(WORK_ITEMS_CURSOR_LOADER_ID, null, this);
    }

    @Override
    public void onListItemClick(ListView l, View view, int position, long id) {
        super.onListItemClick(l, view, position, id);
        ThothWorkItem workItem = (ThothWorkItem) mListAdapter.getItem(position);
        if (ClassSectionsActivity.isTwoPane()) {
            mListAdapter.setSelectedWorkItemID(workItem.getID());
            mCallbacks.onItemSelected((ThothWorkItem) mListAdapter.getItem(position));
            getLoaderManager().restartLoader(WORK_ITEMS_CURSOR_LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String [] SELECTION_ARGS = new String[]{ String.valueOf(sThothClass.getID()) };
        return new CursorLoader(getActivity(), ThothContract.WorkItems.CONTENT_URI,
                CURSOR_COLUMNS, SELECTION, SELECTION_ARGS, null);
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
        ThothUpdateService.startActionWorkItemsUpdate(getActivity(), sThothClass.getID());
        getLoaderManager().restartLoader(WORK_ITEMS_CURSOR_LOADER_ID, null, this);
        mSwipeRefreshLayout.setRefreshing(false);
    }
}