package pt.isel.pdm.grupo17.thothnews.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import pt.isel.pdm.grupo17.thothnews.data.providers.SQLiteUtils;
import pt.isel.pdm.grupo17.thothnews.models.ThothClass;
import pt.isel.pdm.grupo17.thothnews.receivers.BgProcessingResultReceiver;
import pt.isel.pdm.grupo17.thothnews.receivers.NetworkReceiver;
import pt.isel.pdm.grupo17.thothnews.services.ThothUpdateService;
import pt.isel.pdm.grupo17.thothnews.utils.ParseUtils;
import pt.isel.pdm.grupo17.thothnews.utils.SettingsUtils;
import pt.isel.pdm.grupo17.thothnews.utils.TagUtils;
import pt.isel.pdm.grupo17.thothnews.view.MultiSwipeRefreshLayout;

import static pt.isel.pdm.grupo17.thothnews.data.providers.SQLiteUtils.TRUE;
import static pt.isel.pdm.grupo17.thothnews.receivers.BgProcessingResultReceiver.*;

public class ClassesPickFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener, Receiver{

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

    public BgProcessingResultReceiver mReceiver;

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
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if(!isActive){
            return;
        }
        switch (resultCode) {
            case STATUS_RUNNING:
                //show progress
                mSwipeRefreshLayout.setRefreshing(true);
                Toast.makeText(getActivity(), "Please Wait", Toast.LENGTH_SHORT).show();
                break;
            case STATUS_FINISHED:
                // hide progress & analyze bundle
                mSwipeRefreshLayout.setRefreshing(false);
                if(!isActive || loader == null)
                    return;
                loader.restartLoader(CLASSES_SELECTION_CURSOR_LOADER_ID, null, this);
                Toast.makeText(getActivity(), "Done", Toast.LENGTH_SHORT).show();
                break;
            case STATUS_ERROR:
                // handle the error;
                mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_grid_classes_pick, container, false);

        mSwipeRefreshLayout = (MultiSwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
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

        // register a receiver for IntentService broadcasts
        mReceiver = new BgProcessingResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        loader = getLoaderManager();
        return view;
    }

    public void updateClassesPicked(boolean toSave){
        final FragmentActivity activity = getActivity();
        if(mListAdapter.getMapSelection().isEmpty()) {
            activity.finish();
            return;
        }
        ContentResolver resolver = activity.getContentResolver();
        int nrClassesToUpdate = 0;
        for(Map.Entry<Long, ClassesPickAdapter.SelectionState> entryClass : mListAdapter.getMapSelection().entrySet()) {
            ContentValues values = new ContentValues();
            boolean enrolled = ((toSave) ? entryClass.getValue().finalState : entryClass.getValue().initialState);
            values.put(ThothContract.Classes.ENROLLED, enrolled ? SQLiteUtils.TRUE : SQLiteUtils.FALSE);
            resolver.update(ParseUtils.Classes.parseClass(entryClass.getKey()), values, null, null );
            if(toSave && enrolled)
                nrClassesToUpdate++;
        }
        if(nrClassesToUpdate != 0)
            ThothUpdateService.startActionNewsUpdate(activity, mReceiver);

        if(toSave){
            Cursor cursor = resolver.query(ThothContract.Classes.ENROLLED_URI, null, selection, selectionArgs, null);
            List<String> classes = new LinkedList<>();
            while(cursor.moveToNext()){
                if(cursor.getString(cursor.getColumnIndex(ThothContract.Classes.ENROLLED)).equals(TRUE))
                    classes.add(cursor.getString(cursor.getColumnIndex(ThothContract.Classes.FULL_NAME)).replaceAll("\\s+",""));
            }
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
            sharedPreferences.edit().putStringSet(TagUtils.TAG_SELECTED_CLASSES, new HashSet<>(classes)).apply();
        }
        clearSelectedList();

        Toast.makeText(activity.getApplicationContext(),getString((toSave)? R.string.classes_pick_ok : R.string.classes_pick_cancel), Toast.LENGTH_SHORT).show();
        activity.finish();
    }


    public void clearSelectedList() {
        ClassesPickAdapter.sMapSelection.clear();
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
        Set<String> semestersSet = sharedPreferences.getStringSet(TagUtils.TAG_MULTILIST_SEMESTERS_PREF_KEY, null);
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

        if(!SettingsUtils.semesterSelectedChanged && classesCursor.moveToNext()){
            getLoaderManager().initLoader(CLASSES_SELECTION_CURSOR_LOADER_ID, null, this);
        }
        else {
            refreshAndUpdate();
            SettingsUtils.semesterSelectedChanged = false;
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
        if(!NetworkReceiver.checkConnection(getActivity(), true)){
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }
        ThothUpdateService.startActionClassesUpdate(getActivity(), mReceiver);
    }

   public void myCreateOptionsMenu(Menu menu) {
       MenuItem searchViewMenuItem = menu.findItem(R.id.action_search);
       SearchView searchView = new SearchView(getActivity());
       searchView.setOnQueryTextListener(this);

       int textId = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
       ((TextView) searchView.findViewById(textId)).setTextColor(Color.WHITE);

       int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
       ImageView v = (ImageView) searchView.findViewById(searchImgId);
       v.setImageResource(R.drawable.ic_action_search);

       searchViewMenuItem.setActionView(searchView);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mCurFilter = !TextUtils.isEmpty(newText) ? newText : null;
        loader.restartLoader(CLASSES_SELECTION_CURSOR_LOADER_ID, null, this);
        return true;
    }
}