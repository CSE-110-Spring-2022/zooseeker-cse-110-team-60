package com.example.zooseeker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Source: https://stackoverflow
 * .com/questions/52925790/alert-dialog-button-pressed-returning-0-values-always
 *
 * Utilities class for alerts. Enables to get response of the user and reacts
 * accordingly.
 */
public class AlertUtilities {
    private AlertDialog.Builder builder;
    private AlertDialogListener listener;

    /**
     * Interface to record user's response.
     */
    interface AlertDialogListener {
        void onClick(boolean response);
    }

    /**
     * AlertUtilities constructor.
     *
     * @param context  Context.
     * @param listener AlertDialogListener.
     */
    public AlertUtilities(Context context, AlertDialogListener listener) {
        builder = new AlertDialog.Builder(context);
        this.listener = listener;
    }

    /**
     * Displays alert with chosen message and buttons.
     *
     * @param message           Message to be displayed.
     * @param positiveButtonMsg Message for positive button.
     * @param negativeButtonMsg Message for negative button.
     */
    public void showAlert(String message, String positiveButtonMsg, String negativeButtonMsg) {
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
