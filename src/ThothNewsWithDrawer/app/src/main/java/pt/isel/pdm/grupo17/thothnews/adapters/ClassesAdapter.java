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
        View newView = sLayoutInflater.inflate(R.layout.item_class, null);

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
        holder.full_name.setTypeface(null, (newsToRead) ? Typeface.BOLD : Typeface.NORMAL);
        holder.teacher.setTypeface(null, (newsToRead) ? Typeface.BOLD : Typeface.NORMAL);
        view.setBackground(new ColorDrawable((newsToRead) ? 0x44440000 : 0x44444444));
    }

}
