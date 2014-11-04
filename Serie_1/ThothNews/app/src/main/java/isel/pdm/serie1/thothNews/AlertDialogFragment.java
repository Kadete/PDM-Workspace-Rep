package isel.pdm.serie1.thothNews;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.widget.Toast;

import java.util.Set;

/**
 * Created by Kadete on 02/11/2014.
 */
public class AlertDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                // Set Dialog Icon
                .setIcon(R.drawable.thoth_icon)
                // Set Dialog Title
                .setTitle("Clean Your Preferences")
                // Set Dialog Message
                .setMessage("Carefull!! You will lose all the previous preferences stored!")

                // Positive button
                .setPositiveButton("Set Default Preferences", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        Intent myIntent = menu_new_view Intent(Intent.ACTION_VIEW, Uri.parse(moma_url));
//                        startActivity(myIntent);

                        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        sharedPrefs.edit().remove("multi_select_list_key").commit();
                        Toast.makeText(getActivity(),"Preferences set to default!", Toast.LENGTH_LONG).show();

                        getFragmentManager().beginTransaction()
                                .replace(android.R.id.content, new SettingsFragment())
                                .commit();

                    }
                })

                // Negative Button
                .setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do Nothing
                    }
                }).create();
    }
}