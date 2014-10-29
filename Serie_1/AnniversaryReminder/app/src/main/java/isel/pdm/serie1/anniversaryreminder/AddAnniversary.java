package isel.pdm.serie1.anniversaryreminder;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.provider.ContactsContract.Contacts.Data;

import static android.provider.ContactsContract.Contacts.*;
import static android.provider.ContactsContract.CommonDataKinds.*;


public class AddAnniversary extends Activity {

    private static final int REQUEST_CODE_PICK_CONTACT = 0;
    public static final SimpleDateFormat ANNIVERSARY_FORMATTER = new SimpleDateFormat("yyyy:MM:dd", Locale.US);
    private static final String TAG_DEBUG = "DEBUG";

    private TextView contactTextView;

    private static ImageView addContactImageView;
    private static TextView dateView;
    private static String dateString;

    private long contactID;
    private String contactName;

    private Bitmap photo;
    private Uri _contactUri;
    static Calendar mCalendar = Calendar.getInstance();

    ContentValues mValues = new ContentValues();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_anniversary);

        contactTextView = (TextView) findViewById(R.id.name);
        addContactImageView = (ImageView) findViewById(R.id.contactImageView);
        dateView = (TextView) findViewById(R.id.dateView);

        setDefaultImage();
        setDefaultDate();

        final Button selectContactButton = (Button) findViewById(R.id.btnSelectContact);
        selectContactButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        CONTENT_URI);
                i.setType(Phone.CONTENT_TYPE);
                startActivityForResult(i, REQUEST_CODE_PICK_CONTACT);
            }
        });

        final Button datePickerButton = (Button) findViewById(R.id.date_picker_button);
        datePickerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        final Button cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG_DEBUG, "Entered cancelButton.OnClickListener.onClick()");

                setResult(RESULT_CANCELED);
                finish();
            }
        });

        final Button resetButton = (Button) findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG_DEBUG, "Entered resetButton.OnClickListener.onClick()");

                contactTextView.setText("N/A");

                setDefaultImage();
                setDefaultDate();
            }
        });

        final Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG_DEBUG, "Entered submitButton.OnClickListener.onClick()");

                String name = getName();

                if(name.compareToIgnoreCase("N/A") == 0 || dateString.compareTo("N/A") == 0){
                    Toast.makeText(getBaseContext(), "Select an contact and CHANGE / ADD his Anniversary!!", Toast.LENGTH_LONG).show();
                    return;
                }

                changeContactAnniversary();

                Intent data = new Intent();
                AnniversaryItem.packageIntent(data, name, photo, dateString);

                setResult(Activity.RESULT_OK, data);
                finish();
            }

        });
    }

    private void setDefaultImage(){

        Drawable myPhoto = getResources().getDrawable( R.drawable.ic_user_default);
        addContactImageView.setImageDrawable(myPhoto);
    }

    private void setDefaultDate() {
        mCalendar = Calendar.getInstance();
        dateView.setText("N/A");
    }

    private static void setDateString(Date date) {

        dateString = AnniversaryItem.FORMAT.format(
                (date == null) ? mCalendar.getTime() : date
        );
        dateView.setText(dateString);

    }

    private String getName() {
        return contactTextView.getText().toString();
    }

    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            mCalendar.set(year, monthOfYear, dayOfMonth);
            setDateString(null);
        }
    }

    private void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    private void changeContactAnniversary(){

        dateString = ANNIVERSARY_FORMATTER.format(mCalendar.getTime());
        String[] anniversaryInfo = dateString.split(":");

        int year = Integer.valueOf(anniversaryInfo[0]);
        int month = Integer.valueOf(anniversaryInfo[1]);
        month--;
        int day = Integer.parseInt(anniversaryInfo[2],10);

        retrieveAnniversaryDetails();

        if(dateString == null){
            Log.d("", "changeContactAnniversary() >> Anniversary_Uri: INSERT");
            saveAnniversaryDetails(year, month, day);
        }
        else {
            Log.d("", "changeContactAnniversary() >> Anniversary_Uri: UPDATE");
            updateAnniversaryDetails(year, month, day);
        }

        dateString = year+":" + (++month) + ":" + day;
    }

    private void saveAnniversaryDetails(int year,int month,int date)
    {
        Calendar cc= Calendar.getInstance();
        cc.set(year, month, date);
        Date dt = cc.getTime();
        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
        String annidate = df.format(dt);

        mValues.clear();
        mValues.put(Data.RAW_CONTACT_ID, contactID);
        mValues.put(Data.MIMETYPE,Event.CONTENT_ITEM_TYPE);
        mValues.put(Event.START_DATE,annidate);
        mValues.put(Event.TYPE,Event.TYPE_ANNIVERSARY);

        getContentResolver().insert(ContactsContract.Data.CONTENT_URI, mValues);

    }

    private void updateAnniversaryDetails(int year,int month,int date)
    {
        ContentResolver cr = getContentResolver();
        String where = ContactsContract.Data.MIMETYPE + " = ? ";
        String[] whereParams = new String[]{Event.CONTENT_ITEM_TYPE};
        Cursor anniversaryCur = getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                new String[] { Event.DATA },
                ContactsContract.Data.CONTACT_ID + "=" + String.valueOf(contactID) +
                        " AND " + Data.MIMETYPE + "= '" + Event.CONTENT_ITEM_TYPE +
                        "' AND " + Event.TYPE + "=" + Event.TYPE_ANNIVERSARY,
                null,
                ContactsContract.Data.DISPLAY_NAME
        );
        if(anniversaryCur.moveToFirst())
        {
            cr.delete(ContactsContract.Data.CONTENT_URI,where,whereParams);
        }
        anniversaryCur.close();
        saveAnniversaryDetails(year, month, date);
    }

    private void retrieveContactData(){
        retrieveContactNameAndId();
        retrieveContactPhoto();
        retrieveAnniversaryDetails();

        if(dateString == null)
            setDefaultDate();
        else{
            String[] anniversaryInfo = dateString.split(":");

            int year = Integer.valueOf(anniversaryInfo[0]);
            int month = Integer.valueOf(anniversaryInfo[1]);
            --month;
            int day = Integer.parseInt(anniversaryInfo[2],10);

            Date date = new Date(year, month, day);
            setDateString(date);
        }
    }


    //Get the contact Name and Id of the selected contact.
    private void retrieveContactNameAndId() {

        // getting contacts ID
        Cursor cursorID = getContentResolver().query(
            _contactUri,
            new String[] { _ID, DISPLAY_NAME  },
            null,
            null,
            null
        );

        if (cursorID.moveToFirst()) {
            contactID = cursorID.getLong(cursorID.getColumnIndexOrThrow(_ID));
            contactName = cursorID.getString(cursorID.getColumnIndex(DISPLAY_NAME));
        }

        cursorID.close();

        Log.d(TAG_DEBUG, "Contact ID: " + contactID);

    }

    //Retrieve the Contact photo based on the contactId
    private void retrieveContactPhoto() {

        try {
            InputStream inputStream = openContactPhotoInputStream(
                    getContentResolver(),
                    ContentUris.withAppendedId(CONTENT_URI, Long.valueOf(contactID))
            );

            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
                ImageView imageView = (ImageView) findViewById(R.id.contactImageView);
                imageView.setImageBitmap(photo);
            }
            else
                return;

            assert inputStream != null;
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //Retrieve the anniversary based on the contactId
    private void retrieveAnniversaryDetails()
    {
        dateString = null;
        Cursor anniversaryCur = getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                new String[] { Event.DATA },
                ContactsContract.Data.CONTACT_ID + "=" + contactID +
                        " AND " + Data.MIMETYPE + "= '" + Event.CONTENT_ITEM_TYPE +
                        "' AND " + Event.TYPE + "=" + Event.TYPE_ANNIVERSARY,
                null,
                ContactsContract.Data.DISPLAY_NAME
        );

        if(anniversaryCur.moveToFirst())
        {
            DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);

            try {
                Date mydate = df.parse( anniversaryCur.getString(0));
                dateString = ANNIVERSARY_FORMATTER.format(mydate);

            } catch (ParseException e) { e.printStackTrace(); }

            anniversaryCur.close();
        }
    }

    /** result from >> selectContactButton.setOnClickListener(new View.OnClickListener(){...onClick(){...) **/
    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data){
        if(resCode == RESULT_OK){

            switch (reqCode) {
                case REQUEST_CODE_PICK_CONTACT:
                    Uri contactUri = getLookupUri(getContentResolver(), data.getData());
                    Log.d("ContactsExample", contactUri.toString());


                    /** Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI); **/
                    _contactUri = data.getData();

                    retrieveContactData();

                    contactTextView.setText(contactName);

                    showDatePickerDialog();

                    break;
            }
        }
    }
}