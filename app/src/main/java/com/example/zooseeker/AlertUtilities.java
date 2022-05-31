package com.example.zooseeker;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Source: https://stackoverflow.com/questions/52925790/alert-dialog-button-pressed
 * -returning-0-values-always
 */
public class AlertUtilities {
    private final AlertDialog.Builder builder;
    private final AlertDialogListener listener;

    interface AlertDialogListener {
        void onClick(boolean response);
    }

    public AlertUtilities(Context context, AlertDialogListener listener) {
        builder = new AlertDialog.Builder(context);
        this.listener = listener;
    }

    public void showAlert(String message, String positiveButtonMsg,
                          String negativeButtonMsg) {
        builder.setMessage(message);
        builder.setPositiveButton(positiveButtonMsg, (dialogInterface, i) -> {
            listener.onClick(true);
            dialogInterface.dismiss();
        });
        builder.setNegativeButton(negativeButtonMsg, (dialogInterface, i) -> {
            listener.onClick(false);
            dialogInterface.dismiss();
        });
        builder.show();
    }
}