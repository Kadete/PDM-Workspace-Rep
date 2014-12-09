package pt.isel.pdm.grupo17.thothnews.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.models.ThothClass;
import pt.isel.pdm.grupo17.thothnews.services.ThothUpdateService;
import pt.isel.pdm.grupo17.thothnews.utils.UriUtils;

import static pt.isel.pdm.grupo17.thothnews.utils.SQLiteUtils.FALSE;
import static pt.isel.pdm.grupo17.thothnews.utils.SQLiteUtils.TRUE;

public class ClassesSelectionAdapter extends CursorAdapter {

    static class ClassViewHolder{
        public TextView id;
        public TextView full_name;
        public TextView teacher;
        public CheckBox checkBox;
    }

    static LayoutInflater sLayoutInflater = null;
    List<ThothClass> mClasses = new ArrayList<>();

    public class SelectionState{
        public boolean initialState;
        public boolean finalState;

        public SelectionState(boolean initial){
            initialState = initial;
            finalState = !initial;
        }
    }

    static Map<Long,SelectionState> sMapSelection = new HashMap<>();
    public Map<Long,SelectionState> getMapSelection (){
        return sMapSelection;
    }

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
        View newView = sLayoutInflater.inflate(R.layout.item_class_selection, null);

        holder.id = (TextView)newView.findViewById(R.id.class_selection_item_id);
        holder.full_name = (TextView)newView.findViewById(R.id.class_selection_item_full_name);
        holder.teacher = (TextView)newView.findViewById(R.id.class_selection_item_teacher);
        holder.checkBox = (CheckBox)newView.findViewById(R.id.class_selection_item_checkEnrolled);

        newView.setTag(holder);
        return newView;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final ClassViewHolder holder = (ClassViewHolder)view.getTag();

        long id = cursor.getLong(cursor.getColumnIndex(ThothContract.Clazz._ID));
        holder.id.setText(String.valueOf(id));
        holder.full_name.setText(cursor.getString(cursor.getColumnIndex(ThothContract.Clazz.FULL_NAME)));
        holder.teacher.setText(cursor.getString(cursor.getColumnIndex(ThothContract.Clazz.TEACHER)));

        Boolean isEnrolled = cursor.getString(cursor.getColumnIndex(ThothContract.Clazz.ENROLLED)).equals(TRUE);
        holder.checkBox.setChecked(isEnrolled);

        view.setBackground(new ColorDrawable((isEnrolled) ? 0x33440000 : 0x33333333));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Boolean toggleChecked = !holder.checkBox.isChecked();
            holder.checkBox.setChecked(toggleChecked);
            long id = Long.valueOf(holder.id.getText().toString());

            ContentValues values = new ContentValues();
            values.put(ThothContract.Clazz.ENROLLED, (toggleChecked) ? TRUE : FALSE);

            mContext.getContentResolver().update(UriUtils.Classes.parseClass(id), values, null, null );
            ThothUpdateService.startActionClassNewsUpdate(context, id);

            view.setBackground(new ColorDrawable((toggleChecked) ? 0x33440000 : 0x33333333));

            if(sMapSelection.containsKey(id)){
                SelectionState selectionState = sMapSelection.get(id);
                selectionState.finalState = toggleChecked;
                sMapSelection.put(id, selectionState);
            }
            else
                sMapSelection.put(id, new SelectionState(!toggleChecked));
            }
        });

    }
}

