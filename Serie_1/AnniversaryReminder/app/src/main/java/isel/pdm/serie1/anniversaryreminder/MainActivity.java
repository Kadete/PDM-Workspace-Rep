package isel.pdm.serie1.anniversaryreminder;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import static isel.pdm.serie1.anniversaryreminder.AnniversaryItem.ITEM_SEP;


public class MainActivity extends ListActivity {

    private static final int ADD_ANNIVERSARY_ITEM_REQUEST = 0;
    private static final int FILTER_ANNIVERSARY_SETTING_REQUEST = 1;

//    public static final SimpleDateFormat ANNIVERSARY_FORMATTER = new SimpleDateFormat("yyyy:MM:dd", Locale.US);
//    private static final String FILE_NAME = "AnniversaryActivityData.txt";
    private static final String TAG_DEBUG = "DEBUG";

    private static final int MENU_SETTINGS = Menu.FIRST;
    private static final int MENU_DELETE = Menu.FIRST +1;
    private static final int MENU_DUMP = Menu.FIRST + 2;

    private static int daysToFilter, currentDayOfYear;

    AnniversaryAdapter bAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        daysToFilter = Integer.valueOf(sharedPreferences.getString("PREF_LIST", "no selection"));

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

                Log.d(TAG_DEBUG, "Entered footerView.OnClickListener.onClick()");

                Intent i = new Intent(MainActivity.this, AddAnniversary.class);
                startActivityForResult(i, ADD_ANNIVERSARY_ITEM_REQUEST);
            }
        });

        setListAdapter(bAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG_DEBUG,"Entered onActivityResult()");

        if(resultCode == RESULT_OK){
            switch (requestCode) {
                case ADD_ANNIVERSARY_ITEM_REQUEST:
                    AnniversaryItem item = new AnniversaryItem(data);
                    if(isToFilter(item.getDate())) {
                        bAdapter.add(item);
                        bAdapter.orderList();

                    }
                    return;
                case FILTER_ANNIVERSARY_SETTING_REQUEST:

                    //saveItems();

                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                    daysToFilter = Integer.valueOf(sharedPreferences.getString("PREF_LIST", "no selection"));

                    bAdapter.clear();
                    loadItems();

                    return;
                default:
                    return;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

//        if (bAdapter.getCount() == 0)
            loadItems();
    }


    @Override
    protected void onPause() {
        super.onPause();

//        saveItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(Menu.NONE, MENU_SETTINGS, Menu.NONE, "Settings");
        menu.add(Menu.NONE, MENU_DELETE, Menu.NONE, "Delete all");
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
            case MENU_DELETE:
                bAdapter.clear();
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
            Log.d(TAG_DEBUG, "Item " + i + ": " + data.replace(ITEM_SEP, ","));
        }
    }

    private boolean isToFilter(Date anniversaryDate){
        Calendar calendarAnnDate = Calendar.getInstance();
        calendarAnnDate.setTime(anniversaryDate);
        int annDayOfYear = calendarAnnDate.get(Calendar.DAY_OF_YEAR);

        return annDayOfYear >= currentDayOfYear && annDayOfYear <= (currentDayOfYear+daysToFilter);
    }

    //Retrieve the anniversary based on the contactId
    private List<AnniversaryItem> getAnniversaryList()
    {
        List<AnniversaryItem>  anniversaryItems = new LinkedList<AnniversaryItem>();
        String contactName;
        Date myDate;

        Cursor anniversaryCur = getContentResolver().query(

                ContactsContract.Data.CONTENT_URI,
                new String[] { ContactsContract.Data.DISPLAY_NAME, ContactsContract.CommonDataKinds.Event.DATA},
                        ContactsContract.Contacts.Data.MIMETYPE + "= '" + ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE +
                        "' AND " + ContactsContract.CommonDataKinds.Event.TYPE + "=" + ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY,
                null,
                ContactsContract.Data.DISPLAY_NAME
        );

        while(anniversaryCur.moveToNext())
        {
            DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);

            try {
                contactName = anniversaryCur.getString(0);
                myDate = df.parse( anniversaryCur.getString(1));

                AnniversaryItem ann = new AnniversaryItem(contactName, myDate);

                anniversaryItems.add(ann);

            } catch (ParseException e) { e.printStackTrace(); }


        }
        anniversaryCur.close();
        return anniversaryItems;
    }

    private void loadItems() {
//        BufferedReader reader = null;
//        try {
//            FileInputStream fis = openFileInput(FILE_NAME);
//            reader = new BufferedReader(new InputStreamReader(fis));

//            String title;
//            Date anniversaryDate;

            /** Guardar num ficheiro vs query **/
            /*-------------------file-------------------*/
//            while (null != (title = reader.readLine())) {
//                anniversaryDate = FORMAT.parse(reader.readLine());
//
//                if(isToFilter(anniversaryDate))
//                    bAdapter.add(new AnniversaryItem(title, anniversaryDate));
//            }
            /*-------------------query-------------------*/
            for(AnniversaryItem item : getAnniversaryList()){
                if(isToFilter(item.getDate()))
                    bAdapter.add(item);
            }

            bAdapter.orderList();

//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        } finally {
//            if (null != reader) {
//                try {
//                    reader.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }

//    private void saveItems() {
//        PrintWriter writer = null;
//        try {
//            FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
//            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
//                    fos)));
//
//            for (int idx = 0; idx < bAdapter.getCount(); idx++) {
//                writer.println(bAdapter.getItem(idx));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (null != writer) {
//                writer.close();
//            }
//        }
//    }

}
