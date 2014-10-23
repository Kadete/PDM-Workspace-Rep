package isel.pdm.serie1.birthdayreminder;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentUris;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

import static isel.pdm.serie1.birthdayreminder.BirthdayItem.*;


public class AddBirthday extends Activity {

    private static final int REQUEST_CODE_PICK_CONTACTS = 0;
    private static final String TAG = "Lab-UserInterface";

    private TextView contactTextView;

    private static ImageView addContactImageView;
    private static TextView dateView;
    private static String dateString;

    private String contactID, contactName, contactNumber; // contacts unique ID
    private Bitmap photo;
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
                        ContactsContract.Contacts.CONTENT_URI);
                i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(i, REQUEST_CODE_PICK_CONTACTS);
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

                /********************************/

                Intent data = new Intent();
                packageIntent(data, titleString, photo ,dateString);

                setResult(Activity.RESULT_OK, data);
                finish();

            }

        });
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
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            mDate = new Date(year,monthOfYear, dayOfMonth);
            setDateString();
            dateView.setText(dateString);
        }
    }

    private void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    //Retrieve the Contact photo based on the contactId
    private void retrieveContactPhoto() {

        try {
            InputStream inputStream = ContactsContract.Contacts
                    .openContactPhotoInputStream(getContentResolver(),
                            ContentUris.withAppendedId(
                                    ContactsContract.Contacts.CONTENT_URI,
                                    Long.valueOf(contactID)));

            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
                ImageView imageView = (ImageView) findViewById(R.id.contactImageView);
                imageView.setImageBitmap(photo);
            }

            assert inputStream != null;
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Get the contact number of the selected contact.
    private void retrieveContactNumber() {

        // getting contacts ID
        Cursor cursorID = getContentResolver().query(_contactUri,
                new String[] { ContactsContract.Contacts._ID,ContactsContract.Contacts.DISPLAY_NAME  }, null, null,
                null);

        if (cursorID.moveToFirst()) {

            contactID = cursorID.getString(cursorID
                    .getColumnIndex(ContactsContract.Contacts._ID));
            contactName = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }

        cursorID.close();

        Log.d(TAG, "Contact ID: " + contactID);

        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER },

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND "
                        + ContactsContract.CommonDataKinds.Phone.TYPE + " = "
                        + ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[] { contactID }, null);

        if (cursorPhone.moveToFirst()) contactNumber = cursorPhone
                .getString(cursorPhone
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

        cursorPhone.close();

        Log.d(TAG, "Contact Phone Number: " + contactNumber);
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data){
        if(reqCode == REQUEST_CODE_PICK_CONTACTS && resCode == RESULT_OK){



            Uri contactUri = ContactsContract.Contacts.getLookupUri(getContentResolver(), data.getData());
            Log.d("ContactsExample", contactUri.toString());

            _contactUri = data.getData();

            retrieveContactNumber();
            retrieveContactPhoto();

            contactTextView.setText(contactName + ": " + contactNumber);

        }
    }
}
