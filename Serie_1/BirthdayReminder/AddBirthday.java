package isel.pdm.serie1.birthdayreminder;

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
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
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

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.provider.ContactsContract.CommonDataKinds.*;
import static android.provider.ContactsContract.Contacts.*;
import static isel.pdm.serie1.birthdayreminder.BirthdayItem.FORMAT;
import static isel.pdm.serie1.birthdayreminder.BirthdayItem.packageIntent;

public class AddBirthday extends Activity {

    private static final int REQUEST_CODE_PICK_CONTACT = 0;
    public static final SimpleDateFormat BIRTHDAY_FORMATTER = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.US);
    private static final String TAG = "Lab-UserInterface";

    private TextView contactTextView;

    private static ImageView addContactImageView;
    private static TextView dateView;
    private static String dateString;

    private ContentValues mValues = new ContentValues();;

    private long contactID;
    private String contactName;
    private String contactNumber; // contacts unique ID
    private String contactBirthDay;
    private Uri _contactUri;

    static Date mDate = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_birthday);

        contactTextView = (TextView) findViewById(R.id.title);
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

                Log.i(TAG, "Entered cancelButton.OnClickListener.onClick()");

                setResult(RESULT_CANCELED);
                finish();
            }
        });

        final Button resetButton = (Button) findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Entered resetButton.OnClickListener.onClick()");

                contactTextView.setText("N/A");

                setDefaultImage();
                setDefaultDate();
            }
        });

        final Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i(TAG, "Entered submitButton.OnClickListener.onClick()");

                String titleString = getToDoTitle();

                if(titleString.compareToIgnoreCase("N/A") == 0){
                    Toast.makeText(getBaseContext(), "Select an contact to CHANGE / ADD his birthday!!", Toast.LENGTH_LONG).show();
                    return;
                }

                /************* TODO *************/

                changeContactBirthday();
                
                /********************************/

                Intent data = new Intent();
                packageIntent(data, titleString, GetContactPhoto(getApplicationContext()) ,dateString);

                setResult(Activity.RESULT_OK, data);
                finish();

            }

        });
    }

    final public Bitmap GetContactPhoto(android.content.Context context)
    {
        ContentResolver cr = context.getContentResolver();

        Uri uri = ContentUris.withAppendedId(CONTENT_URI, contactID);

        InputStream input = openContactPhotoInputStream(cr, uri);

        if (input == null) {
            return null;
        }

        Bitmap photo = BitmapFactory.decodeStream(input);
        ImageView imageView = (ImageView) findViewById(R.id.contactImageView);
        imageView.setImageBitmap(photo);

        return BitmapFactory.decodeStream(input);
    }


    private void setDefaultImage(){

        Drawable myPhoto = getResources().getDrawable( R.drawable.ic_launcher );
        ColorFilter filter = new LightingColorFilter( Color.BLUE, Color.BLUE );
        myPhoto.setColorFilter(filter);

        addContactImageView.setImageDrawable(myPhoto);
    }

    private void setDefaultDate() {
        mDate = new Date();
        setDateString();
        dateView.setText(dateString);
    }

    private static void setDateString() {
        dateString = FORMAT.format(mDate);
    }

    private String getToDoTitle() {
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

            mDate = new Date(year,monthOfYear, dayOfMonth);
            setDateString();
            dateView.setText(dateString);
        }
    }

    private void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    private void changeContactBirthday(){

        String date = String.valueOf(BIRTHDAY_FORMATTER.format(mDate));

        /** Dia de Nascimento **/
//        Calendar cc= Calendar.getInstance();
//        cc.set(year, month, date);
//        Date dt = cc.getTime();
//        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
//        String birdate = df.format(dt);
//        Log.i("BirthdayDate",birdate);
//        values.clear();
//        values.put(Data.RAW_CONTACT_ID, rawContactId);
//        values.put(Data.MIMETYPE,Event.CONTENT_ITEM_TYPE);
//        values.put(Event.START_DATE,birdate);
//        values.put(Event.TYPE,Event.TYPE_BIRTHDAY);
//        _m_instance.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);

//        Date dt = mDate;
//        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
//        String anniversary_date = df.format(dt);
//        mValues.clear();
//        mValues.put(Data.RAW_CONTACT_ID, contactID);
//        mValues.put(Data.MIMETYPE, Event.CONTENT_ITEM_TYPE);
//        mValues.put(Event.START_DATE,anniversary_date);
//        mValues.put(Event.TYPE,Event.TYPE_ANNIVERSARY);
//        getContentResolver().insert(ContactsContract.Data.CONTENT_URI, mValues);


//        mValues.clear();
//        mValues.put(Event.TYPE, Event.TYPE_ANNIVERSARY); //TYPE_ANNIVERSARY
//        mValues.put(Data.MIMETYPE, Event.CONTENT_ITEM_TYPE);
//
//        mValues.put(Data.RAW_CONTACT_ID, contactID);
//        mValues.put(Event.START_DATE, date);
//
//        ContentResolver cr = getContentResolver();
//        if(contactBirthDay != null)
//            cr.update(ContactsContract.Data.CONTENT_URI, mValues, null , null);
//        else {
//            Uri birthDay_Uri = cr.insert(ContactsContract.Data.CONTENT_URI, mValues);
//            Log.d("changeContactBirthday() >> birthDay_Uri: ", String.valueOf(birthDay_Uri));
//        }
    }

    private void retrieveContactData(){
        retrieveContactNumberNameAndId();
        retrieveContactAnniversary();
    }


    //Get the contact number of the selected contact.
    private void retrieveContactNumberNameAndId() {

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

        Log.d(TAG, "Contact ID: " + contactID);

        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(
                Phone.CONTENT_URI,
                new String[] { Phone.NUMBER },
                Phone.CONTACT_ID + " = ? AND "
                        + Phone.TYPE + " = "
                        + Phone.TYPE_MOBILE,
                new String[] {String.valueOf(contactID)},
                null
        );

        if (cursorPhone.moveToFirst())
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(Phone.NUMBER));
        cursorPhone.close();

        Log.d(TAG, "Contact Phone Number: " + contactNumber);
    }


    //Retrieve the Contact birthday based on the contactId
    private void retrieveContactAnniversary() {

        ContentResolver cr = getApplicationContext().getContentResolver();

        Uri uri = ContactsContract.Data.CONTENT_URI;

        String[] projection = new String[] {
                ContactsContract.Data.CONTACT_ID,
                Event.START_DATE,
                ContactsContract.Data.MIMETYPE,
                Event.TYPE
        };

        String where = ContactsContract.Data.CONTACT_ID + "=?"
                + " AND " + ContactsContract.Data.MIMETYPE + "=?"
                + " AND " + Event.TYPE + "=?";

        // Add contactID filter.
        String[] selectionArgs = new String[] {
                String.valueOf(contactID),
                Event.CONTENT_ITEM_TYPE,
                String.valueOf(Event.TYPE_ANNIVERSARY)
        };

        String sortOrder = null;

        Cursor cursor= cr.query(uri, projection, where, selectionArgs, sortOrder);

        while (cursor.moveToFirst()) {
            contactBirthDay = cursor.getString(cursor.getColumnIndex(Event.START_DATE));
        }
        cursor.close();
    }

    /** result from >> selectContactButton.setOnClickListener(new View.OnClickListener(){...onClick(){...) **/
    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data){
        if(resCode == RESULT_OK){

            switch (reqCode) {
                case REQUEST_CODE_PICK_CONTACT:
                    Uri contactUri = getLookupUri(getContentResolver(), data.getData());
                    Log.d("ContactsExample", contactUri.toString());


                    //Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    _contactUri = data.getData();

                    retrieveContactData();

                    contactTextView.setText(contactName + ": " + contactNumber);
                    break;
            }
        }
    }
}