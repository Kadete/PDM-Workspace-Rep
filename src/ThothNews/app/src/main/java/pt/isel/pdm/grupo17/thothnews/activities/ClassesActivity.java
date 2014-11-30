package pt.isel.pdm.grupo17.thothnews.activities;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.models.ThothClass;
import pt.isel.pdm.grupo17.thothnews.services.ThothUpdateService;
import pt.isel.pdm.grupo17.thothnews.utils.TagUtils;
import pt.isel.pdm.grupo17.thothnews.utils.UriUtils;

import static pt.isel.pdm.grupo17.thothnews.utils.ParseUtils.d;
import static pt.isel.pdm.grupo17.thothnews.utils.TagUtils.TAG_ACTIVITY;

public class ClassesActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    ClassesAdapter mAdapter;
    static final int CLASSES_CURSOR_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle(R.string.label_activity_classes);

        /** código para simular turmas escolhidas nas preferencias **/
        /** TODO: CLEAN CODE **/
        ContentValues values = new ContentValues();
        values.put(ThothContract.Clazz.ENROLLED, 1);

        int[] args = new int[] {339, 340,348,360,361,373};
        for(int i = 0; i < args.length; i++){
            String selection = ThothContract.Clazz._ID + " = ? ";
            String []selectionArgs = new String[]{String.valueOf(args[i])};
            getContentResolver().update(ThothContract.Clazz.CONTENT_URI, values, selection, selectionArgs );
        }
        /***********************************************************/

        mAdapter =  new ClassesAdapter(getApplicationContext());
        getListView().setAdapter(mAdapter);
        getLoaderManager().initLoader(CLASSES_CURSOR_LOADER_ID, null, this);

        ThothUpdateService.startActionClassesUpdate(getApplicationContext());
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ThothClass clazz = (ThothClass) mAdapter.getItem(position);
        Intent i = new Intent(this, NewsActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra(TagUtils.TAG_SELECT_CLASS_NAME, clazz.getFullName());
        i.putExtra(TagUtils.TAG_SELECT_CLASS_ID, clazz.getID());
        startActivity(i);
    }

    @Override
    public void onResume() {
        super.onResume();
        d(TAG_ACTIVITY, "ClassesActivity: onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        d(TAG_ACTIVITY,"ClassesActivity: onPause()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_classes_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity( new Intent(new Intent(ClassesActivity.this, PreferencesActivity.class)));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String OrderBy = ThothContract.Clazz.SEMESTER+ ", "+ ThothContract.Clazz.FULL_NAME + " ASC";
        String[] columns_to_return = {ThothContract.Clazz._ID,ThothContract.Clazz.FULL_NAME,ThothContract.Clazz.TEACHER, ThothContract.Clazz.UNREAD_NEWS};
        return new CursorLoader(this, ThothContract.Clazz.ENROLLED_URI, columns_to_return , null, null, OrderBy);
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

class ClassesAdapter extends CursorAdapter {

    static class ClassViewHolder{
        public TextView id;
        public TextView full_name;
        public TextView teacher;
        public ImageView new_news;
    }

    static final String FALSE = "0";
    static LayoutInflater sLayoutInflater = null;
    List<ThothClass> mClasses = new ArrayList<ThothClass>();
    Context mContext;

    public ClassesAdapter(Context context) {
        super(context, null, 0);
        mContext = context;
        sLayoutInflater = LayoutInflater.from(context);
    }

//    public void clearList() {
//        mClasses.clear();
//        mContext.getContentResolver().delete(ThothContract.Clazz.CONTENT_URI, null, null);
//        notifyDataSetChanged();
//    }

    @Override
    public Object getItem(int position) {
        return mClasses.get(position);
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        Cursor oldCursor = super.swapCursor(newCursor);
        mClasses.clear();
        if (newCursor !=null) {
            newCursor.moveToFirst();
            while(!newCursor.isAfterLast()) {
                ThothClass clazz = ThothClass.fromCursor(newCursor);
                mClasses.add(clazz);
                newCursor.moveToNext();
            }
        }
        notifyDataSetChanged();
        return oldCursor;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        ClassViewHolder holder = new ClassViewHolder();
        View newView = sLayoutInflater.inflate(R.layout.layout_class_item, null);

        holder.id = (TextView)newView.findViewById(R.id.class_item_id);
        holder.full_name = (TextView)newView.findViewById(R.id.class_item_full_name);
        holder.teacher = (TextView)newView.findViewById(R.id.class_item_teacher);
        holder.new_news = (ImageView)newView.findViewById(R.id.class_item_new_news_ic);

        newView.setTag(holder);
        return newView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ClassViewHolder holder = (ClassViewHolder)view.getTag();

        Long classeID = cursor.getLong(cursor.getColumnIndex(ThothContract.Clazz._ID));

        holder.id.setText(String.valueOf(classeID));
        holder.full_name.setText(cursor.getString(cursor.getColumnIndex(ThothContract.Clazz.FULL_NAME)));
        holder.teacher.setText(cursor.getString(cursor.getColumnIndex(ThothContract.Clazz.TEACHER)));

        String selection = ThothContract.News.READ + " =  ? ";
        String [] selectionArgs =  new String[] { FALSE };
        String orderBy = ThothContract.News.READ;
        Uri classNewsUri = UriUtils.Classes.parseNewsFromClasseID(classeID);
        Cursor cursorNewsRead = mContext.getContentResolver().query(classNewsUri, new String[] {ThothContract.Clazz._ID}, selection, selectionArgs, orderBy);

        Boolean newsToRead = cursorNewsRead.moveToNext();
        holder.new_news.setVisibility((newsToRead)? View.VISIBLE : View.GONE);
        cursorNewsRead.close();
        view.setBackground(new ColorDrawable((newsToRead) ? 0x44440000 : 0x44444444));
    }
}
