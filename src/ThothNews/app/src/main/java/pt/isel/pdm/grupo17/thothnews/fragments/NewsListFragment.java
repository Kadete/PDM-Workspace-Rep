package pt.isel.pdm.grupo17.thothnews.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.activities.NewsActivity;
import pt.isel.pdm.grupo17.thothnews.activities.SingeNewActivity;
import pt.isel.pdm.grupo17.thothnews.adapters.NewsAdapter;
import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.models.ThothNew;
import pt.isel.pdm.grupo17.thothnews.services.ThothUpdateService;
import pt.isel.pdm.grupo17.thothnews.utils.TagUtils;

public class NewsListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{

    NewsAdapter mAdapter;
    long sClassID;
    String sClassName;
    static final int ARG_CLASS_ID_DEFAULT_VALUE = -1;
    static final int NEWS_CURSOR_LOADER_ID = 2;

    static final String[] CURSOR_COLUMNS = {ThothContract.News._ID, ThothContract.News.TITLE,
            ThothContract.News.WHEN_CREATED, ThothContract.News.READ, ThothContract.News.CONTENT};

    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private Callbacks mCallbacks = sDummyCallbacks;
    public static int mActivatedPosition = ListView.INVALID_POSITION;

    public interface Callbacks {
        public void onItemSelected(ThothNew thothNew);
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(ThothNew thothNew) {
        }
    };

    public NewsListFragment() {
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }

        Intent intent = getActivity().getIntent();
        sClassID = intent.getLongExtra(TagUtils.TAG_SELECT_CLASS_ID, ARG_CLASS_ID_DEFAULT_VALUE);
        sClassName = intent.getStringExtra(TagUtils.TAG_SELECT_CLASS_NAME);

        mAdapter = new NewsAdapter(getActivity());
        setListAdapter(mAdapter);

        getLoaderManager().initLoader(NEWS_CURSOR_LOADER_ID, null, this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    public void setActivateOnItemClick(boolean activateOnItemClick) {
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        getListView().setItemChecked((position == ListView.INVALID_POSITION) ? mActivatedPosition : position, false);
        mActivatedPosition = position;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(mAdapter.isEmpty())
            ThothUpdateService.startActionClassNewsUpdate(getActivity(), sClassID);
        refreshLoader();
    }

    @Override
    public void onListItemClick(ListView l, View view, int position, long id) {
        super.onListItemClick(l, view, position, id);
        ThothNew thothNew = (ThothNew) mAdapter.getItem(position);
        if (NewsActivity.mTwoPane) {
            NewsAdapter.setSelectedNewID(thothNew.getID());
            mCallbacks.onItemSelected((ThothNew) mAdapter.getItem(position));
        } else {
            Intent i = new Intent(getActivity(), SingeNewActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra(TagUtils.TAG_SELECT_CLASS_NAME, sClassName);
            i.putExtra(TagUtils.TAG_SERIALIZABLE_LIST, mAdapter.getNewsList());
            i.putExtra(TagUtils.TAG_SELECT_NEW_POS, position);
            startActivity(i);
        }
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = ThothContract.News.CLASS_ID + " = ? ";
        String [] selectionArgs = new String[]{ String.valueOf(sClassID) };
        return new android.support.v4.content.CursorLoader(getActivity(), ThothContract.News.CONTENT_URI,
                CURSOR_COLUMNS, selection, selectionArgs, ThothContract.News.READ +", "+ ThothContract.News.WHEN_CREATED + " DESC");
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> cursorLoader, Cursor cursor) {
        final TextView _tv1 = (TextView) getActivity().findViewById(R.id.tv_error_news);
        _tv1.setVisibility((cursor.getCount() == 0) ? View.VISIBLE: View.GONE);
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }

    public void refreshLoader() {
        getLoaderManager().restartLoader(NEWS_CURSOR_LOADER_ID, null, this);
    }

}