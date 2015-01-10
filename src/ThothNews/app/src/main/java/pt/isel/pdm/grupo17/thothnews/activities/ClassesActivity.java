package pt.isel.pdm.grupo17.thothnews.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.broadcastreceivers.NetworkReceiver;
import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.fragments.ClassesFragment;
import pt.isel.pdm.grupo17.thothnews.utils.TagUtils;
import pt.isel.pdm.grupo17.thothnews.utils.UriUtils;

public class ClassesActivity extends FragmentActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_classes);

        startPrefsIfNoClassesEnrolled(this);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            ClassesFragment fragment = new ClassesFragment();
            transaction.replace(R.id.fragment_container_classes, fragment);
            transaction.commit();
        }

        getActionBar().setTitle(R.string.label_activity_classes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_classes, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_webview:
                if(!NetworkReceiver.checkConnection(getApplicationContext(), true))
                    break;
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra(TagUtils.TAG_EXTRA_WEB_VIEW_URL, UriUtils.URI_CLASSES_ROOT);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                startActivity(new Intent(ClassesActivity.this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void startPrefsIfNoClassesEnrolled(Context context){
        Cursor cClassesEnrolled = context.getContentResolver().query(ThothContract.Classes.ENROLLED_URI, null, null, null, null);
        if(!cClassesEnrolled.moveToNext()){
            Toast.makeText(context, context.getString(R.string.setup_classes_request), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, SettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            cClassesEnrolled.close();
            context.startActivity(intent);
        }
        cClassesEnrolled.close();
    }
}