package pt.isel.pdm.grupo17.thothnews.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import java.util.Map;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.activities.ClassSectionsActivity;
import pt.isel.pdm.grupo17.thothnews.adapters.ClassesSelectionAdapter;
import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.models.ThothClass;
import pt.isel.pdm.grupo17.thothnews.services.ThothUpdateService;
import pt.isel.pdm.grupo17.thothnews.utils.SQLiteUtils;
import pt.isel.pdm.grupo17.thothnews.utils.TagUtils;
import pt.isel.pdm.grupo17.thothnews.utils.UriUtils;
import pt.isel.pdm.grupo17.thothnews.view.MultiSwipeRefreshLayout;

public class ClassesSelectionFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static final int CLASSES_SELECTION_CURSOR_LOADER_ID = 1;
    static final String[] CURSOR_COLUMNS = {ThothContract.Clazz._ID, ThothContract.Clazz.FULL_NAME, ThothContract.Clazz.TEACHER_NAME, ThothContract.Clazz.SHORT_NAME,
                                            ThothContract.Clazz.SEMESTER, ThothContract.Clazz.COURSE, ThothContract.Clazz.TEACHER_ID, ThothContract.Clazz.ENROLLED};

    private MultiSwipeRefreshLayout mSwipeRefreshLayout;
    private GridView mGridView;
    private View mEmptyView;
    private ClassesSelectionAdapter mListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_grid_classes_selection, container, false);

        mSwipeRefreshLayout = (MultiSwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        mGridView = (GridView) view.findViewById(android.R.id.list);
        mEmptyView = view.findViewById(android.R.id.empty);

        final FragmentActivity activity = getActivity();

        final Button cancelBtn = (Button) view.findViewById(R.id.BtnDiscard);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateClassesSelection(activity, false);
            }
        });

        final Button okBtn = (Button) view.findViewById(R.id.BtnSave);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateClassesSelection(activity, true);
                ThothUpdateService.startActionNewsUpdate(getActivity());
            }
        });

        return view;
    }

    public void updateClassesSelection(FragmentActivity activity, boolean toSave){
        for(Map.Entry<Long, ClassesSelectionAdapter.SelectionState> entryClass : mListAdapter.getMapSelection().entrySet()) {
            ContentValues values = new ContentValues();
            boolean enrolled = ((toSave) ? entryClass.getValue().finalState : entryClass.getValue().initialState);
            values.put(ThothContract.Clazz.ENROLLED, enrolled ? SQLiteUtils.TRUE : SQLiteUtils.FALSE);
            activity.getContentResolver().update(UriUtils.Classes.parseClass(entryClass.getKey()), values, null, null );
            if(toSave)
                ThothUpdateService.startActionClassNewsUpdate(activity, entryClass.getKey());
        }
        Toast.makeText(activity.getApplicationContext(),getString((toSave)? R.string.class_selection_ok : R.string.class_selection_cancel), Toast.LENGTH_LONG).show();
        activity.finish();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListAdapter = new ClassesSelectionAdapter(getActivity());

        mGridView.setAdapter(mListAdapter);
        mGridView.setEmptyView(mEmptyView);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ThothClass clazz = (ThothClass) mListAdapter.getItem(position);
                Intent i = new Intent(getActivity(), ClassSectionsActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra(TagUtils.TAG_SERIALIZABLE_CLASS, clazz);
                startActivity(i);
            }
        });

        mSwipeRefreshLayout.setSwipeableChildren(android.R.id.list, android.R.id.empty);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLoader();
            }
        });

        getLoaderManager().initLoader(CLASSES_SELECTION_CURSOR_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String OrderBy = ThothContract.Clazz.SEMESTER + " DESC" + ", " + ThothContract.Clazz.COURSE;
        return new CursorLoader(getActivity(), ThothContract.Clazz.CONTENT_URI, CURSOR_COLUMNS, null, null, OrderBy);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mListAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mListAdapter.swapCursor(null);
    }

    public void refreshLoader() {
        Toast.makeText(getActivity(),getString(R.string.toast_wait_message),Toast.LENGTH_LONG).show();
        mSwipeRefreshLayout.setRefreshing(true);
        ThothUpdateService.startActionClassesUpdate(getActivity());
        getLoaderManager().restartLoader(CLASSES_SELECTION_CURSOR_LOADER_ID, null, this);
        mSwipeRefreshLayout.setRefreshing(false);

    }
}