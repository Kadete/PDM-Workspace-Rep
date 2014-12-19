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
import pt.isel.pdm.grupo17.thothnews.handlers.SetViewAndUpdateHandler;
import pt.isel.pdm.grupo17.thothnews.models.ThothStudent;
import pt.isel.pdm.grupo17.thothnews.models.ThothStudentsList;
import pt.isel.pdm.grupo17.thothnews.utils.BitmapUtils;
import pt.isel.pdm.grupo17.thothnews.utils.UriUtils;

import static pt.isel.pdm.grupo17.thothnews.utils.BitmapUtils.EnumModel;
import static pt.isel.pdm.grupo17.thothnews.utils.BitmapUtils.EnumModel.DIR_PATH_STUDENT;

public class ParticipantsAdapter extends CursorAdapter {

    private static final int WITHOUT_GROUP = 0;

    class NewViewHolder {
        public TextView id;
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

        BitmapUtils.initStoragePath(mContext, EnumModel.DIR_PATH_STUDENT);
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

        holder.id = (TextView)newView.findViewById(R.id.participant_item_id);
        holder.number_and_group = (TextView)newView.findViewById(R.id.participant_item_number_and_group);
        holder.fullName = (TextView)newView.findViewById(R.id.participant_item_full_name);
        holder.email = (TextView)newView.findViewById(R.id.participant_item_email);
        holder.avatar = (ImageView)newView.findViewById(R.id.student_item_avatar);

        newView.setTag(holder);
        return newView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        NewViewHolder holder = (NewViewHolder)view.getTag();

        final long id = cursor.getLong(cursor.getColumnIndex(ThothContract.Classes_Students.KEY_STUDENT_ID));
        holder.id.setText(String.valueOf(id));// _ID == NUMBER

        final int nGroup = cursor.getInt(cursor.getColumnIndex(ThothContract.Classes_Students.GROUP));
        String mainInfo = "Nº" + id + mContext.getString(R.string.participant_main_info_tv) + " " + ((nGroup == WITHOUT_GROUP) ? "-" : String.valueOf(nGroup));
        holder.number_and_group.setText(mainInfo);

        final String studentEmail = cursor.getString(cursor.getColumnIndex(ThothContract.Students.ACADEMIC_EMAIL));
        holder.email.setText(studentEmail);

        final String studentName = cursor.getString(cursor.getColumnIndex(ThothContract.Students.FULL_NAME));
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

        setStudentAvatar(holder.avatar, cursor,  id);
    }

    private void setStudentAvatar(ImageView ivStudentAvatar, Cursor cursor, long id) {

        Cursor studentAvatarPath = mContext.getContentResolver().query(UriUtils.Students.parseStudentID(id),null, null, null, null);
        String avatarPath = null;
        if(studentAvatarPath.moveToNext())
            avatarPath = studentAvatarPath.getString(studentAvatarPath.getColumnIndex(ThothContract.Paths.AVATAR_PATH));
        studentAvatarPath.close();

        if (avatarPath == null || avatarPath.isEmpty()) { /** photo not saved yet. Get avatar via req HTTP and then save the file on phone ROM **/

            String storagePath = BitmapUtils.initStoragePath(mContext, DIR_PATH_STUDENT);
            String avatarUrl = cursor.getString(cursor.getColumnIndex(ThothContract.Students.AVATAR_URL));
            SetViewAndUpdateHandler svh = new SetViewAndUpdateHandler(Looper.getMainLooper(), mContext.getContentResolver());

            ImageHandlerThread th = new ImageHandlerThread();
            th.start();
            ImageHandler ih = new ImageHandler(svh, th.getLooper());
            ih.fetchImage(ivStudentAvatar, avatarUrl, UriUtils.Students.parseStudentID(id), storagePath); // external url

        }
        else{ /** AsyncTask to get the photo and show when ready: getBitmapFromFile **/
            new BitmapUtils.LoadBitmapTask(ivStudentAvatar).execute(avatarPath);
        }
    }
}