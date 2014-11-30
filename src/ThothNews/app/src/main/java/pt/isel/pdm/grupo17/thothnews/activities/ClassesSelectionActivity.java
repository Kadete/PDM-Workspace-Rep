package pt.isel.pdm.grupo17.thothnews.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.models.ThothClass;
import pt.isel.pdm.grupo17.thothnews.services.ThothUpdateService;
import pt.isel.pdm.grupo17.thothnews.utils.UriUtils;

import static pt.isel.pdm.grupo17.thothnews.utils.SQLiteUtils.FALSE;
import static pt.isel.pdm.grupo17.thothnews.utils.SQLiteUtils.TRUE;

public class ClassesSelectionActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{

    ClassesSelectionAdapter mAdapter;
    ListView mListView;
    static final int CLASSES_CURSOR_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_class_selection_view);

        mAdapter =  new ClassesSelectionAdapter(this);

        mListView = (ListView) findViewById(R.id.classesList);
        mListView.setAdapter(mAdapter);

        getLoaderManager().initLoader(CLASSES_CURSOR_LOADER_ID, null, this);
        ThothUpdateService.startActionClassesUpdate(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String OrderBy = ThothContract.Clazz.SEMESTER + " DESC" + ", " + ThothContract.Clazz.COURSE;
        String[] columns_to_return = {ThothContract.Clazz._ID,ThothContract.Clazz.FULL_NAME,ThothContract.Clazz.TEACHER, ThothContract.Clazz.ENROLLED};
        return new CursorLoader(this, ThothContract.Clazz.CONTENT_URI, columns_to_return , null, null, OrderBy);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        final TextView _tv1 = (TextView) findViewById(R.id.tv1);
        final ProgressBar bar = (ProgressBar)findViewById(R.id.classes_progress);
        bar.setVisibility(View.GONE);
        if(data.getCount() == 0){
            _tv1.setVisibility(View.VISIBLE);
        }else{
            _tv1.setVisibility(View.GONE);
            mAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}


class ClassesSelectionAdapter extends CursorAdapter {

    static class ClassViewHolder{
        public TextView id;
        public TextView full_name;
        public TextView teacher;
        public CheckBox checkBox;
    }

    static LayoutInflater sLayoutInflater = null;
    List<ThothClass> mClasses = new ArrayList<ThothClass>();
    Context mContext;

    public ClassesSelectionAdapter(Context context) {
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
        View newView = sLayoutInflater.inflate(R.layout.layout_class_selection_item, null);

        holder.id = (TextView)newView.findViewById(R.id.class_selection_item_id);
        holder.full_name = (TextView)newView.findViewById(R.id.class_selection_item_full_name);
        holder.teacher = (TextView)newView.findViewById(R.id.class_selection_item_teacher);
        holder.checkBox = (CheckBox)newView.findViewById(R.id.class_selection_item_checkEnrolled);

        newView.setTag(holder);
        return newView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ClassViewHolder holder = (ClassViewHolder)view.getTag();

        Long classeID = cursor.getLong(cursor.getColumnIndex(ThothContract.Clazz._ID));

        holder.id.setText(String.valueOf(classeID));
        holder.full_name.setText(cursor.getString(cursor.getColumnIndex(ThothContract.Clazz.FULL_NAME)));
        holder.teacher.setText(cursor.getString(cursor.getColumnIndex(ThothContract.Clazz.TEACHER)));

        Boolean isEnrolled = cursor.getString(cursor.getColumnIndex(ThothContract.Clazz.ENROLLED)).equals(TRUE);
        holder.checkBox.setChecked(isEnrolled);

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                values.put(ThothContract.Clazz.ENROLLED, (holder.checkBox.isChecked()) ? TRUE : FALSE);

                long classID = Long.valueOf(holder.id.getText().toString());
                mContext.getContentResolver().update(UriUtils.Classes.parseClasseID(classID), values, null, null );

                ThothUpdateService.startActionClassNewsUpdate(mContext, classID);
            }
        });

        view.setBackground(new ColorDrawable((isEnrolled) ? 0x44440000 : 0x44444444));
    }
}
