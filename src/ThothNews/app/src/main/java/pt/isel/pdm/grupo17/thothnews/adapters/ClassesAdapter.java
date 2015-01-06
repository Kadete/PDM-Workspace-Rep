package pt.isel.pdm.grupo17.thothnews.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.models.ThothClass;
import pt.isel.pdm.grupo17.thothnews.utils.UriUtils;

import static pt.isel.pdm.grupo17.thothnews.utils.SQLiteUtils.FALSE;

public class ClassesAdapter extends CursorAdapter {

    static class ClassViewHolder{
        public TextView id;
        public TextView full_name;
        public TextView teacher;
        public ImageView new_news;
    }

    static LayoutInflater sLayoutInflater = null;
    List<ThothClass> mClasses = new ArrayList<>();
    Context mContext;

    public ClassesAdapter(Context context) {
        super(context, null, 0);
        mContext = context;
        sLayoutInflater = LayoutInflater.from(context);
    }

//    public void clearList() {
//        mClasses.clear();
//        mContext.getContentResolver().delete(ThothContract.Classes.CONTENT_URI, null, null);
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
                ThothClass thothClass = ThothClass.fromCursor(newCursor);
                mClasses.add(thothClass);
                newCursor.moveToNext();
            }
        }
        notifyDataSetChanged();
        return oldCursor;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        ClassViewHolder holder = new ClassViewHolder();
        View newView = sLayoutInflater.inflate(R.layout.item_class, null);

        holder.id = (TextView)newView.findViewById(R.id.class_item_id);
        holder.full_name = (TextView)newView.findViewById(R.id.class_item_full_name);
        holder.teacher = (TextView)newView.findViewById(R.id.class_item_teacher);
        holder.new_news = (ImageView)newView.findViewById(R.id.class_item_new_news_ic);

        newView.setTag(holder);
        return newView;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        ClassViewHolder holder = (ClassViewHolder)view.getTag();

        Long classeID = cursor.getLong(cursor.getColumnIndex(ThothContract.Classes._ID));

        holder.id.setText(String.valueOf(classeID));
        holder.full_name.setText(cursor.getString(cursor.getColumnIndex(ThothContract.Classes.FULL_NAME)));
        holder.teacher.setText(cursor.getString(cursor.getColumnIndex(ThothContract.Classes.TEACHER_NAME)));

        String selection = ThothContract.News.READ + " =  ? ";
        String [] selectionArgs =  new String[] { FALSE };
        String orderBy = ThothContract.News.READ;
        Uri classNewsUri = UriUtils.Classes.parseNewsFromClassID(classeID);
        Cursor cursorNewsRead = mContext.getContentResolver().query(classNewsUri, new String[] {ThothContract.Classes._ID}, selection, selectionArgs, orderBy);

        Boolean newsToRead = cursorNewsRead.moveToNext();

        holder.new_news.setImageResource((newsToRead) ? R.drawable.ic_news_to_read : R.drawable.ic_action_visibility);
        cursorNewsRead.close();
        holder.full_name.setTypeface(null, (newsToRead) ? Typeface.BOLD : Typeface.NORMAL);
        holder.teacher.setTypeface(null, (newsToRead) ? Typeface.BOLD : Typeface.NORMAL);
        view.setBackground(new ColorDrawable((newsToRead) ? 0x33440000 : 0x33333333));
    }

}
