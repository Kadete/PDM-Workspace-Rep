package pt.isel.pdm.grupo17.thothnews.activities;

import android.app.ActionBar;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.adapters.NewsAdapter;
import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.fragments.NewsListFragment;
import pt.isel.pdm.grupo17.thothnews.fragments.SingleNewFragment;
import pt.isel.pdm.grupo17.thothnews.fragments.SlidingTabsColorsFragment;
import pt.isel.pdm.grupo17.thothnews.fragments.dialogs.ReadAllDialogFragment;
import pt.isel.pdm.grupo17.thothnews.handlers.ImageHandler;
import pt.isel.pdm.grupo17.thothnews.handlers.ImageHandlerThread;
import pt.isel.pdm.grupo17.thothnews.handlers.SetViewHandler;
import pt.isel.pdm.grupo17.thothnews.models.ThothClass;
import pt.isel.pdm.grupo17.thothnews.models.ThothNew;
import pt.isel.pdm.grupo17.thothnews.utils.TagUtils;
import pt.isel.pdm.grupo17.thothnews.utils.UriUtils;

public class ClassSectionsActivity extends FragmentActivity implements NewsListFragment.Callbacks{

    private static ThothClass sThothClass;
    public static ThothClass getThothClass() {
        return sThothClass;
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
        }

        Intent intent = getIntent();
        sThothClass = (ThothClass) intent.getSerializableExtra(TagUtils.TAG_SERIALIZABLE_CLASS);

        final TextView tvClass = (TextView) findViewById(R.id.tv_class_name);
        final TextView tvTeacher = (TextView) findViewById(R.id.tv_teacher_name);
        final ImageView ivTeacherAvatar = (ImageView) findViewById(R.id.iv_teacher_avatar);
        final TextView tvTeacherEmail = (TextView) findViewById(R.id.tv_teacher_email);

        tvClass.setText(sThothClass.getFullName());
        tvTeacher.setText(sThothClass.getTeacherName());

        long teacherID = sThothClass.getTeacherID();
        Uri teacherUri = UriUtils.Teachers.parseFromTeacherID(teacherID);
        String [] cursorColumns = new String[] {ThothContract.Teachers._ID, ThothContract.Teachers.ACADEMIC_EMAIL, ThothContract.Teachers.AVATAR_URL};
        Cursor teacherCursor = getApplication().getContentResolver().query(teacherUri,cursorColumns , null, null, null);

        if(teacherCursor.moveToNext()){
            tvTeacherEmail.setMovementMethod(LinkMovementMethod.getInstance());
            final String teacherEmail = teacherCursor.getString(teacherCursor.getColumnIndex(ThothContract.Teachers.ACADEMIC_EMAIL));
            tvTeacherEmail.setText(teacherEmail);
            tvTeacherEmail.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            tvTeacherEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, teacherEmail);
                    i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.send_email_subject));
                    i.putExtra(Intent.EXTRA_TEXT, getString(R.string.send_email_body));
                    try {
                        startActivity(Intent.createChooser(i, getString(R.string.send_mail_to) + sThothClass.getTeacherName()));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(ClassSectionsActivity.this, getString(R.string.send_mail_fail_no_app), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            SetViewHandler svh = new SetViewHandler(Looper.getMainLooper());
            ImageHandlerThread th = new ImageHandlerThread();
            th.start();
            ImageHandler ih = new ImageHandler(svh, th.getLooper());
            ih.fetchImage(ivTeacherAvatar, teacherCursor.getString(teacherCursor.getColumnIndex(ThothContract.Teachers.AVATAR_URL)));
        }else
            tvTeacherEmail.setText("N/A");
        teacherCursor.close();
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
    public void onItemSelected(ThothNew thothNew) {
        if (NewsListFragment.isTwoPane()) {
            Bundle arguments = new Bundle();
            arguments.putSerializable(TagUtils.TAG_SERIALIZABLE_NEW, thothNew);
            SingleNewFragment fragment = new SingleNewFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_detail_new, fragment)
                    .commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_class_sections, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if(NewsListFragment.isTwoPane())
            NewsAdapter.setSelectedNewID(NewsAdapter.NO_NEW_SELECTED);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_read_all:
                ReadAllDialogFragment readAllDialogFragment = new ReadAllDialogFragment(sThothClass.getID());
                readAllDialogFragment.show(getSupportFragmentManager(), "Read All News Dialog Fragment");
                return true;
            case R.id.action_refresh:
                SlidingTabsColorsFragment fragment = (SlidingTabsColorsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container_class_sections);
                if(fragment != null)
                     fragment.refreshLoader();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, PreferencesActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
