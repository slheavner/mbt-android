package com.slheavner.wvubus.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import com.slheavner.wvubus.R;
import com.slheavner.wvubus.utils.PrefsUtil;

/**
 * Shows list of bus names and numbers with checkboxes in a dialog. Save applies the changes to SharedPreferences.
 * Cancel cancels. Opened from the FloatingActionButton on the home screen.
 */
public class BusSelectDialogFragment extends DialogFragment {

    private boolean[] checks;
    private PrefsUtil prefs;
    private BusSelectClosedListener listener;

    public static BusSelectDialogFragment newInstance(BusSelectClosedListener listener){
        BusSelectDialogFragment busSelectDialogFragment = new BusSelectDialogFragment();
        busSelectDialogFragment.listener = listener;
        return busSelectDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String title = "Bus Select",
                success = "Save",
                cancel = "Cancel";
        prefs = new PrefsUtil(getActivity());
        checks = prefs.getBusEnabledBool();

        builder = builder.setTitle(title)
                .setMultiChoiceItems(R.array.bus_full, prefs.getBusEnabledBool(), new DialogInterface.OnMultiChoiceClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checks[which] = isChecked;
                    }
                })
                .setPositiveButton(success, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        prefs.setBusesEnabled(checks);
                        if(BusSelectDialogFragment.this.listener != null){
                            BusSelectDialogFragment.this.listener.onSave();
                        }
                    }
                })
                .setNegativeButton(cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //we don't need to do anything here really.
                    }
                });
        return builder.create();
    }

    public interface BusSelectClosedListener{
        void onSave();
    }

}
