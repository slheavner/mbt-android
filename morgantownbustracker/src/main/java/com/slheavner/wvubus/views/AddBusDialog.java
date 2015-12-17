package com.slheavner.wvubus.views;

import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import com.slheavner.wvubus.R;
import com.slheavner.wvubus.controllers.PrefsController;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddBusDialog.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddBusDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddBusDialog extends DialogFragment {

    boolean[] checks;
    PrefsController prefs;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        prefs = new PrefsController(getActivity());
        checks = prefs.getBusEnabledBool();
        builder = builder.setTitle("Bus Select")
                .setMultiChoiceItems(R.array.bus_full, prefs.getBusEnabledBool(), new DialogInterface.OnMultiChoiceClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checks[which] = isChecked;
                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        prefs.setBusEnabled(checks);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
