package isel.pdm.serie1.anniversaryreminder;

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
import java.util.TimeZone;

import static android.provider.ContactsContract.CommonDataKinds.Event;
import static android.provider.ContactsContract.Contacts;
import static isel.pdm.serie1.anniversaryreminder.AnniversaryItem.ITEM_SEP;
import static isel.pdm.serie1.anniversaryreminder.Utils.d;


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

        Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());
        currentDayOfYear = localCalendar.get(Calendar.DAY_OF_YEAR);

        bAdapter = new AnniversaryAdapter(getApplicationContext());

        View inflater = getLayoutInflater().inflate(R.layout.layout_header_list, null);
        TextView headerView = (TextView) inflater.findViewById(R.id.headerView);

        if (null == headerView) {
            return;
        }

        getListView().setHeaderDividersEnabled(true);

        getListView().addHeaderView(headerView);

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                d("Entered footerView.OnClickListener.onClick()");

                Intent i = new Intent(MainActivity.this, AddAnniversary.class);
                startActivityForResult(i, ADD_ANNIVERSARY_ITEM_REQUEST);
            }
        });

        getListView().setBackgroundResource(R.drawable.orange_background_grad);

        setListAdapter(bAdapter);
//        ListView lv = getListView();
//        lv.setDivider(new PaintDrawable(R.color.sage));
//        lv.setDividerHeight(1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        d("Entered onActivityResult()");

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
        d("MainActivity, onResume Called");
        loadItems();
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d("DEBUG","MainActivity, onPause Called");
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
        menu.add(Menu.NONE, MENU_DUMP, Menu.NONE, "Dump to log");

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
            case MENU_DUMP:
                dump();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void dump() {
        for (int i = 0; i < bAdapter.getCount(); i++) {
            String data = ((AnniversaryItem) bAdapter.getItem(i)).toLog();
            d("Item " + i + ": " + data.replace(ITEM_SEP, ","));
        }
    }

    private boolean isToFilter(Date anniversaryDate){
        Calendar calendarAnnDate = Calendar.getInstance();
        calendarAnnDate.setTime(anniversaryDate);
        int annDayOfYear = calendarAnnDate.get(Calendar.DAY_OF_YEAR);

        return annDayOfYear >= currentDayOfYear && annDayOfYear <= (currentDayOfYear+daysToFilter);
    }

    private List<AnniversaryItem> getAnniversaryList()
    {
        List<AnniversaryItem>  anniversaryItems = new LinkedList<AnniversaryItem>();
        String contactName;
        Date contactAnniDate;
        Uri contactThumbUri;

        Cursor anniCursor = getContentResolver().query(

                ContactsContract.Data.CONTENT_URI,
                new String[] { Contacts.DISPLAY_NAME, Event.DATA, Contacts.PHOTO_THUMBNAIL_URI},
                        ContactsContract.Data.MIMETYPE + "= '" + Event.CONTENT_ITEM_TYPE +
                        "' AND " + Event.TYPE + "=" + Event.TYPE_ANNIVERSARY,
                null,
                ContactsContract.Data.DISPLAY_NAME
        );

        int nameCol = anniCursor.getColumnIndex(Contacts.DISPLAY_NAME);
        int dateCol = anniCursor.getColumnIndex(Event.START_DATE);
        int photoCol = anniCursor.getColumnIndex(Contacts.PHOTO_THUMBNAIL_URI);

        while(anniCursor.moveToNext())
        {
            contactThumbUri = null;
            DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
            try {
                contactName = anniCursor.getString(nameCol);
                contactAnniDate = df.parse(anniCursor.getString(dateCol));

                String contactThumbStr = anniCursor.getString(photoCol);
                if(contactThumbStr != null)
                    contactThumbUri = Uri.parse(contactThumbStr);

                AnniversaryItem ann = new AnniversaryItem(contactName, contactAnniDate, contactThumbUri);
                anniversaryItems.add(ann);
            } catch (ParseException e) { e.printStackTrace(); }


        }
        anniCursor.close();
        return anniversaryItems;
    }

    private void loadItems() {

        bAdapter.clear();
        for (AnniversaryItem item : getAnniversaryList()) {
            if (isToFilter(item.getDate()))
                bAdapter.add(item);
        }

        bAdapter.orderList();

    }
}
