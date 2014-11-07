package pt.isel.pdm.grupo17.thothnews.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import pt.isel.pdm.grupo17.thothnews.R;

public class AlertDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String clean = getString((R.string.clean_classes_title));
        String warning = getString(R.string.clean_classes_warning);
        String ok = getString(R.string.clean_clasess_ok);
        String cancel = getString(R.string.clean_classes_cancel);
        final String toastMessage = getString(R.string.clean_classes_toast);

        return new AlertDialog.Builder(getActivity())
                // Set Dialog Icon
                .setIcon(R.drawable.thoth_icon)
                // Set Dialog Title
                .setTitle(clean)
                // Set Dialog Message
                .setMessage(warning)

                // Positive button
                .setPositiveButton(ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        Intent myIntent = menu_new_view Intent(Intent.ACTION_VIEW, Uri.parse(moma_url));
//                        startActivity(myIntent);

                        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        sharedPrefs.edit().remove("multi_select_list_key").commit();
                        Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_LONG).show();

                        getFragmentManager().beginTransaction()
                                .replace(android.R.id.content, new SettingsFragment())
                                .commit();

                    }
                })

                // Negative Button
                .setNegativeButton(cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do Nothing
                    }
                }).create();
    }
}