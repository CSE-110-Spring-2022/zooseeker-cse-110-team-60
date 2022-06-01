package com.example.zooseeker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressLint("StaticFieldLeak")
public class GPSTracker implements LocationListener, DirectionObserver {

    private static Context context;
    private static Activity activity;
    private static NodeDao dao;

    public static boolean isGPSEnabled = false;
    public static boolean manualLocation = true;

    public static Location location;
    public static double latitude;
    public static double longitude;

    private static boolean rejected = false;
    private static String nextExhibit = "";
    private static double radius;
    private static List<Node> nodes;

    private static final String provider = LocationManager.GPS_PROVIDER;
    private static final long MIN_TIME_BW_UPDATES = 0;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1f;

    public static LocationManager locationManager;

    private static List<String> exhibitIdsToVisit =
            DirectionTracker.currentExhibitIdsOrder;
    private static List<String> nodeIdsToPass = new ArrayList<>();
    private static List<Node> exhibitsToVisit = new ArrayList<>();
    private static List<Node> nodesToPass = new ArrayList<>();

    private static int exhibitIndex = 0;
//    private static int currIndex = 0;

    public GPSTracker(Context context, Activity activity) {
        GPSTracker.context = context;
        GPSTracker.activity = activity;
        dao = DirectionTracker.getDao();
        radius = findSmallestDistance() * 0.5;

Log.d("MOCK radius = ", String.valueOf(radius));

        nodes = ExhibitList.getAllNodes();
        DirectionTracker.register(this);

        getLocation();
    }

