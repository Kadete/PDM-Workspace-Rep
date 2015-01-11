package pt.isel.pdm.grupo17.thothnews.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.provider.CalendarContract;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Date;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.activities.ClassSectionsActivity;
import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.models.ThothWorkItem;
import pt.isel.pdm.grupo17.thothnews.models.ThothWorkItemsList;
import pt.isel.pdm.grupo17.thothnews.utils.CalendarUtils;
import pt.isel.pdm.grupo17.thothnews.utils.DateUtils;
import pt.isel.pdm.grupo17.thothnews.utils.ResolverUtils;

import static pt.isel.pdm.grupo17.thothnews.utils.LogUtils.d;
import static pt.isel.pdm.grupo17.thothnews.utils.TagUtils.TAG_ADAPTER;

public class WorkItemsAdapter extends CursorAdapter {

    private final Drawable dwAddEvent;
    private final Drawable dwEditEvent;

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
        dwAddEvent = mContext.getResources().getDrawable(R.drawable.ic_add_event);
        dwEditEvent = mContext.getResources().getDrawable(R.drawable.ic_editor_event);
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
    public View newView(final Context context, final Cursor cursor, ViewGroup viewGroup) {
        final WorkItemViewHolder holder = new WorkItemViewHolder();
        View newView = sLayoutInflater.inflate(R.layout.item_workitem, null);

        holder.id = (TextView)newView.findViewById(R.id.item_workItem_id);
        holder.title = (TextView)newView.findViewById(R.id.item_workItem_title);
        holder.title.setPaintFlags(holder.title.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        holder.whenStart = (TextView)newView.findViewById(R.id.item_workItem_whenStart);
        holder.whenDue = (TextView)newView.findViewById(R.id.item_workItem_whenDue);
        holder.url = (TextView)newView.findViewById(R.id.item_workItem_url);

        newView.setTag(holder);
        return newView;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        final WorkItemViewHolder holder = (WorkItemViewHolder)view.getTag();

        final String workItemID = cursor.getString(cursor.getColumnIndex(ThothContract.WorkItems._ID));
        final String title = cursor.getString(cursor.getColumnIndex(ThothContract.WorkItems.TITLE));
        final long eventId = cursor.getLong(cursor.getColumnIndex(ThothContract.WorkItems.EVENT_ID));

        holder.id.setText(workItemID);
        holder.title.setText(title);

        try {
            final Date dateStart = DateUtils.SAVE_DATE_FORMAT.parse(cursor.getString(cursor.getColumnIndex(ThothContract.WorkItems.START_DATE)));
            final Date dateDue = DateUtils.SAVE_DATE_FORMAT.parse(cursor.getString(cursor.getColumnIndex(ThothContract.WorkItems.DUE_DATE)));
            holder.whenStart.setText(Html.fromHtml(context.getString(R.string.start_date_label)+" <b>" + DateUtils.SHOW_DATE_FORMAT.format(dateStart)+"</b>"));
            holder.whenDue.setText(Html.fromHtml(context.getString(R.string.due_date_label)+" <b>" + DateUtils.SHOW_DATE_FORMAT.format(dateDue)+"</b>"));

            view.findViewById(R.id.layout_work_item_add_event).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                   try(Cursor cursor = mContext.getContentResolver().query(CalendarContract.Events.CONTENT_URI,
                            null, CalendarContract.Events._ID + " = ? " , new String[]{String.valueOf(eventId)},null)) {

                       boolean hasEvent = cursor.moveToNext();
                       v.findViewById(R.id.iv_workitem_event_options).setBackground((hasEvent)
                               ? mContext.getResources().getDrawable(R.drawable.ic_editor_event)
                               : mContext.getResources().getDrawable(R.drawable.ic_add_event));

                       if (!hasEvent || eventId == CalendarUtils.INVALID_EVENT_ID) { // ADD
                           long newEventId = CalendarUtils.addAppointment(mContext,
                                   title, ClassSectionsActivity.getThothClass().getFullName(), dateDue.getTime());
                           if (newEventId != CalendarUtils.INVALID_EVENT_ID) {
                               ResolverUtils.updateWorkItem(mContext, newEventId, workItemID);
                               Toast.makeText(mContext, "Event " + title + " ADDED with Success!!", Toast.LENGTH_SHORT).show();
                           } else
                               Toast.makeText(mContext, "Some ERROR occur!!", Toast.LENGTH_SHORT).show();
                           return;
                       }
                   }

                    AlertDialog.Builder ab = new AlertDialog.Builder(mContext);
                    ab.setTitle("Calendar Event");
                    ab.setIcon(R.drawable.ic_action_event);
                    ab.setItems(new String[]{"EDIT ", "DELETE"}, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface d, int choice) {
                            switch (choice){
                                case 0: // EDIT
                                    CalendarUtils.editAppointment(mContext, eventId);
                                    break;
                                case 1: // DELETE
                                    int nrEventsDeleted = CalendarUtils.deleteAppointment(mContext, eventId);
                                    ResolverUtils.updateWorkItem(mContext, CalendarUtils.INVALID_EVENT_ID, workItemID);
                                    Toast.makeText(mContext,(nrEventsDeleted != 0)  ? "Event "+ title +" DELETED with success!!"
                                                                                    : "Some ERROR occur!!",Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    });
                    ab.show();
                }
            });

        } catch (ParseException e) {
            d(TAG_ADAPTER, "FAIL TO PARSE DATE");
            Toast.makeText(mContext, "Some error on convert date", Toast.LENGTH_SHORT).show();
        }

        if(ClassSectionsActivity.isTwoPane()){
            if (workItemSelectedID == Long.valueOf(workItemID)) {
                view.findViewById(R.id.layout_work_item_info).setBackground(mContext.getResources().getDrawable(R.drawable.bg_new_selected));
                view.findViewById(R.id.layout_work_item_add_event).setVisibility(View.GONE);
                view.findViewById(R.id.iv_workitem_event_options).setVisibility(View.GONE);
            }
            else {
                view.findViewById(R.id.layout_work_item_info).setBackground(mContext.getResources().getDrawable(R.drawable.grad_light_blue));
                view.findViewById(R.id.layout_work_item_add_event).setVisibility(View.VISIBLE);
                view.findViewById(R.id.iv_workitem_event_options).setVisibility(View.VISIBLE);
            }
        }
        try (Cursor cursorEvent = mContext.getContentResolver().query(CalendarContract.Events.CONTENT_URI,
                null, CalendarContract.Events._ID + " = ? " , new String[]{String.valueOf(eventId)},null)) {
            boolean hasEvent = cursorEvent.moveToNext();
            view.findViewById(R.id.iv_workitem_event_options).setBackground((hasEvent) ? dwEditEvent : dwAddEvent);
        }
    }

}