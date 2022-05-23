package com.example.zooseeker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import java.util.concurrent.atomic.AtomicBoolean;

public class Utilities {
    public static boolean showAlert(Activity activity, String message,
                                    String positiveButtonMsg, String negativeButtonMsg) {
        AtomicBoolean toReturn = new AtomicBoolean(false);
        DialogInterface.OnClickListener dialog = (dialogInterface, i) -> {
            switch (i) {
                // "Yes" or "Ok" button clicked
                case DialogInterface.BUTTON_POSITIVE:
                    toReturn.set(true);
                    break;

                // "No" or "Cancel" button clicked
                case DialogInterface.BUTTON_NEGATIVE:
                    toReturn.set(false);
                    break;
            }
        };

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);

        alertBuilder.setMessage(message).setPositiveButton(positiveButtonMsg, dialog)
                    .setNegativeButton(negativeButtonMsg, dialog).show();

        return toReturn.get();
    }

    public static double dist(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75; // miles (or 6371.0 kilometers)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a =
                Math.pow(sindLat, 2) + Math.pow(sindLng, 2) * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c;
    }
}
