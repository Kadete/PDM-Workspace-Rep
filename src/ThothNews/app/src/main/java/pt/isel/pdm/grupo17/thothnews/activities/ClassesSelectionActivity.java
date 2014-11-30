package pt.isel.pdm.grupo17.thothnews.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.services.ThothUpdateService;

public class ClassesSelectionActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{

    private SimpleCursorAdapter _adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_class_selection_view);
        final ListView _listView = (ListView) findViewById(R.id.classesList);

        getLoaderManager().initLoader(0,null,this);

        /** TODO Create layout for preference view item */
        _adapter =  new ClassesSelectionAdapter(ClassesSelectionActivity.this,R.layout.layout_class_item,null,
                new String[]{ThothContract.Clazz._ID,ThothContract.Clazz.FULL_NAME,ThothContract.Clazz.TEACHER},
                new int[]{R.id.class_item_id,R.id.class_item_full_name,R.id.class_item_teacher},
                0);
        _listView.setAdapter(
               _adapter);

        ThothUpdateService.startActionClassesUpdate(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, ThothContract.Clazz.CONTENT_URI,
                new String[]{ThothContract.Clazz._ID,ThothContract.Clazz.FULL_NAME,ThothContract.Clazz.TEACHER}
                ,null,null,ThothContract.Clazz.COURSE + " DESC");
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
            _adapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        _adapter.swapCursor(null);
    }
}
class ClassesSelectionAdapter extends SimpleCursorAdapter{

    public ClassesSelectionAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);
//        Toast.makeText(context,cursor.getString(1),Toast.LENGTH_SHORT).show();
    }
}
