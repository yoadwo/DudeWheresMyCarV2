package com.gingos.dudewheresmycar.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.gingos.dudewheresmycar.R;


// with https://developer.android.com/guide/topics/ui/dialogs
// with https://www.youtube.com/watch?v=miUPzUmaENo (to change onattach:: targetFragment()

public class ConfirmationDialogFragment extends DialogFragment {

    private static final String TAG = "DUDE_ConfirmationDialog";
    private static final String ARG_TITLE = "title";
    private static final String ARG_DEFAULT_TITLE = "<Title>";
    private static final String ARG_MESSAGE = "message";
    private static final String ARG_DEFAULT_MESSAGE = "<Message>?";

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface ConfirmationDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    public static ConfirmationDialogFragment newInstance(String title, String message) {

        ConfirmationDialogFragment frag = new ConfirmationDialogFragment();

        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        frag.setArguments(args);

        return frag;
    }

    // Use this instance of the interface to deliver action events
    ConfirmationDialogListener _listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String title = getArguments().getString(ARG_TITLE, ARG_DEFAULT_TITLE);
        String message = getArguments().getString(ARG_MESSAGE, ARG_DEFAULT_MESSAGE);


        builder.setMessage(message)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "onClick: " + "click OK");
                        _listener.onDialogPositiveClick(ConfirmationDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "onClick: " + "click Cancel");
                        _listener.onDialogNegativeClick(ConfirmationDialogFragment.this);
                    }
                })
                .setTitle(title);

        // Create the AlertDialog object and return it
        return builder.create();
    }


    // may not work, tutorial used onAttach(Activity activity) which is deprecated
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            _listener = (ConfirmationDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            Log.e(TAG, "onAttach: ClassCastException, must implement ConfirmationDialogListener:" +e.getMessage() );
        }

    }
}