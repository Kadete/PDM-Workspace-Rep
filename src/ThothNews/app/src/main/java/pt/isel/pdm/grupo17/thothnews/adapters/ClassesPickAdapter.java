package pt.isel.pdm.grupo17.thothnews.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
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
import pt.isel.pdm.grupo17.thothnews.utils.ParseUtils;

import static pt.isel.pdm.grupo17.thothnews.data.providers.SQLiteUtils.FALSE;
import static pt.isel.pdm.grupo17.thothnews.data.providers.SQLiteUtils.TRUE;

public class ClassesPickAdapter extends CursorAdapter {

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

    public static Map<Long,SelectionState> sMapSelection = new HashMap<>();
    public Map<Long,SelectionState> getMapSelection (){
        return sMapSelection;
    }

    Context mContext;

    public ClassesPickAdapter(Context context) {
        super(context, null, 0);
        mContext = context;
        sLayoutInflater = LayoutInflater.from(context);
    }

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
        View newView = sLayoutInflater.inflate(R.layout.item_class_pick, null);

        holder.id = (TextView)newView.findViewById(R.id.class_pick_item_id);
        holder.full_name = (TextView)newView.findViewById(R.id.class_pick_item_full_name);
        holder.full_name.setPaintFlags(holder.full_name.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        holder.teacher = (TextView)newView.findViewById(R.id.class_pick_item_teacher);
        holder.checkBox = (CheckBox)newView.findViewById(R.id.class_pick_item_checkEnrolled);

        newView.setTag(holder);
        return newView;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final ClassViewHolder holder = (ClassViewHolder)view.getTag();

        long id = cursor.getLong(cursor.getColumnIndex(ThothContract.Classes._ID));
        holder.id.setText(String.valueOf(id));
        holder.full_name.setText(cursor.getString(cursor.getColumnIndex(ThothContract.Classes.FULL_NAME)));
        holder.teacher.setText(Html.fromHtml(context.getString(R.string.teacher_label) + cursor.getString(cursor.getColumnIndex(ThothContract.Classes.TEACHER_NAME))));

        Boolean isEnrolled = cursor.getString(cursor.getColumnIndex(ThothContract.Classes.ENROLLED)).equals(TRUE);
        holder.checkBox.setChecked(isEnrolled);

        view.setBackground(new ColorDrawable((isEnrolled) ? 0x33440000 : 0xffb7dde1));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean toggleChecked = !holder.checkBox.isChecked();
                holder.checkBox.setChecked(toggleChecked);
                long id = Long.valueOf(holder.id.getText().toString());

                ContentValues values = new ContentValues();
                values.put(ThothContract.Classes.ENROLLED, (toggleChecked) ? TRUE : FALSE);

                mContext.getContentResolver().update(ParseUtils.Classes.parseClass(id), values, null, null );

                view.setBackground(new ColorDrawable((toggleChecked) ? 0x33440000 : 0xffb7dde1));

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

