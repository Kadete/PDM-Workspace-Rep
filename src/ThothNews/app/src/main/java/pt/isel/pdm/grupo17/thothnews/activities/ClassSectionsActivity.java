package pt.isel.pdm.grupo17.thothnews.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.adapters.NewsAdapter;
import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.fragments.NewsListFragment;
import pt.isel.pdm.grupo17.thothnews.fragments.SingleNewFragment;
import pt.isel.pdm.grupo17.thothnews.fragments.SlidingTabsColorsFragment;
import pt.isel.pdm.grupo17.thothnews.fragments.dialogs.ReadAllDialogFragment;
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
        String [] cursorColumns = new String[] {ThothContract.Teacher._ID, ThothContract.Teacher.ACADEMIC_EMAIL, ThothContract.Teacher.AVATAR_URL};
        Cursor cursorNewsRead = getApplication().getContentResolver().query(teacherUri,cursorColumns , null, null, null);

        if(cursorNewsRead.moveToNext()){
            tvTeacherEmail.setText(cursorNewsRead.getString(cursorNewsRead.getColumnIndex(ThothContract.Teacher.ACADEMIC_EMAIL)));
//            ivTeacherAvatar.setImageDrawable();
        }else
            tvTeacherEmail.setText("N/A");
        cursorNewsRead.close();
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
