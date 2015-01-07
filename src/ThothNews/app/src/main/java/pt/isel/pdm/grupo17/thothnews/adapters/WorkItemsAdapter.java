package pt.isel.pdm.grupo17.thothnews.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Date;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.activities.ClassSectionsActivity;
import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.models.ThothWorkItem;
import pt.isel.pdm.grupo17.thothnews.models.ThothWorkItemsList;
import pt.isel.pdm.grupo17.thothnews.utils.DateUtils;

import static pt.isel.pdm.grupo17.thothnews.utils.ParseUtils.d;
import static pt.isel.pdm.grupo17.thothnews.utils.TagUtils.TAG_ADAPTER;

public class WorkItemsAdapter extends CursorAdapter {

    class WorkItemViewHolder {
        public TextView id;
        public TextView title;
        public TextView whenStart;
        public TextView whenDue;
        public TextView url;
    }

    public static final int NO_WORK_ITEM_SELECTED = -1;
    private long workItemSelectedID = NO_WORK_ITEM_SELECTED;

    public void setSelectedWorkItemID(long id) {
        workItemSelectedID = id;
    }

    static LayoutInflater sLayoutInflater = null;
    ThothWorkItemsList mWorkItems = new ThothWorkItemsList();
    Context mContext;

    public WorkItemsAdapter(Context context) {
        super(context, null, 0);
        mContext = context;
        sLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public Object getItem(int position) {
        return mWorkItems.get(position);
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        Cursor oldCursor = super.swapCursor(newCursor);

        mWorkItems.clear();
        if (newCursor !=null) {
            newCursor.moveToFirst();
            while(!newCursor.isAfterLast()) {
                ThothWorkItem workItem = ThothWorkItem.fromCursor(newCursor);
                mWorkItems.add(workItem);
                newCursor.moveToNext();
            }
        }
        notifyDataSetChanged();
        return oldCursor;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        WorkItemViewHolder holder = new WorkItemViewHolder();
        View newView = sLayoutInflater.inflate(R.layout.item_workitem, null);

        holder.id = (TextView)newView.findViewById(R.id.item_workItem_id);
        holder.title = (TextView)newView.findViewById(R.id.item_workItem_title);
        holder.whenStart = (TextView)newView.findViewById(R.id.item_workItem_whenStart);
        holder.whenDue = (TextView)newView.findViewById(R.id.item_workItem_whenDue);
        holder.url = (TextView)newView.findViewById(R.id.item_workItem_url);

        newView.setTag(holder);
        return newView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        WorkItemViewHolder holder = (WorkItemViewHolder)view.getTag();

        final String id = cursor.getString(cursor.getColumnIndex(ThothContract.WorkItems._ID));

        holder.id.setText(id);
        holder.title.setText(cursor.getString(cursor.getColumnIndex(ThothContract.WorkItems.TITLE)));

        try {
            Date dateStart = DateUtils.SAVE_DATE_FORMAT.parse(cursor.getString(cursor.getColumnIndex(ThothContract.WorkItems.START_DATE)));
            Date dateDue = DateUtils.SAVE_DATE_FORMAT.parse(cursor.getString(cursor.getColumnIndex(ThothContract.WorkItems.DUE_DATE)));
            holder.whenStart.setText("Start Date: " + DateUtils.SHOW_DATE_FORMAT.format(dateStart));
            holder.whenDue.setText("Due Date: " + DateUtils.SHOW_DATE_FORMAT.format(dateDue));
        } catch (ParseException e) {
            d(TAG_ADAPTER, "FAIL TO PARSE DATE");
        }

        if(ClassSectionsActivity.isTwoPane()){
            if (workItemSelectedID == Long.valueOf(id)) {
                view.setBackground(view.getResources().getDrawable(R.drawable.bg_new_selected));
                view.findViewById(R.id.iv_workitem_webview).setVisibility(View.GONE);
            }
            else {
                view.setBackground(new ColorDrawable(0x33440000));
                view.findViewById(R.id.iv_workitem_webview).setVisibility(View.VISIBLE);
            }
        }
        else{
            view.setBackground(view.getResources().getDrawable(R.drawable.bg_new_selected));
        }

    }


}