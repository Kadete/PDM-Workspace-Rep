package pt.isel.pdm.grupo17.thothnews.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import pt.isel.pdm.grupo17.thothnews.R;
import pt.isel.pdm.grupo17.thothnews.fragments.SlidingTabsColorsFragment;
import pt.isel.pdm.grupo17.thothnews.utils.TagUtils;

public class ReadAllDialogFragment extends DialogFragment {

    static long sClassID;

    public ReadAllDialogFragment(){ }

    public static  ReadAllDialogFragment newInstance(long classID){
        ReadAllDialogFragment f = new ReadAllDialogFragment();
        Bundle b = new Bundle();
        b.putLong(TagUtils.TAG_SELECT_CLASS_ID, classID);
        f.setArguments(b);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String clean = getString(R.string.news_read_all_title);
        String warning = getString(R.string.news_read_all_warning);
        String ok = getString(R.string.dialog_ok);
        String cancel = getString(R.string.dialog_cancel);
        final String toastSuccessMessage = getString(R.string.news_read_all_toast_success);
        final String toastFailMessage = getString(R.string.news_read_all_toast_fail);

        sClassID = getArguments().getLong(TagUtils.TAG_SELECT_CLASS_ID);

        return new AlertDialog.Builder(getActivity())
            .setIcon(R.drawable.ic_thoth)
            .setTitle(clean)
            .setMessage(warning)
            .setPositiveButton(ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    SlidingTabsColorsFragment fragment = (SlidingTabsColorsFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container_class_sections);
                    if (fragment != null) {
                        fragment.updateReadAll(sClassID);
                        Toast.makeText(getActivity(), toastSuccessMessage, Toast.LENGTH_LONG).show();
                        return;
                    }
                    Toast.makeText(getActivity(), toastFailMessage, Toast.LENGTH_LONG).show();
                }
            })
            .setNegativeButton(cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Do Nothing
                }
            }).create();
    }
}