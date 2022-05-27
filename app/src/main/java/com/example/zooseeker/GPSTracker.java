package com.example.zooseeker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.util.List;

public class GPSTracker implements LocationListener {

    private Context context;
    private Activity activity;

    public boolean isGPSEnabled = false;

    public Location location;
    public double latitude;
    public double longitude;

    private boolean rejected = false;
    private String nextExhibit = "";

    private static final String provider = LocationManager.GPS_PROVIDER;
    private static final long MIN_TIME_BW_UPDATES = 0;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1f;

    public LocationManager locationManager;

    public GPSTracker(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
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
            locationManager.requestLocationUpdates(provider, MIN_TIME_BW_UPDATES,
                                                   MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

            if (locationManager != null) {
                Location lastKnownLocation =
                        locationManager.getLastKnownLocation(provider);
                if (lastKnownLocation != null) {
                    location = lastKnownLocation;
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
        }
        return location;
    }

    /**
     * Name:     onLocationChanged
     * Behavior: called each time the user moves 10 meters and checks if the user is off track
     *           it prompts them to redirect if they are off track and the order of remaining nodes has changed
     *           if they decline, then they arent prompted if they go off track once again between the same 2 exhibits
     *           if they approve, then we delegate to redirect
     *
     * _ @param     Location      location        the user's current location
     */
    @Override
    public void onLocationChanged(@NonNull Location location) {
        List<String> nodes = DirectionTracker.currentExhibitIdsOrder;
        String next = DirectionTracker.currentExhibitIdsOrder.get(0); // why not just say exhibits.get(0)??
        double closestDistance = Integer.MAX_VALUE;
        String closestExhibit = next;

        if (rejected && !nextExhibit.equals(next)) {
            rejected = false;
        }

        //goes through all the exhibits that are left to visit and checks which one is the closest
//        {
//            for (String node : nodes) {
//                double distance = Utilities.getVincentyDistance(latitude, longitude, .latitude,
//                item.longitude);
//                if (distance < closestDistance) {
//                    closestDistance = distance;
//                    closestExhibit = item.id;
//                }
//            }
//            /* Scenario 1: if the closest exhibit is no longer what was expected */
//            if (!closestExhibit.equals(next)) {
//                // ask the user if they want new directions
//                boolean redirect = Utilities.showAlert(activity, "You are off track! " +
//                        "Do you want to re-plan your directions?", "Yes", "No");
//                if (redirect) {
//                    DirectionTracker.redirect(findNearestNode(latitude, longitude));
//                }
//                else {
//                    // do nothing until next exhibit
//                    rejected = true;
//                    nextExhibit = next;
//                }
//            }
//            /* Scenario 2 */
//            else {
//                DirectionTracker.redirect(findNearestNode(latitude, longitude));
//            }
//        }
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    /**
     * @param latitude : represents user's current latitude
     * @param longitude : represents user's current longitude
     * @return the name of the nearest node (this includes gates, intersections, exhibits, groups, etc..)
     */
    private String findNearestNode(double latitude, double longitude) {
        List<Node> nodes = ExhibitList.getAllNodes();
        double closestDistance = Integer.MAX_VALUE;
        String closestNode = "";

        //goes through all nodes and calculates distance from user's current location(parameters)
        //to all the other nodes using the vincenty formula
        for (Node node : nodes) {
            double distance = Utilities.getVincentyDistance(latitude, longitude,
                                                            node.latitude,
                                                            node.longitude);
            //if the distance is less than the minimum so far
            if (distance < closestDistance) {
                //update the minimum distance
                closestDistance = distance;
                closestNode = node.id;
            }
        }

        return closestNode;
    }
}

