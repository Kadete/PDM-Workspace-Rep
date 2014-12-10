package pt.isel.pdm.grupo17.thothnews.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.handlers.ImageHandler;
import pt.isel.pdm.grupo17.thothnews.handlers.ImageHandlerThread;
import pt.isel.pdm.grupo17.thothnews.handlers.SetViewHandler;
import pt.isel.pdm.grupo17.thothnews.models.ThothStudent;
import pt.isel.pdm.grupo17.thothnews.models.ThothStudentsList;

public class ParticipantsAdapter extends CursorAdapter {

    private static final int WITHOUT_GROUP = 0;

    class NewViewHolder {
        public TextView number_and_group;
        public TextView fullName;
        public TextView email;
        public ImageView avatar;
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
        holder.avatar = (ImageView)newView.findViewById(R.id.iv_student_avatar);

        newView.setTag(holder);
        return newView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        NewViewHolder holder = (NewViewHolder)view.getTag();

        String mainInfo = "Nº" + String.valueOf(cursor.getLong(cursor.getColumnIndex(ThothContract.Student._ID))); // _ID == NUMBER
        int nGroup = cursor.getInt(cursor.getColumnIndex(ThothContract.Student.GROUP));
        final String studentEmail = cursor.getString(cursor.getColumnIndex(ThothContract.Student.ACADEMIC_EMAIL));
        final String studentName = cursor.getString(cursor.getColumnIndex(ThothContract.Student.FULL_NAME));

        mainInfo += mContext.getString(R.string.participant_main_info_tv) + ((nGroup == WITHOUT_GROUP) ? "-" : String.valueOf(nGroup));
        holder.number_and_group.setText(mainInfo);
        holder.email.setText(studentEmail);
        holder.fullName.setText(studentName);

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, studentEmail);
                i.putExtra(Intent.EXTRA_SUBJECT, mContext.getString(R.string.send_email_subject));
                i.putExtra(Intent.EXTRA_TEXT, mContext.getString(R.string.send_email_body));
                try {
                    mContext.startActivity(Intent.createChooser(i, mContext.getString(R.string.send_mail_to) + studentName));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(mContext, mContext.getString(R.string.send_mail_fail_no_app), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        SetViewHandler svh = new SetViewHandler(Looper.getMainLooper());
        ImageHandlerThread th = new ImageHandlerThread();
        th.start();
        ImageHandler ih = new ImageHandler(svh, th.getLooper());
        String avatarUrl = cursor.getString(cursor.getColumnIndex(ThothContract.Student.AVATAR_URL));
        ih.fetchImage(holder.avatar ,avatarUrl);
    }
}