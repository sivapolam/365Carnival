package com.techplicit.mycarnival.ui.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by pnaganjane001 on 14/11/15.
 */
public class WhereAreMyFriendsPrivacyFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Enabled Privacy");
        builder.setMessage("Please Enable privacy for the access. \n");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
                dismiss();
            }
        });

        return builder.create();
    }
}
