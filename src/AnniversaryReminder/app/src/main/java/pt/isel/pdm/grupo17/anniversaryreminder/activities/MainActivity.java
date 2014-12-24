package pt.isel.pdm.grupo17.anniversaryreminder.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import pt.isel.pdm.grupo17.anniversaryreminder.R;
import pt.isel.pdm.grupo17.anniversaryreminder.adapters.AnniversaryAdapter;
import pt.isel.pdm.grupo17.anniversaryreminder.broadcastreceivers.AlarmStartupReceiver;
import pt.isel.pdm.grupo17.anniversaryreminder.models.AnniversaryItem;

import static android.text.format.DateFormat.getTimeFormat;
import static pt.isel.pdm.grupo17.anniversaryreminder.utils.CursorUtils.getAnniversaryList;
import static pt.isel.pdm.grupo17.anniversaryreminder.utils.Utils.d;

public class MainActivity extends ListActivity {

    private static final int ADD_ANNIVERSARY_ITEM_REQUEST = 0;
    private static final int FILTER_ANNIVERSARY_SETTING_REQUEST = 1;

    private static final int MENU_SETTINGS = Menu.FIRST;

    private static int daysToFilter;

    AnniversaryAdapter bAdapter;
    private static final String TAG_ACTIVITY_MAIN = "TAG_ACTIVITY_MAIN";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String aux = sharedPreferences.getString("PREF_LIST", "14");
        daysToFilter = Integer.valueOf(aux);

        bAdapter = new AnniversaryAdapter(getApplicationContext());

        View inflater = getLayoutInflater().inflate(R.layout.layout_header_list, null);
        TextView headerView = (TextView) inflater.findViewById(R.id.headerView);

        if (null == headerView) {
            return;
        }

        getListView().addHeaderView(headerView);

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                d(TAG_ACTIVITY_MAIN,"Entered footerView.OnClickListener.onClick()");

                Intent i = new Intent(MainActivity.this, AddAnniversaryActivity.class);
                startActivityForResult(i, ADD_ANNIVERSARY_ITEM_REQUEST);
            }
        });

        getListView().setBackgroundResource(R.drawable.background_grad);

        setListAdapter(bAdapter);
        ListView lv = getListView();
        lv.setDivider(new PaintDrawable(R.color.sage));
        lv.setDividerHeight(1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        d(TAG_ACTIVITY_MAIN,"Entered onActivityResult()");

        if(resultCode == RESULT_OK){
            switch (requestCode) {
                case ADD_ANNIVERSARY_ITEM_REQUEST:
                    Toast.makeText(getApplicationContext(), "Anniversary Saved With Success!", Toast.LENGTH_LONG).show();
                    return;
                case FILTER_ANNIVERSARY_SETTING_REQUEST:
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                    daysToFilter = Integer.valueOf(sharedPreferences.getString("FILTER_PREF_LIST", "no selection"));
                    Toast.makeText(getApplicationContext(), "Preferences Saved With Success!", Toast.LENGTH_LONG).show();
                    return;
                default:
            }
        }
        else if(resultCode == RESULT_CANCELED)
            Toast.makeText(getApplicationContext(), "Operation Canceled!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume(){
        super.onResume();

        CharSequence seq = getTimeFormat(this).format(new Date(SystemClock.elapsedRealtime()));
        d(TAG_ACTIVITY_MAIN, "$$ SystemClock.elapsedRealtime: " + seq);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Long notify_time_millis = sharedPreferences.getLong(AlarmStartupReceiver.TAG_SCHEDULE_NOTIFY_TIME, 0);
        seq = getTimeFormat(this).format(new Date(notify_time_millis));
        d(TAG_ACTIVITY_MAIN, "$$ StartupBootReceiver # notify_time: " + seq);

        loadItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, MENU_SETTINGS, Menu.NONE, "Settings");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_SETTINGS:
                startActivityForResult(
                        new Intent(MainActivity.this, PreferencesActivity.class),
                        FILTER_ANNIVERSARY_SETTING_REQUEST
                );
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadItems() {
        bAdapter.clear();
        for (AnniversaryItem item : getAnniversaryList(getApplicationContext())) {
            if(item.getDaysLeft() < daysToFilter)
                bAdapter.add(item);
        }
        bAdapter.orderList();
    }
}
