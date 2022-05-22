package com.example.zooseeker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;

public class GPSTracker implements LocationListener {

    private Context context;
    public boolean isGPSEnabled = false;
//    public boolean canGetLocation = false;

    public Location location;
    public double latitude;
    public double longitude;

    private static final String provider = LocationManager.GPS_PROVIDER;
    private static final long MIN_TIME_BW_UPDATES = 0;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1f;

    public LocationManager locationManager;

    public GPSTracker(Context context) {
        this.context = context;
        getLocation();
    }

    @SuppressLint("MissingPermission")
    public Location getLocation() {
        locationManager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(provider);

        if (!isGPSEnabled) {
            // gpsTracker only called if Precise Location granted
            // TODO: downgrade
        }
        else {
//            canGetLocation = true; //TODO
            locationManager.requestLocationUpdates(provider, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

            if (locationManager != null) {
                Location lastKnownLocation =
                        locationManager.getLastKnownLocation(provider);
                if (lastKnownLocation != null) {
                    location = lastKnownLocation;
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
//                    Log.d("LOCATION",
//                          location.getLatitude() + " " + location.getLongitude());
                }
            }
        }
        return location;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        // TODO: OFFTRACK
//        Log.d("LOCATION",
//              location.getLatitude() + " " + location.getLongitude());
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }
}

