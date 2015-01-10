package pt.isel.pdm.grupo17.thothnews.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.receivers.NetworkReceiver;
import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.fragments.NewsListFragment;
import pt.isel.pdm.grupo17.thothnews.fragments.SingleNewFragment;
import pt.isel.pdm.grupo17.thothnews.fragments.SlidingTabsColorsFragment;
import pt.isel.pdm.grupo17.thothnews.fragments.WebViewFragment;
import pt.isel.pdm.grupo17.thothnews.fragments.WorkItemsListFragment;
import pt.isel.pdm.grupo17.thothnews.fragments.dialogs.ReadAllDialogFragment;
import pt.isel.pdm.grupo17.thothnews.handlers.ImageHandler;
import pt.isel.pdm.grupo17.thothnews.handlers.ImageHandlerThread;
import pt.isel.pdm.grupo17.thothnews.handlers.SetViewAndUpdateHandler;
import pt.isel.pdm.grupo17.thothnews.models.ThothClass;
import pt.isel.pdm.grupo17.thothnews.models.ThothNew;
import pt.isel.pdm.grupo17.thothnews.models.ThothWorkItem;
import pt.isel.pdm.grupo17.thothnews.utils.BitmapUtils;
import pt.isel.pdm.grupo17.thothnews.utils.ParseUtils;
import pt.isel.pdm.grupo17.thothnews.utils.TagUtils;
import pt.isel.pdm.grupo17.thothnews.utils.UriUtils;

import static pt.isel.pdm.grupo17.thothnews.utils.BitmapUtils.EnumModel.DIR_PATH_TEACHER;

public class ClassSectionsActivity extends FragmentActivity implements NewsListFragment.CallbackNew, WorkItemsListFragment.CallbackWorkItem {

    private static ThothClass sThothClass;
    public static ThothClass getThothClass() {
        return sThothClass;
    }

    private static boolean sTwoPane;
    public static boolean isTwoPane() {
        return sTwoPane;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_class_sections);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            SlidingTabsColorsFragment fragment = new SlidingTabsColorsFragment();
            transaction.replace(R.id.fragment_container_class_sections, fragment);
            transaction.commit();