    /**
     * Source: https://stackoverflow.com/questions/17983865/making-a-location-object-in-android-with-latitude-and-longitude-values
     * @return Location
     */
    @SuppressLint("MissingPermission")
    public Location getLocation() {
        if (manualLocation) {
            Location manualLocation = new Location(LocationManager.GPS_PROVIDER);
            manualLocation.setLatitude(latitude);
            manualLocation.setLongitude(longitude);

            return manualLocation;
        }
        else {
            locationManager =
                    (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(provider);

            if (isGPSEnabled) {
                locationManager.requestLocationUpdates(provider, MIN_TIME_BW_UPDATES,
                                                       MIN_DISTANCE_CHANGE_FOR_UPDATES,
                                                       this);

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
    }

    /**
     * Name:     onLocationChanged
     * Behavior: called each time the user moves 10 meters and checks if the user is off
     * track
     * it prompts them to redirect if they are off track and the order of remaining nodes
     * has changed
     * if they decline, then they arent prompted if they go off track once again between
     * the same 2 exhibits
     * if they approve, then we delegate to redirect
     *
     * @param location the user's current location
     */
    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        offTrack();
    }

    public void offTrack() {

Log.d("MOCK offtrack method entered", "***");

Log.d("MOCK rejected = ", String.valueOf(rejected));
Log.d("MOCK nextExhibit = ", nextExhibit);
Log.d("MOCK exhibitIndex = ", String.valueOf(exhibitIndex));

        Node nextExhibitToVisit;
        if (exhibitIndex < exhibitsToVisit.size()) {
            nextExhibitToVisit = exhibitsToVisit.get(exhibitIndex);
        }
        else {
            return;
        }

        double closestDistance = Integer.MAX_VALUE;
        String closestExhibit = nextExhibitToVisit.id;

        if (rejected && !nextExhibit.equals(nextExhibitToVisit.id)) {

            rejected = false;
Log.d("MOCK rejected changed to false, moving on to next exhibit", "***");
        }

        {
            for (int iterator = exhibitIndex; iterator < exhibitsToVisit.size(); iterator++) {

                double distance = Utilities.getVincentyDistance(latitude, longitude,
                                                                exhibitsToVisit.get(iterator).latitude, exhibitsToVisit.get(iterator).longitude);

Log.d("MOCK calculating distance", "distance between current location lat=" + latitude + ", lng= " + longitude + "and " + exhibitsToVisit.get(iterator).id + ": lat=" + exhibitsToVisit.get(iterator).latitude + ", lng=" + exhibitsToVisit.get(iterator).longitude + ", distance " + distance);

                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestExhibit = exhibitsToVisit.get(iterator).id;
                }
            }

Log.d("MOCK closestExhibit = ", closestExhibit);

            /* Scenario 1 */
            if (!closestExhibit.equals(nextExhibitToVisit.id)) {

Log.d("MOCK scenario 1 entered", "***");

                // prompt
                AlertUtilities alert = new AlertUtilities(context, response -> {
                    if (response) {

Log.d("MOCK prompted, user said yes, now find nearest node and redirect", "***");
Log.d("MOCK nearest node is ", findNearestNode(latitude, longitude));

                        DirectionTracker.redirect(findNearestNode(latitude, longitude));

Log.d("MOCK redirected", "***");

                    }
                    else {

Log.d("MOCK prompted, user said no, wait til next exhibit to prompt", "***");

                        // do nothing until next exhibit
                        DirectionTracker.getDirection(findNearestNode(latitude, longitude));
                        rejected = true;
                        nextExhibit = nextExhibitToVisit.id;
//                        currIndex = exhibitIndex;

Log.d("MOCK rejected changed to true", "***");
Log.d("MOCK nextExhibit changed to ", nextExhibit);

                    }
                });
                alert.showAlert("You are off track! Do" + " you want to re-plan " +
                                "your directions?", "Yes", "No");
            }
            /* Scenario 2 */
            else {

Log.d("MOCK scenario 2 entered", "***");

                for (Node node : nodes) {

Log.d("MOCK nodesToPass", nodesToPass.toString());
                    // offTrack
                    if (Utilities.getVincentyDistance(latitude, longitude,
                                                      node.latitude, node.longitude) < radius && !(nodesToPass.get(0) == node)) {

Log.d("MOCK passing node not on route, redirect" , "*** passing " + node.id);
Log.d("MOCK nodes to pass", nodesToPass.toString());

                        DirectionTracker.redirect(node.id);

Log.d("MOCK closest node", node.id);
Log.d("MOCK nodes to pass", nodesToPass.toString());

Log.d("MOCK passed wrong node, has been redirected", "***");

                    }
                    // node passed
Log.d("MOCK distance=", String.valueOf(Utilities.getVincentyDistance(latitude, longitude,
                                                                     node.latitude,
                                                                     node.longitude)));

Log.d("MOCK radius=", String.valueOf(radius));
Log.d("MOCK node=", node.id);
Log.d("MOCK nodesToPass.get(0)", nodesToPass.get(0).toString());
Log.d("MOCK node", node.toString());

                    double distance = (Utilities.getVincentyDistance(latitude, longitude,
                                                              node.latitude,
                                                              node.longitude));

                    if ((distance < radius) && (nodesToPass.get(0).id.equals(node.id))) {
Log.d("MOCK IF STATEMENT ENTERED", "***");
Log.d("MOCK distance current location to ", node.id  + ", distance=" + String.valueOf(Utilities.getVincentyDistance(latitude, longitude,
                                                                       node.latitude,
                                                                       node.longitude)));
Log.d("MOCK passing planned node", "***");

                        nodesToPass.remove(0);

Log.d("MOCK passed node, remove from the nodesToPass list", "***");
                    }

                    // exhibit visited
                    if (Utilities.getVincentyDistance(latitude, longitude,
                                                      node.latitude, node.longitude) < radius && (exhibitsToVisit.get(exhibitIndex).id.equals(node.id))) {

                        Log.d("MOCK mark exhibit as visited", "***");

                        exhibitIndex++;

                        if (exhibitIndex < exhibitsToVisit.size()) {

                            nextExhibit = nextExhibitToVisit.id;

                            Log.d("MOCK nextExhibit: ", nextExhibit);

                        }
                    }
                }
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    /**
     * @param latitude  : represents user's current latitude
     * @param longitude : represents user's current longitude
     *
     * @return the name of the nearest node (this includes gates, intersections, exhibits,
     * groups, etc..)
     */
    public static String findNearestNode(double latitude, double longitude) {

Log.d("MOCK findNearestNode", "***");

        List<Node> nodes = ExhibitList.getAllNodes();
        double closestDistance = Integer.MAX_VALUE;
        String closestNodeId = "";

        // goes through all nodes and calculates distance from user's current location
        // (parameters)
        // to all the other nodes using the vincenty formula
        for (Node node : nodes) {
            double distance = Utilities.getVincentyDistance(latitude, longitude,
                                                            node.latitude,
                                                            node.longitude);

            // if the distance is less than the minimum so far
            if (distance < closestDistance) {
                // update the minimum distance
                closestDistance = distance;
                closestNodeId = node.id;
            }
        }

Log.d("MOCK closestNodeId = ", closestNodeId);

        return closestNodeId;
    }

    private double findSmallestDistance() {

Log.d("MOCK findSmallestDistance entered", "***");

        Set<IdentifiedWeightedEdge> edges = DirectionTracker.g.edgeSet();
        double closestDistance = Integer.MAX_VALUE;

        for (IdentifiedWeightedEdge edge : edges) {
            String sourceId = (String) DirectionTracker.g.getEdgeSource(edge);

            String targetId = (String) DirectionTracker.g.getEdgeTarget(edge);

            Node source = dao.get(sourceId);

            Node target = dao.get(targetId);

            double distance = Utilities.getVincentyDistance(source.latitude,
                                                            source.longitude,
                                                            target.latitude,
                                                            target.longitude);

            if (distance < closestDistance) {
                closestDistance = distance;
            }
        }
        return closestDistance;
    }

    @Override
    public void updateDirection(Direction direction) {
        nodeIdsToPass = direction.nodeIds;

for (String id : nodeIdsToPass) {
    Log.d("MOCK nodeIdsToPass updated", id);
}

        nodesToPass = getNodes(nodeIdsToPass);
    }

    @Override
    public void updateOrder(List<String> exhibitIds) {

for (String id : exhibitIds) {
    Log.d("MOCK exhibitIds updated", id);
}

        exhibitIdsToVisit = exhibitIds;

        if (!exhibitIdsToVisit.get(exhibitIdsToVisit.size() - 1)
                              .equals("entrance_exit_gate")) {
            exhibitIdsToVisit.add("entrance_exit_gate");
        }

        if (nextExhibit.equals("")) {
            nextExhibit = exhibitIdsToVisit.get(0);

Log.d("MOCK first run, nextExhibit is set", nextExhibit);
        }
        else {
            nextExhibit = exhibitIdsToVisit.get(exhibitIndex);
        }

        exhibitsToVisit = getNodes(exhibitIdsToVisit);
    }

    private List<Node> getNodes(List<String> ids) {
        List<Node> nodes = new ArrayList<>();

        for (String id : ids) {
            Node node = dao.get(id);
            nodes.add(node);
        }
        return nodes;
    }

    public void updateContext(Context context) {
        GPSTracker.context = context;
    }
}