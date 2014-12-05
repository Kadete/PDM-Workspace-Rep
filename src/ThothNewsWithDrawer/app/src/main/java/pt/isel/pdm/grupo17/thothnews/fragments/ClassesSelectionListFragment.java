package pt.isel.pdm.grupo17.thothnews.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.TextView;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.adapters.ClassesSelectionAdapter;
import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.services.ThothUpdateService;

public class ClassesSelectionListFragment extends ListFragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    ClassesSelectionAdapter mAdapter;
    static final int CLASSES_SELECTION_CURSOR_LOADER_ID = 1;
    static final String[] CURSOR_COLUMNS = {ThothContract.Clazz._ID, ThothContract.Clazz.FULL_NAME, ThothContract.Clazz.TEACHER, ThothContract.Clazz.ENROLLED};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new ClassesSelectionAdapter(getActivity());
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(CLASSES_SELECTION_CURSOR_LOADER_ID, null, this);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String OrderBy = ThothContract.Clazz.SEMESTER + " DESC" + ", " + ThothContract.Clazz.COURSE;
        return new android.support.v4.content.CursorLoader(getActivity(), ThothContract.Clazz.CONTENT_URI, CURSOR_COLUMNS, null, null, OrderBy);
    }

    @Override
    public void onResume(){
        super.onResume();
        if(mAdapter.isEmpty())
            ThothUpdateService.startActionClassesUpdate(getActivity());
        refreshLoader();
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> cursorLoader, Cursor cursor) {
        final TextView _tv1 = (TextView) getActivity().findViewById(R.id.tv_error_class_selection);
        _tv1.setVisibility((cursor.getCount() == 0) ? View.VISIBLE: View.GONE);
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }

    public void refreshLoader() {
        getLoaderManager().restartLoader(CLASSES_SELECTION_CURSOR_LOADER_ID, null, this);
    }
}