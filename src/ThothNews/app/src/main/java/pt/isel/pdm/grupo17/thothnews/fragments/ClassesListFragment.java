package pt.isel.pdm.grupo17.thothnews.fragments;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.activities.NewsActivity;
import pt.isel.pdm.grupo17.thothnews.adapters.ClassesAdapter;
import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.models.ThothClass;
import pt.isel.pdm.grupo17.thothnews.services.ThothUpdateService;
import pt.isel.pdm.grupo17.thothnews.utils.TagUtils;

public class ClassesListFragment extends ListFragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    ClassesAdapter mAdapter;
    static final int CLASSES_CURSOR_LOADER_ID = 0;
    static final String[] CURSOR_COLUMNS = {ThothContract.Clazz._ID,ThothContract.Clazz.FULL_NAME,ThothContract.Clazz.TEACHER, ThothContract.Clazz.UNREAD_NEWS};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new ClassesAdapter(getActivity());
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(CLASSES_CURSOR_LOADER_ID, null, this);
    }

    @Override
    public void onResume(){
        super.onResume();
        if(mAdapter.isEmpty())
            ThothUpdateService.startActionClassesUpdate(getActivity());
        refreshLoader();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ThothClass clazz = (ThothClass) mAdapter.getItem(position);
        Intent i = new Intent(getActivity(), NewsActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra(TagUtils.TAG_SELECT_CLASS_NAME, clazz.getFullName());
        i.putExtra(TagUtils.TAG_SELECT_CLASS_ID, clazz.getID());
        startActivity(i);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String OrderBy = ThothContract.Clazz.SEMESTER + " DESC, "+ ThothContract.Clazz.FULL_NAME + " ASC";
        return new android.support.v4.content.CursorLoader(getActivity(), ThothContract.Clazz.ENROLLED_URI, CURSOR_COLUMNS , null, null, OrderBy);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        final TextView _tv1 = (TextView) getActivity().findViewById(R.id.tv_error_class);
        _tv1.setVisibility((cursor.getCount() == 0) ? View.VISIBLE: View.GONE);
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }

    public void refreshLoader() {
        getLoaderManager().restartLoader(CLASSES_CURSOR_LOADER_ID, null, this);
    }

}