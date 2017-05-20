package com.noqapp.android.client.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.noqapp.android.client.R;


public class ShowAlertInformation {

    public static void showDialog(Context context, String Title, String Message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(Title);
        alertDialog.setMessage(Message);
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setNegativeButton(context.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    public static void showNetworkDialog(Context context) {
        showDialog(context, context.getString(R.string.networkerror), context.getString(R.string.offline));
    }
}
