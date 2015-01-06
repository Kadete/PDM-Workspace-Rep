package pt.isel.pdm.grupo17.thothnews.fragments;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.activities.ClassSectionsActivity;
import pt.isel.pdm.grupo17.thothnews.adapters.ClassesPickAdapter;
import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.models.ThothClass;
import pt.isel.pdm.grupo17.thothnews.services.ThothUpdateService;
import pt.isel.pdm.grupo17.thothnews.utils.ConnectionUtils;
import pt.isel.pdm.grupo17.thothnews.utils.SQLiteUtils;
import pt.isel.pdm.grupo17.thothnews.utils.TagUtils;
import pt.isel.pdm.grupo17.thothnews.utils.UriUtils;
import pt.isel.pdm.grupo17.thothnews.view.MultiSwipeRefreshLayout;

import static pt.isel.pdm.grupo17.thothnews.utils.SQLiteUtils.TRUE;

public class ClassesPickFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener{

    private static final boolean CANCEL = false;
    private static final boolean SAVE = true;

    private static final int CLASSES_SELECTION_CURSOR_LOADER_ID = 1;
    private static final String[] CURSOR_COLUMNS = {ThothContract.Classes._ID, ThothContract.Classes.FULL_NAME, ThothContract.Classes.TEACHER_NAME, ThothContract.Classes.SHORT_NAME,
                                            ThothContract.Classes.SEMESTER, ThothContract.Classes.COURSE, ThothContract.Classes.TEACHER_ID, ThothContract.Classes.ENROLLED};
    private static final String ORDER_BY = ThothContract.Classes.SEMESTER + " DESC" + ", " + ThothContract.Classes.COURSE;

    private static String selection = null, selectionArgs[] = null;

    private MultiSwipeRefreshLayout mSwipeRefreshLayout;
    private GridView mGridView;
    private View mEmptyView;
    private ClassesPickAdapter mListAdapter;
    private String mCurFilter;
    private SearchView mSearchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_grid_classes_pick, container, false);

        mSwipeRefreshLayout = (MultiSwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        mGridView = (GridView) view.findViewById(android.R.id.list);
        mEmptyView = view.findViewById(android.R.id.empty);

        final Button cancelBtn = (Button) view.findViewById(R.id.BtnDiscard);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateClassesPicked(CANCEL);
            }
        });

        final Button okBtn = (Button) view.findViewById(R.id.BtnSave);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateClassesPicked(SAVE);
            }
        });

        return view;
    }

    public void updateClassesPicked(boolean toSave){
        final FragmentActivity activity = getActivity();
        if(mListAdapter.getMapSelection().isEmpty()) {
            activity.finish();
            return;
        }
        ContentResolver resolver = activity.getContentResolver();
        for(Map.Entry<Long, ClassesPickAdapter.SelectionState> entryClass : mListAdapter.getMapSelection().entrySet()) {
            ContentValues values = new ContentValues();
            boolean enrolled = ((toSave) ? entryClass.getValue().finalState : entryClass.getValue().initialState);
            values.put(ThothContract.Classes.ENROLLED, enrolled ? SQLiteUtils.TRUE : SQLiteUtils.FALSE);
            resolver.update(UriUtils.Classes.parseClass(entryClass.getKey()), values, null, null );
            if(toSave && enrolled)
                ThothUpdateService.startActionClassNewsUpdate(activity, entryClass.getKey());
        }
        if(toSave){
            Cursor cursor = resolver.query(ThothContract.Classes.ENROLLED_URI, null, selection, selectionArgs, null);
            List<String> classes = new LinkedList<>();
            while(cursor.moveToNext()){
                if(cursor.getString(cursor.getColumnIndex(ThothContract.Classes.ENROLLED)).equals(TRUE))
                    classes.add(cursor.getString(cursor.getColumnIndex(ThothContract.Classes.FULL_NAME)).replaceAll("\\s+",""));
            }
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
            sharedPreferences.edit().putStringSet(TagUtils.TAG_CLASSES_SELECTED, new HashSet<>(classes)).apply();
        }
        Toast.makeText(activity.getApplicationContext(),getString((toSave)? R.string.classes_pick_ok : R.string.classes_pick_cancel), Toast.LENGTH_LONG).show();
        activity.finish();
    }

    /**runs after the View (onCreateView) has been created.**/
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListAdapter = new ClassesPickAdapter(getActivity());
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

        mSwipeRefreshLayout.setSwipeableChildren(android.R.id.list, android.R.id.empty);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshAndUpdate();
            }
        });
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN);

        /** FILTER SEMESTERS SELECTED **/
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Set<String> semestersSet = sharedPreferences.getStringSet(TagUtils.TAG_MULTILIST_SEMESTERS_KEY, null);
        if(!semestersSet.isEmpty()) {
            selection = "";
            selectionArgs = new String[semestersSet.size()];
            Iterator<String> iterator = semestersSet.iterator();
            int i = 0;
            while(iterator.hasNext()){
                selection += ThothContract.Classes.SEMESTER + " LIKE ?";
                selectionArgs[i++] =  iterator.next();
                if(iterator.hasNext()){
                    selection += " OR ";
                }
            }
        }
        /********************************/

        Cursor classesCursor = getActivity().getContentResolver()
                .query(ThothContract.Classes.CONTENT_URI, null, selection, selectionArgs, null);

        if(classesCursor.moveToNext()){
            classesCursor.close();
            getLoaderManager().initLoader(CLASSES_SELECTION_CURSOR_LOADER_ID, null, this);
        }
        else {
            classesCursor.close();
            Toast.makeText(getActivity(),getString(R.string.toast_wait_message),Toast.LENGTH_SHORT).show();
            refreshAndUpdate();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri baseUri;
        if (mCurFilter != null) {
            baseUri = Uri.withAppendedPath(ThothContract.Classes.SEARCH_URI,
                    Uri.encode(mCurFilter));
        } else {
            baseUri = ThothContract.Classes.CONTENT_URI;
        }
        return new CursorLoader(getActivity(), baseUri, CURSOR_COLUMNS, selection, selectionArgs, ORDER_BY);
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
        if(!ConnectionUtils.isConnected(getActivity()))
            return;

        mSwipeRefreshLayout.setRefreshing(true);
        ThothUpdateService.startActionClassesUpdate(getActivity());
        getLoaderManager().restartLoader(CLASSES_SELECTION_CURSOR_LOADER_ID, null, this);
        mSwipeRefreshLayout.setRefreshing(false);
    }

   public void myCreateOptionsMenu(Menu menu) {
       MenuItem searchViewMenuItem = menu.findItem(R.id.action_search);
       mSearchView = new SearchView(getActivity());
       mSearchView.setOnQueryTextListener(this);

       int textId = mSearchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
       ((TextView) mSearchView.findViewById(textId)).setTextColor(Color.WHITE);

       int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
       ImageView v = (ImageView) mSearchView.findViewById(searchImgId);
       v.setImageResource(R.drawable.ic_action_search);

       searchViewMenuItem.setActionView(mSearchView);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mCurFilter = !TextUtils.isEmpty(newText) ? newText : null;
        getLoaderManager().restartLoader(CLASSES_SELECTION_CURSOR_LOADER_ID, null, this);
        return true;
    }
}