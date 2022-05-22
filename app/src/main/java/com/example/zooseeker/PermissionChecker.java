package com.example.zooseeker;

import android.Manifest;
import android.content.DialogInterface;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;

public class PermissionChecker {
    private ComponentActivity activity;
    final ActivityResultLauncher<String[]> requestPermissionLauncher;

    /**
     * Requests permissions through pop-ups.
     * Note that Android 11 and above will stop showing pop-ups
     * after user taps Deny twice for a specific permission during app's lifetime of installation on a device.
     *
     * If precise location access granted, recreate activity for change to take effect.
     * Else, an alert for enabling precise location is shown, and app quites after.
     *
     * @param activity  activity
     */
    public PermissionChecker(ComponentActivity activity) {
        this.activity = activity;

        requestPermissionLauncher =
                activity.registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            Boolean fineLocationGranted =
                    result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);

             // precise location access granted
            if (fineLocationGranted != null && fineLocationGranted) {
                activity.recreate();
                MainActivity main = MainActivity.getInstance();
                main.gpsTracker = new GPSTracker(activity);
            }
            // only approximate location access granted or no access granted
            else {
                DialogInterface.OnClickListener dialog = (dialogInterface, i) -> {
                    if (i == DialogInterface.BUTTON_POSITIVE) {
                        activity.finish(); // TODO: downgrade to not use location
                        System.exit(0);
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Please go to System Settings to enable Precise Location for ZooSeeker.")
                       .setPositiveButton("Ok", dialog).show();  // TODO: check what happens if clicking "only allow this time"
            }
        });
    }
}
