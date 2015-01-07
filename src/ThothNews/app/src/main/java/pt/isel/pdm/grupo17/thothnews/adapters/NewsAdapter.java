package pt.isel.pdm.grupo17.thothnews.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Date;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.activities.ClassSectionsActivity;
import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.models.ThothNew;
import pt.isel.pdm.grupo17.thothnews.models.ThothNewsList;
import pt.isel.pdm.grupo17.thothnews.utils.DateUtils;

import static pt.isel.pdm.grupo17.thothnews.utils.ParseUtils.d;
import static pt.isel.pdm.grupo17.thothnews.utils.SQLiteUtils.TRUE;
import static pt.isel.pdm.grupo17.thothnews.utils.TagUtils.TAG_ADAPTER;

public class NewsAdapter extends CursorAdapter {

    class NewViewHolder {
        public TextView id;
        public TextView title;
        public TextView when;
        public CheckBox checkRead;
    }

    public static final long NO_NEW_SELECTED = -1;
    public long newSelectID = NO_NEW_SELECTED;

    public void setSelectedNewID(long id) {
        newSelectID = id;
    }

    static LayoutInflater sLayoutInflater = null;
    ThothNewsList mNews = new ThothNewsList();
    Context mContext;

    public NewsAdapter(Context context) {
        super(context, null, 0);
        mContext = context;
        sLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public Object getItem(int position) {
        return mNews.get(position);
    }

    public ThothNewsList getNewsList() {
        return mNews;
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
        View newView = sLayoutInflater.inflate(R.layout.item_new, null);

        holder.id = (TextView)newView.findViewById(R.id.item_new_id);
        holder.title = (TextView)newView.findViewById(R.id.item_new_title);
        holder.when = (TextView)newView.findViewById(R.id.item_new_when);
        holder.checkRead = (CheckBox)newView.findViewById(R.id.item_new_checkread);

        newView.setTag(holder);
        return newView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        NewViewHolder holder = (NewViewHolder)view.getTag();

        final String id = cursor.getString(cursor.getColumnIndex(ThothContract.News._ID));
        holder.id.setText(id);
        holder.title.setText(cursor.getString(cursor.getColumnIndex(ThothContract.News.TITLE)));

        try {
            Date date = DateUtils.SAVE_DATE_FORMAT.parse(cursor.getString(cursor.getColumnIndex(ThothContract.News.WHEN_CREATED)));
            holder.when.setText(DateUtils.SHOW_DATE_FORMAT.format(date));
        } catch (ParseException e) {
            d(TAG_ADAPTER, "FAIL TO PARSE DATE");
        }

        Boolean read = cursor.getString(cursor.getColumnIndex(ThothContract.News.READ)).equals(TRUE);
        holder.checkRead.setChecked(read);
        holder.title.setTypeface(null, (!read) ? Typeface.BOLD : Typeface.NORMAL);
        holder.when.setTypeface(null, (!read) ? Typeface.BOLD : Typeface.NORMAL);

        if(ClassSectionsActivity.isTwoPane()){
            if (newSelectID == Long.valueOf(id)) {
                view.findViewById(R.id.arrow).setVisibility(View.VISIBLE);
                view.setBackground(view.getResources().getDrawable(R.drawable.bg_new_selected));
            }
            else {
                view.findViewById(R.id.arrow).setVisibility((!read) ? View.VISIBLE :  View.INVISIBLE);
                view.setBackground(new ColorDrawable((!read) ? 0x33440000 : 0x44444444));
            }
        }
        else {
            view.findViewById(R.id.arrow).setVisibility((!read) ? View.VISIBLE :  View.INVISIBLE);
            view.setBackground(new ColorDrawable((!read) ? 0x33440000 : 0x33333333));
        }
    }


}