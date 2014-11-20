package pt.isel.pdm.grupo17.anniversaryreminder.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.PaintDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import pt.isel.pdm.grupo17.anniversaryreminder.R;
import pt.isel.pdm.grupo17.anniversaryreminder.adapters.AnniversaryAdapter;
import pt.isel.pdm.grupo17.anniversaryreminder.models.AnniversaryItem;
import pt.isel.pdm.grupo17.anniversaryreminder.utils.CursorUtils;
import pt.isel.pdm.grupo17.anniversaryreminder.utils.Utils;

import static android.provider.ContactsContract.CommonDataKinds.Event;
import static android.provider.ContactsContract.Contacts;
import static pt.isel.pdm.grupo17.anniversaryreminder.models.AnniversaryItem.ITEM_SEP;
import static pt.isel.pdm.grupo17.anniversaryreminder.utils.Utils.*;
import static pt.isel.pdm.grupo17.anniversaryreminder.utils.Utils.d;


public class MainActivity extends ListActivity {

    private static final int ADD_ANNIVERSARY_ITEM_REQUEST = 0;
    private static final int FILTER_ANNIVERSARY_SETTING_REQUEST = 1;

    private static final int MENU_SETTINGS = Menu.FIRST;
    private static final int MENU_DUMP = Menu.FIRST + 1;

    private static int daysToFilter, currentDayOfYear;

    AnniversaryAdapter bAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String aux = sharedPreferences.getString("PREF_LIST", "14");
        daysToFilter = Integer.valueOf(aux);

        Calendar localCalendar = Calendar.getInstance();
        currentDayOfYear = localCalendar.get(Calendar.DAY_OF_YEAR);

        bAdapter = new AnniversaryAdapter(getApplicationContext());

        View inflater = getLayoutInflater().inflate(R.layout.layout_header_list, null);
        TextView headerView = (TextView) inflater.findViewById(R.id.headerView);

        if (null == headerView) {
            return;
        }

//        getListView().setHeaderDividersEnabled(true);

        getListView().addHeaderView(headerView);

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                d(TAG_ACTIVITY,"Entered footerView.OnClickListener.onClick()");

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
        d(TAG_ACTIVITY,"Entered onActivityResult()");

        if(resultCode == RESULT_OK){
            switch (requestCode) {
                case ADD_ANNIVERSARY_ITEM_REQUEST:
                    Toast.makeText(getApplicationContext(), "Anniversary Saved With Success!", Toast.LENGTH_LONG).show();
                    return;
                case FILTER_ANNIVERSARY_SETTING_REQUEST:
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                    daysToFilter = Integer.valueOf(sharedPreferences.getString("PREF_LIST", "no selection"));
                    Toast.makeText(getApplicationContext(), "Preferences Saved With Success!", Toast.LENGTH_LONG).show();
                    return;
                default:
                    return;
            }
        }
        else if(resultCode == RESULT_CANCELED)
            Toast.makeText(getApplicationContext(), "Operation Canceled!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume(){
        super.onResume();
        d(TAG_ACTIVITY,"MainActivity, onResume Called");
        loadItems();
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d("DEBUG", "MainActivity, onPause Called");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d("DEBUG","MainActivity, onStop Called");
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Log.d("DEBUG", "MainActivity, onRestart Called");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d("DEBUG", "MainActivity, onDestroy Called");
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

    private boolean isToFilter(Date anniversaryDate){
        Calendar filterDate = Calendar.getInstance(), anvDate = Calendar.getInstance();
        anvDate.setTime(anniversaryDate);
        filterDate.add(Calendar.DAY_OF_YEAR, daysToFilter);
        Calendar today = Calendar.getInstance();
        return anvDate.get(Calendar.DAY_OF_YEAR)>= today.get(Calendar.DAY_OF_YEAR) && anvDate.before(filterDate);
    }

    private void loadItems() {
        bAdapter.clear();
        for (AnniversaryItem item : CursorUtils.getAnniversaryList(getApplicationContext())) {
            if (isToFilter(item.getDate())){
                bAdapter.add(item);
            }
        }
        bAdapter.orderList();
    }
}
