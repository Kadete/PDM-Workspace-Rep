package pt.isel.pdm.grupo17.thothnews.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.data.ThothContract;
import pt.isel.pdm.grupo17.thothnews.utils.TagUtils;

import static pt.isel.pdm.grupo17.thothnews.data.providers.SQLiteUtils.FALSE;

public class CleanPreferencesDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String title = getString((R.string.clean_classes_title));
        String warning = getString(R.string.clean_classes_warning);
        String ok = getString(R.string.dialog_ok);
        String cancel = getString(R.string.dialog_cancel);
        final String toastSuccessMessage = getString(R.string.clean_classes_toast);

        return new AlertDialog.Builder(getActivity())
            .setIcon(R.drawable.ic_thoth)
            .setTitle(title)
            .setMessage(warning)
            .setPositiveButton(ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    ContentValues values = new ContentValues();
                    values.put(ThothContract.Classes.ENROLLED, FALSE);
                    getActivity().getContentResolver().update(ThothContract.Classes.CONTENT_URI, values, null, null);

                    Toast.makeText(getActivity(), toastSuccessMessage, Toast.LENGTH_SHORT).show();
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    sharedPreferences.edit().putStringSet(TagUtils.TAG_SELECTED_CLASSES, null).apply();
                }
            })
            .setNegativeButton(cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Do Nothing
                }
            }).create();
    }
}