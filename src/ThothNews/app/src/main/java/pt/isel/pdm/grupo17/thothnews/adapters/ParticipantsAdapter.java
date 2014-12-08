package pt.isel.pdm.grupo17.thothnews.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.models.ThothStudent;
import pt.isel.pdm.grupo17.thothnews.models.ThothStudentsList;

public class ParticipantsAdapter extends CursorAdapter {

    private static final int WITHOUT_GROUP = 0;

    class NewViewHolder {
        public TextView number_and_group;
        public TextView fullName;
        public TextView email;
    }

    static LayoutInflater sLayoutInflater = null;
    ThothStudentsList mParticipants = new ThothStudentsList();
    Context mContext;

    public ParticipantsAdapter(Context context) {
        super(context, null, 0);
        mContext = context;
        sLayoutInflater = LayoutInflater.from(mContext);
    }

//    public void clearList() {
//        mParticipants.clear();
//        mContext.getContentResolver().delete(ThothContract.Participants.CONTENT_URI, null, null);
//        notifyDataSetChanged();
//    }

    @Override
    public Object getItem(int position) {
        return mParticipants.get(position);
    }

//    public ThothParticipantsList getParticipantsList() {
//        return mParticipants;
//    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        Cursor oldCursor = super.swapCursor(newCursor);

        mParticipants.clear();
        if (newCursor !=null) {
            newCursor.moveToFirst();
            while(!newCursor.isAfterLast()) {
                ThothStudent participant = ThothStudent.fromCursor(newCursor);
                mParticipants.add(participant);
                newCursor.moveToNext();
            }
        }
        notifyDataSetChanged();
        return oldCursor;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        NewViewHolder holder = new NewViewHolder();
        View newView = sLayoutInflater.inflate(R.layout.item_participant, null);

        holder.number_and_group = (TextView)newView.findViewById(R.id.participant_item_number_and_group);
        holder.fullName = (TextView)newView.findViewById(R.id.participant_item_full_name);
        holder.email = (TextView)newView.findViewById(R.id.participant_item_email);

        newView.setTag(holder);
        return newView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        NewViewHolder holder = (NewViewHolder)view.getTag();

        String mainInfo = "Nº" + String.valueOf(cursor.getInt(cursor.getColumnIndex(ThothContract.Students._ID))); // _ID == NUMBER
        int nGroup = cursor.getInt(cursor.getColumnIndex(ThothContract.Students.GROUP));
        mainInfo += mContext.getString(R.string.participant_main_info_tv) + ((nGroup == WITHOUT_GROUP) ? "-" : String.valueOf(nGroup));

        holder.number_and_group.setText(mainInfo);

        holder.fullName.setText(cursor.getString(cursor.getColumnIndex(ThothContract.Students.FULL_NAME)));
        holder.email.setText(cursor.getString(cursor.getColumnIndex(ThothContract.Students.ACADEMIC_EMAIL)));

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{String.valueOf(((TextView)v.findViewById(R.id.participant_item_email)).getText())});
                i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
                i.putExtra(Intent.EXTRA_TEXT, "body of email");
                try {
                    mContext.startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(mContext, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }
}