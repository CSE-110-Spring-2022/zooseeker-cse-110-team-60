package com.example.zooseeker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GPSTracker implements LocationListener, DirectionObserver {

    private Context context;
    private Activity activity;
    private static NodeDao dao;

    public boolean isGPSEnabled = false;

    public Location location;
    public double latitude;
    public double longitude;

    private boolean rejected = false;
    private String nextExhibit = "";
    private double radius;
    private List<Node> nodes;

    private static final String provider = LocationManager.GPS_PROVIDER;
    private static final long MIN_TIME_BW_UPDATES = 0;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1f;

    public LocationManager locationManager;

    private static List<String> exhibitIdsToVisit = DirectionTracker.currentExhibitIdsOrder;
    private static List<String> nodeIdsToPass = new ArrayList<>();
    private static List<Node> exhibitsToVisit = new ArrayList<>();
    private static List<Node> nodesToPass = new ArrayList<>();

    public GPSTracker(Context context, Activity activity, DirectionSubject subject) {
        this.context = context;
        this.activity = activity;
        dao = DirectionTracker.getDao();
        getLocation();
        radius = findSmallestDistance() * 0.5;
        nodes = dao.getAll();

        subject.register(this);
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
     * @param     location        the user's current location
     */
    @Override
    public void onLocationChanged(@NonNull Location location) {
        Node nextExhibitToVisit;
        if (exhibitsToVisit.size() > 0) {
            nextExhibitToVisit = exhibitsToVisit.get(0);
        }
        // finished all exhibits, heading back to gate
        else {
            nextExhibitToVisit = dao.get("entrance_exit_gate");
        }
        double closestDistance = Integer.MAX_VALUE;
        String closestExhibit = nextExhibitToVisit.id;

        if (rejected && !nextExhibit.equals(nextExhibitToVisit.id)) {
            rejected = false;
        }

        {
            for (Node exhibit : exhibitsToVisit) {
                double distance = Utilities.getVincentyDistance(latitude, longitude, exhibit.latitude, exhibit.longitude);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestExhibit = exhibit.id;
                }
            }
            /* Scenario 1 */
            if (!closestExhibit.equals(nextExhibitToVisit.id)) {
                // prompt
                boolean redirect = Utilities.showAlert(activity, "You are off track! Do" + " you want to re-plan " + "your directions?", "Yes", "No");
                if (redirect) {
                    DirectionTracker.redirect(findNearestNode(latitude, longitude));
                }
                else {
                    // do nothing until next exhibit
                    rejected = true;
                    nextExhibit = nextExhibitToVisit.id;
                }
            }
            /* Scenario 2 */
            else {
                for (Node node : nodes) {
                    // offTrack
                    if (Utilities.getVincentyDistance(latitude, longitude, node.latitude, node.longitude) < radius && !(nodesToPass.get(0) == node)) {
                        DirectionTracker.redirect(node.id);
                    }
                    // node passed
                    else if (Utilities.getVincentyDistance(latitude, longitude, node.latitude, node.longitude) < radius && (nodesToPass.get(0) == node)) {
                        nodesToPass.remove(0);
                    }

                    // exhibit visited
                    if (Utilities.getVincentyDistance(latitude, longitude, node.latitude, node.longitude) < radius && (exhibitsToVisit.get(0) == node)) {
                        exhibitsToVisit.remove(0);
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
     * @param latitude : represents user's current latitude
     * @param longitude : represents user's current longitude
     * @return the name of the nearest node (this includes gates, intersections, exhibits, groups, etc..)
     */
    private String findNearestNode(double latitude, double longitude) {
        List<Node> nodes = ExhibitList.getAllNodes();
        double closestDistance = Integer.MAX_VALUE;
        String closestNodeId = "";

        // goes through all nodes and calculates distance from user's current location(parameters)
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

        return closestNodeId;
    }

    private double findSmallestDistance() {
            Set<IdentifiedWeightedEdge> edges = DirectionTracker.g.edgeSet();
            double closestDistance = Integer.MAX_VALUE;

            for (IdentifiedWeightedEdge edge : edges) {
                String sourceId = (String) DirectionTracker.g.getEdgeSource(edge);
Log.d("EDGES source id", sourceId);

                String targetId = (String) DirectionTracker.g.getEdgeTarget(edge);
Log.d("EDGES target id", targetId);

                Node source = dao.get(sourceId);
Log.d("EDGES source node", source.toString());

                Node target = dao.get(targetId);
Log.d("EDGES target node", target.toString());

                double distance = Utilities.getVincentyDistance(source.latitude, source.longitude, target.latitude, target.longitude);
Log.d("EDGES distance", String.valueOf(distance));

                if (distance < closestDistance) {
                    closestDistance = distance;
                }
            }
            return closestDistance;
        }

    @Override
    public void updateDirection(Direction direction) {
        nodeIdsToPass = direction.nodeIds;
        nodesToPass = getNodes(nodeIdsToPass);
    }

    @Override
    public void updateOrder(List<String> exhibitIds) {
        exhibitIdsToVisit = exhibitIds;
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
}