            if(findViewById(R.id.fragment_dummy) != null)
                sTwoPane = true;
        }

        Intent intent = getIntent();
        sThothClass = (ThothClass) intent.getSerializableExtra(TagUtils.TAG_SERIALIZABLE_CLASS);

        final TextView tvClass = (TextView) findViewById(R.id.tv_class_name);
        tvClass.setText(sThothClass.getFullName());
        tvClass.setPaintFlags(tvClass.getPaintFlags()|Paint.UNDERLINE_TEXT_FLAG);
        setupTeacherInfo();
    }

    private void setupTeacherInfo(){
        final TextView tvTeacher = (TextView) findViewById(R.id.tv_teacher_name);
        tvTeacher.setText(sThothClass.getTeacherName());

        long teacherID = sThothClass.getTeacherID();
        Uri teacherUri = ParseUtils.Teachers.parseTeacherID(teacherID);
        String [] cursorColumns = new String[] {ThothContract.Teachers._ID, ThothContract.Teachers.ACADEMIC_EMAIL, ThothContract.Avatars.AVATAR_URL, ThothContract.Avatars.AVATAR_PATH};
        Cursor teacherCursor = getApplication().getContentResolver().query(teacherUri,cursorColumns , null, null, null);

        if(teacherCursor.moveToNext()) {
            final TextView tvTeacherEmail = (TextView) findViewById(R.id.tv_teacher_email);
            tvTeacherEmail.setMovementMethod(LinkMovementMethod.getInstance());
            final String teacherEmail = teacherCursor.getString(teacherCursor.getColumnIndex(ThothContract.Teachers.ACADEMIC_EMAIL));
            tvTeacherEmail.setText(teacherEmail);
            tvTeacherEmail.setPaintFlags(tvTeacherEmail.getPaintFlags()|Paint.UNDERLINE_TEXT_FLAG);
            tvTeacherEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { /** send email to teacher **/
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[] {teacherEmail});
                    i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.send_email_subject));
                    i.putExtra(Intent.EXTRA_TEXT, getString(R.string.send_email_body));
                    try {
                        startActivity(Intent.createChooser(i, getString(R.string.send_mail_to) + sThothClass.getTeacherName()));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(ClassSectionsActivity.this, getString(R.string.send_mail_fail_no_app), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            final ImageView ivTeacherAvatar = (ImageView) findViewById(R.id.iv_teacher_avatar);
            setTeacherAvatar(ivTeacherAvatar, teacherCursor, teacherID);
        }
        teacherCursor.close();
    }

    private void setTeacherAvatar(ImageView ivTeacherAvatar, Cursor teacherCursor, long teacherID) {

        String avatarPath = teacherCursor.getString(teacherCursor.getColumnIndex(ThothContract.Avatars.AVATAR_PATH)); // saved path

        if (avatarPath == null || avatarPath.isEmpty()) { /** photo not saved yet. Get avatar via req HTTP and then save the file on phone ROM **/

            String storagePath = BitmapUtils.initStoragePath(getApplication(), DIR_PATH_TEACHER);
            String avatarUrl = teacherCursor.getString(teacherCursor.getColumnIndex(ThothContract.Avatars.AVATAR_URL));
            SetViewAndUpdateHandler svh = new SetViewAndUpdateHandler(Looper.getMainLooper(), getContentResolver());

            ImageHandlerThread th = new ImageHandlerThread();
            th.start();
            ImageHandler ih = new ImageHandler(svh, th.getLooper());
            ih.fetchImage(ivTeacherAvatar, avatarUrl, ParseUtils.Teachers.parseTeacherID(teacherID), storagePath); // external url
        }
        else{ /** AsyncTask to get the photo and show when ready: getBitmapFromFile **/
            new BitmapUtils.LoadBitmapTask(ivTeacherAvatar).execute(avatarPath);
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        ActionBar actionbar = getActionBar();
        assert actionbar != null;
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeButtonEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE)
            return;
        FrameLayout layout = (FrameLayout)findViewById(R.id.frame_teacher_info_id);
        if(layout != null) {
            layout.setVisibility(View.GONE);
            getActionBar().setTitle(sThothClass.getFullName());
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            FrameLayout layout = (FrameLayout)findViewById(R.id.frame_teacher_info_id);
            if(layout != null) {
                layout.setVisibility(View.GONE);
                getActionBar().setTitle(sThothClass.getFullName());
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            FrameLayout layout = (FrameLayout)findViewById(R.id.frame_teacher_info_id);
            if(layout != null)
                layout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemSelected(ThothNew thothNew) {
        if (isTwoPane()) {
            Bundle arguments = new Bundle();
            arguments.putSerializable(TagUtils.TAG_SERIALIZABLE_NEW, thothNew);
            SingleNewFragment singleNewFragment = new SingleNewFragment();
            singleNewFragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_new_detailed, singleNewFragment)
                    .commit();
        }
    }

    @Override
    public void onItemSelected(ThothWorkItem workItem) {
        if (isTwoPane()) {
            WebViewFragment webViewFragment = new WebViewFragment();
            webViewFragment.init(workItem.getUrl());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_workitem_detailed, webViewFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_class_sections, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_read_all:
                ReadAllDialogFragment readAllDialogFragment = ReadAllDialogFragment.newInstance(sThothClass.getID());
                readAllDialogFragment.show(getSupportFragmentManager(), "Read All News Dialog Fragment");
                return true;
            case R.id.action_webview:
                if(!NetworkReceiver.checkConnection(getApplicationContext(), true))
                    break;
                Intent intent = new Intent(this, WebViewActivity.class);
                String fullName = sThothClass.getFullName().replaceAll(" ", "");
                String full_path = String.format("%s/%s", UriUtils.URI_CLASSES_ROOT, fullName);
                intent.putExtra(TagUtils.TAG_EXTRA_WEB_VIEW_URL, full_path);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
