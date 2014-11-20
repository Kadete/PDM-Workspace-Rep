package pt.isel.pdm.grupo17.anniversaryreminder.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import android.provider.ContactsContract.Contacts.Data;

import pt.isel.pdm.grupo17.anniversaryreminder.R;
import pt.isel.pdm.grupo17.anniversaryreminder.utils.DateUtils;

import static android.provider.ContactsContract.Contacts.*;
import static android.provider.ContactsContract.CommonDataKinds.*;
import static pt.isel.pdm.grupo17.anniversaryreminder.utils.Utils.*;


public class AddAnniversaryActivity extends Activity {

    private static final int REQUEST_CODE_PICK_CONTACT = 0;
    private static final int YEAR_POS = 0;
    private static final int MONTH_POS = 1;
    private static final int DAY_POS = 2;

    private static ImageView contactImageView;
    private static TextView dateView, contactTextView;
    private static String dateString;

    private long contactID;
    private int rawContactID = -1;
    private String contactName;

    private Uri _contactUri, contactPhotoUri = null;

    static Calendar mCalendar = Calendar.getInstance();
    ContentValues mValues = new ContentValues();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_anniversary);

        contactTextView = (TextView) findViewById(R.id.name);
        contactImageView = (ImageView) findViewById(R.id.contactImageView);
        dateView = (TextView) findViewById(R.id.dateView);

        setDefaultName();
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
                d(TAG_ACTIVITY,"Entered cancelButton.OnClickListener.onClick()");

                setResult(RESULT_CANCELED);
                finish();
            }
        });

        final Button resetButton = (Button) findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d(TAG_ACTIVITY,"Entered resetButton.OnClickListener.onClick()");

                setDefaultName();
                setDefaultImage();
                setDefaultDate();
            }
        });

        final Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d(TAG_ACTIVITY,"Entered submitButton.OnClickListener.onClick()");

                String name = contactTextView.getText().toString();
                if(name.compareToIgnoreCase("N/A") == 0 || dateString.compareTo("N/A") == 0){
                    Toast.makeText(getBaseContext(), "Select an contact and CHANGE / ADD his Anniversary!!", Toast.LENGTH_LONG).show();
                    return;
                }
                if(! changeContactAnniversary()){
                    Toast.makeText(getBaseContext(), "Couldn't Change Your Contact Anniversary!!", Toast.LENGTH_LONG).show();
                }else
                    setResult(Activity.RESULT_OK, null);
                finish();
            }

        });
    }

    private void setDefaultName() {
        contactTextView.setText("N/A");
    }

    private void setDefaultImage(){
        Drawable myPhoto = getResources().getDrawable( R.drawable.ic_user_default);
        contactImageView.setImageDrawable(myPhoto);
    }

    private void setDefaultDate() {
        mCalendar = Calendar.getInstance();
        dateView.setText("N/A");
    }

    private static void setDateString(Date date) {
        dateString = DateUtils.SHOW_DATE_FORMATTER.format(
                (date == null) ? mCalendar.getTime() : date
        );
        dateView.setText(dateString);
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

    private void setValues() {
        dateString = DateUtils.SAVE_DATE_FORMATTER.format(mCalendar.getTime());
        int[] anniversaryInfo = DateUtils.parseDateFromString(dateString,":");

        Calendar cc= Calendar.getInstance();
        cc.set(anniversaryInfo[YEAR_POS],
                DateUtils.getCalendarMonth(anniversaryInfo[MONTH_POS]),
                anniversaryInfo[DAY_POS]);

        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
        String anniversaryDate = df.format(cc.getTime());

        Cursor c= getContentResolver().query(_contactUri, null, null, null, null);
        int rawIx = c.getColumnIndex(Data.RAW_CONTACT_ID);
        if(c.moveToFirst()) {
            rawContactID = c.getInt(rawIx);
        }

        mValues.clear();
        mValues.put(Data.MIMETYPE, Event.CONTENT_ITEM_TYPE);
        mValues.put(Data.RAW_CONTACT_ID, rawContactID);

        mValues.put(Event.TYPE, Event.TYPE_ANNIVERSARY);
        mValues.put(Event.START_DATE, anniversaryDate);
    }


    private boolean changeContactAnniversary(){

        setValues();

        String anniQueryWhere = ContactsContract.Data.MIMETYPE + " = ? AND "+ Event.RAW_CONTACT_ID + " = ? AND "+ Event.TYPE  + " = ?";
        String[] anniQueryWhereParams = new String[]{Event.CONTENT_ITEM_TYPE, String.valueOf(rawContactID), String.valueOf(Event.TYPE_ANNIVERSARY)};

        ContentResolver cr = getContentResolver();

        Cursor anniversaryCur = cr.query(
                ContactsContract.Data.CONTENT_URI,
                new String[] { Event.START_DATE },
                anniQueryWhere,
                anniQueryWhereParams,
                null
        );

        try{
            if(anniversaryCur.moveToFirst())
            {
                d(TAG_ACTIVITY,"changeContactAnniversary() >> Anniversary_Uri: UPDATE");
                return (cr.update(ContactsContract.Data.CONTENT_URI, mValues, anniQueryWhere, anniQueryWhereParams) !=-1);
            }
            else{
                d(TAG_ACTIVITY,"changeContactAnniversary() >> Anniversary_Uri: INSERT");
                return (cr.insert(ContactsContract.Data.CONTENT_URI, mValues) != null);
            }
        }
        finally {
            anniversaryCur.close();
        }
    }

    //Get the contact Name and Id of the selected contact.
    private void retrieveContactData() {

        Cursor cursorID = getContentResolver().query(
            _contactUri,
            new String[] { _ID, DISPLAY_NAME, PHOTO_THUMBNAIL_URI  },
            null,
            null,
            null
        );

        if (cursorID.moveToFirst()) {
            contactID = cursorID.getLong(cursorID.getColumnIndexOrThrow(_ID));
            contactName = cursorID.getString(cursorID.getColumnIndex(DISPLAY_NAME));


            String photoStr = cursorID.getString(cursorID.getColumnIndex(PHOTO_THUMBNAIL_URI));
            if(photoStr != null) {
                contactPhotoUri = Uri.parse(photoStr);
            }
        }

        cursorID.close();

        d(TAG_ACTIVITY,"Contact ID: " + contactID);
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
                    if(contactPhotoUri != null)
                        contactImageView.setImageURI(contactPhotoUri);

                    showDatePickerDialog();

                    break;
            }
        }
    }
}