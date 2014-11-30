package pt.isel.pdm.grupo17.thothnews.activities;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.models.ThothNew;
import pt.isel.pdm.grupo17.thothnews.services.ThothUpdateService;
import pt.isel.pdm.grupo17.thothnews.utils.DateUtils;
import pt.isel.pdm.grupo17.thothnews.utils.TagUtils;
import pt.isel.pdm.grupo17.thothnews.utils.UriUtils;

import static pt.isel.pdm.grupo17.thothnews.utils.ParseUtils.d;
import static pt.isel.pdm.grupo17.thothnews.utils.TagUtils.TAG_ACTIVITY;
import static pt.isel.pdm.grupo17.thothnews.utils.TagUtils.TAG_ADAPTER;

public class NewsActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    NewsAdapter mAdapter;
    static long sClassID;
    static String sClassName;

    static final int NEWS_CURSOR_LOADER_ID = 2;
    static final int ARG_CLASS_ID_DEFAULT_VALUE = -1;


    static final String[] CURSOR_COLUMNS = {ThothContract.News._ID, ThothContract.News.TITLE,
            ThothContract.News.WHEN_CREATED, ThothContract.News.READ, ThothContract.News.CONTENT};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        sClassID = intent.getLongExtra(TagUtils.TAG_SELECT_CLASS_ID, ARG_CLASS_ID_DEFAULT_VALUE);

        sClassName = intent.getStringExtra(TagUtils.TAG_SELECT_CLASS_NAME);
        getActionBar().setTitle(sClassName);

        mAdapter =  new NewsAdapter(getApplicationContext());
        getListView().setAdapter(mAdapter);
        getLoaderManager().initLoader(NEWS_CURSOR_LOADER_ID, null, this);

        ThothUpdateService.startActionClassNewsUpdate(getApplicationContext(), sClassID);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ThothNew thothNew = (ThothNew) mAdapter.getItem(position);
        long newID = thothNew.getID();

        ContentValues values = new ContentValues();
        values.put(ThothContract.News.READ, 1);
        getContentResolver().update(UriUtils.News.parseFromNewID(newID), values, null, null );

        Intent i = new Intent(this, SingeNewActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra(TagUtils.TAG_SELECT_CLASS_NAME, sClassName);
        i.putExtra(TagUtils.TAG_SELECT_NEW, thothNew);
        startActivity(i);
    }

    @Override
    protected void onStart(){
        super.onStart();
        ActionBar actionbar = this.getActionBar();
        assert actionbar != null;
        actionbar.setDisplayHomeAsUpEnabled(true);
        d(TAG_ACTIVITY, "MainActivity, onStart Called");
    }

    @Override
    protected void onResume(){
        super.onResume();
        d(TAG_ACTIVITY, "MainActivity, onResume Called");
        /* TODO sort*/
    }

    @Override
    protected void onPause(){
        super.onPause();
        d(TAG_ACTIVITY, "MainActivity, onPause Called");
    }

    @Override
    protected void onStop(){
        super.onStop();
        d(TAG_ACTIVITY, "MainActivity, onStop Called");
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        d(TAG_ACTIVITY, "MainActivity, onRestart Called");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        d(TAG_ACTIVITY, "MainActivity, onDestroy Called");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
                startActivity(new Intent(NewsActivity.this, PreferencesActivity.class));
                return true;
            case R.id.action_refresh_all:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = ThothContract.News.CLASS_ID + " = ? ";
        String [] selectionArgs = new String[]{ String.valueOf(sClassID) };
        return new CursorLoader(this, ThothContract.News.CONTENT_URI, CURSOR_COLUMNS , selection, selectionArgs, ThothContract.News.READ +", "+ ThothContract.News.WHEN_CREATED + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}

class NewsAdapter extends CursorAdapter {

    class NewViewHolder {
        public TextView id;
        public TextView title;
        public TextView when;
        public CheckBox checkRead;
    }

    static final int READ = 1;

    static LayoutInflater sLayoutInflater = null;
    List<ThothNew> mNews = new ArrayList<ThothNew>();
    Context mContext;

    public NewsAdapter(Context context) {
        super(context, null, 0);
        mContext = context;
        sLayoutInflater = LayoutInflater.from(mContext);
    }

//    public void clearList() {
//        mNews.clear();
//        mContext.getContentResolver().delete(ThothContract.News.CONTENT_URI, null, null);
//        notifyDataSetChanged();
//    }

    @Override
    public Object getItem(int position) {
        return mNews.get(position);
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        Cursor oldCursor = super.swapCursor(newCursor);
        mNews.clear();
        if (newCursor !=null) {
            newCursor.moveToFirst();
            while(!newCursor.isAfterLast()) {
                ThothNew thothNew = ThothNew.fromCursor(newCursor);
                mNews.add(thothNew);
                newCursor.moveToNext();
            }
        }
        notifyDataSetChanged();
        return oldCursor;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        NewViewHolder holder = new NewViewHolder();
        View newView = sLayoutInflater.inflate(R.layout.layout_new_item, null);

        holder.id = (TextView)newView.findViewById(R.id.new_item_id);
        holder.title = (TextView)newView.findViewById(R.id.new_item_title);
        holder.when = (TextView)newView.findViewById(R.id.new_item_when);
        holder.checkRead = (CheckBox)newView.findViewById(R.id.new_item_checkread);

        newView.setTag(holder);
        return newView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        NewViewHolder holder = (NewViewHolder)view.getTag();

        holder.id.setText(cursor.getString(cursor.getColumnIndex(ThothContract.News._ID)));
        holder.title.setText(cursor.getString(cursor.getColumnIndex(ThothContract.News.TITLE)));

        Date date = new Date();
        try {
            date = DateUtils.SAVE_DATE_FORMAT.parse(cursor.getString(cursor.getColumnIndex(ThothContract.News.WHEN_CREATED)));
        } catch (ParseException e) {
            d(TAG_ADAPTER, "FAIL TO PARSE DATE");
        }
        String dateStr = DateUtils.SHOW_DATE_FORMAT.format(date);
        holder.when.setText(dateStr);

        int aux = cursor.getInt(cursor.getColumnIndex(ThothContract.News.READ));
        Boolean read = aux == READ;
        holder.checkRead.setChecked(read);
        holder.title.setTypeface(null, (!read) ? Typeface.BOLD : Typeface.NORMAL);
        holder.when.setTypeface(null, (!read) ? Typeface.BOLD : Typeface.NORMAL);
        view.setBackground(new ColorDrawable((!read) ? 0x44440000 : 0x44444444));

    }
}