package pt.isel.pdm.grupo17.thothnews.fragments;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.activities.ClassSectionsActivity;
import pt.isel.pdm.grupo17.thothnews.adapters.ParticipantsAdapter;
import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.models.ThothClass;
import pt.isel.pdm.grupo17.thothnews.receivers.BgProcessingResultReceiver;
import pt.isel.pdm.grupo17.thothnews.receivers.NetworkReceiver;
import pt.isel.pdm.grupo17.thothnews.services.ThothUpdateService;
import pt.isel.pdm.grupo17.thothnews.utils.ParseUtils;
import pt.isel.pdm.grupo17.thothnews.view.MultiSwipeRefreshLayout;

import static pt.isel.pdm.grupo17.thothnews.receivers.BgProcessingResultReceiver.*;

public class ParticipantsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, Receiver {

    public static ParticipantsFragment newInstance() {
        Bundle bundle = new Bundle();
        ParticipantsFragment fragment = new ParticipantsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private static final int PARTICIPANTS_CURSOR_LOADER_ID = 3;
    private static final String[] CURSOR_COLUMNS = {ThothContract.Students._ID, ThothContract.Students.FULL_NAME, ThothContract.Avatars.AVATAR_URL, ThothContract.Avatars.AVATAR_PATH,
            ThothContract.Students.ACADEMIC_EMAIL, ThothContract.Students.ENROLLED_DATE, ThothContract.Classes_Students.GROUP};
    private static final String ORDER_BY = ThothContract.Students._ID + " ASC";

    private MultiSwipeRefreshLayout mSwipeRefreshLayout;
    private GridView mGridView;
    private View mEmptyView;
    private ParticipantsAdapter mListAdapter;

    private static ThothClass sThothClass;
    private static ContentResolver sContentResolver;

    public BgProcessingResultReceiver mReceiver;
    static boolean isActive = false;

    @Override
    public void onStart() {
        super.onStart();
        isActive = true;
    }

    @Override
    public void onStop() {
        super.onStop();
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
                break;
            case STATUS_FINISHED:
                // hide progress & analyze bundle
                mSwipeRefreshLayout.setRefreshing(false);
                getLoaderManager().restartLoader(PARTICIPANTS_CURSOR_LOADER_ID, null, this);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_section_participants, container, false);
        mSwipeRefreshLayout = (MultiSwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mGridView = (GridView) view.findViewById(android.R.id.list);
        mEmptyView = view.findViewById(android.R.id.empty);
        sContentResolver = getActivity().getContentResolver();

        // register a receiver for IntentService broadcasts
        mReceiver = new BgProcessingResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sThothClass = ClassSectionsActivity.getThothClass();

        mListAdapter = new ParticipantsAdapter(getActivity());
        AlphaInAnimationAdapter animationAdapter = new AlphaInAnimationAdapter(mListAdapter);
        animationAdapter.setAbsListView(mGridView);
        mGridView.setAdapter(animationAdapter);
        mGridView.setEmptyView(mEmptyView);

        mSwipeRefreshLayout.setSwipeableChildren(android.R.id.list, android.R.id.empty);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshAndUpdate();
            }
        });
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN);
    }

    @Override
    public void onResume(){
        super.onResume();
        try(Cursor studentsCursor = sContentResolver.query(ParseUtils.Classes.parseParticipantsFromClassID(sThothClass.getID()), null, null, null, null)){
            if(studentsCursor.moveToNext())
                getLoaderManager().initLoader(PARTICIPANTS_CURSOR_LOADER_ID, null, this);
            else
                refreshAndUpdate();
        }
    }

    private boolean isFragmentUIActive() {
        return isVisible() && isAdded() && !isDetached() && !isRemoving();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if(isFragmentUIActive()){
            return new CursorLoader(getActivity(),
                ParseUtils.Classes.parseParticipantsFromClassID(sThothClass.getID()), CURSOR_COLUMNS, null, null, ORDER_BY);
        }
        return null;
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
        ThothUpdateService.startActionClassParticipantsUpdate(getActivity(), mReceiver, sThothClass.getID());
    }

}
