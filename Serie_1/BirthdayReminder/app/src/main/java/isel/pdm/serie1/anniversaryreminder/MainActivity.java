package isel.pdm.serie1.anniversaryreminder;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Date;

import static isel.pdm.serie1.anniversaryreminder.AnniversaryItem.*;


public class MainActivity extends ListActivity {

    private static final int ADD_ANNIVERSARY_ITEM_REQUEST = 0;
    private static final String FILE_NAME = "AnniversaryActivityData.txt";
    private static final String TAG = "Lab-UserInterface";

    private static final int MENU_DELETE = Menu.FIRST;
    private static final int MENU_DUMP = Menu.FIRST + 1;

    AnniversaryAdapter bAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bAdapter = new AnniversaryAdapter(getApplicationContext());

        getListView().setHeaderDividersEnabled(true);

        View inflater = getLayoutInflater().inflate(R.layout.add_header_view, null);
        TextView headerView = (TextView) inflater.findViewById(R.id.headerView);

        if (null == headerView) {
            return;
        }

        getListView().addHeaderView(headerView);

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i(TAG, "Entered footerView.OnClickListener.onClick()");

                Intent i = new Intent(MainActivity.this, AddAnniversary.class);
                startActivityForResult(i, ADD_ANNIVERSARY_ITEM_REQUEST);
            }
        });

        setListAdapter(bAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(TAG,"Entered onActivityResult()");

        if(resultCode == RESULT_OK && requestCode == ADD_ANNIVERSARY_ITEM_REQUEST){

            AnniversaryItem item = new AnniversaryItem(data);
            bAdapter.add(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (bAdapter.getCount() == 0)
            loadItems();
    }

    @Override
    protected void onPause() {
        super.onPause();

        saveItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(Menu.NONE, MENU_DELETE, Menu.NONE, "Delete all");
        menu.add(Menu.NONE, MENU_DUMP, Menu.NONE, "Dump to log");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
            Log.i(TAG,	"Item " + i + ": " + data.replace(ITEM_SEP, ","));
        }

    }

    private void loadItems() {
        BufferedReader reader = null;
        try {
            FileInputStream fis = openFileInput(FILE_NAME);
            reader = new BufferedReader(new InputStreamReader(fis));

            String title;
            Date date;

            while (null != (title = reader.readLine())) {
                date = FORMAT.parse(reader.readLine());
                bAdapter.add(new AnniversaryItem(title, date));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveItems() {
        PrintWriter writer = null;
        try {
            FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    fos)));

            for (int idx = 0; idx < bAdapter.getCount(); idx++) {
                writer.println(bAdapter.getItem(idx));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != writer) {
                writer.close();
            }
        }
    }

}
