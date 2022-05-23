package com.example.zooseeker;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DirectionTracker {

    private static Graph g;
    private static Map<String, ZooData.VertexInfo> vInfo;
    private static Map<String, ZooData.EdgeInfo> eInfo;

    public static int index;
    public static List<ExhibitItem> currentExhibitsOrder;
    private static List<String> routePlanSummary;

    private static ExhibitItemDao dao;

    static void loadGraphData(Context context, String vertexPath, String edgePath, String graphPath) {
        vInfo = ZooData.loadVertexInfoJSON(context,vertexPath);
        eInfo = ZooData.loadEdgeInfoJSON(context, edgePath);
        g = ZooData.loadZooGraphJSON(context, graphPath);
    }

    /**
     * Name:     initDirections
     * Behavior: recalculate the order of exhibits to be visited by starting from the user's
     *           location and going to the closest exhibit left to visit repeatedly, build up the
     *           route plan summary accordingly.
     *
     * @param     String             startNodeId         id of the node closest to the user
     * @param     List<ExhibitItem>  exhibitsToVisit     the list of exhibits to be visited
     */
    static void initDirections(String startNodeId, List<ExhibitItem> exhibitsToVisit) {
        index = 0;
        currentExhibitsOrder = new ArrayList<ExhibitItem>();
        String previousId = startNodeId;
        routePlanSummary = new ArrayList<String>();

        while (!exhibitsToVisit.isEmpty()) {
            ExhibitItem nextExhibit = getNextClosestExhibitToVisit(previousId, exhibitsToVisit);
            exhibitsToVisit.remove(nextExhibit);
            String nextId = nextExhibit.id;
            double distance = getDistanceBetweenNodes(previousId, nextId);
            currentExhibitsOrder.add(nextExhibit);
            routePlanSummary.add(vInfo.get(previousId).name + " to " + nextExhibit.name + " (" + distance + "m)");
            previousId = nextId;
        }

        ZooData.VertexInfo gate = DirectionTracker.vInfo.get(DirectionTracker.getGateId());
        double distance = getDistanceBetweenNodes(previousId, gate.id);
        routePlanSummary.add(vInfo.get(previousId).name + " to " + gate.name + " (" + distance + "m)");
    }

    static List<String> getRoutePlanSummary() { return routePlanSummary; }

    /**
     * Name:     getDirection
     * Behavior: Given the user's current location, return a direction object to the next exhibit
     *           to be visited.
     *
     * @param     String     startNodeId     the node the user is closest to
     * @return    Direction                  a direction object to the next exhibit to be visited
     */
    static Direction getDirection(String startNodeId) {
        String startNodeName = vInfo.get(startNodeId).name;
        String nextNodeId;
        String nextNodeName;
        if (index < currentExhibitsOrder.size()) {
            nextNodeId = currentExhibitsOrder.get(index).id;
            nextNodeName = currentExhibitsOrder.get(index).name;
        } else {
            nextNodeId = getGateId();
            nextNodeName = vInfo.get(getGateId()).name;
        }

        GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.findPathBetween(g, startNodeId, nextNodeId);

        ArrayList<String> detailedSteps = new ArrayList<String>();
        int i = 1;
        for (IdentifiedWeightedEdge e : path.getEdgeList()) {
            detailedSteps.add(String.format("  %d. Walk %.0f meters along %s from '%s' to '%s'.\n",
                    i,
                    g.getEdgeWeight(e),
                    eInfo.get(e.getId()).street,
                    vInfo.get(g.getEdgeSource(e).toString()).name,
                    vInfo.get(g.getEdgeTarget(e).toString()).name));
            i++;
        }

//        List<String> briefSteps

        return new Direction(startNodeName, nextNodeName, detailedSteps, path.getWeight());
    }

    /**
     * Name:     next
     * Behavior: Remove the exhibit just reached from the database storing exhibits to visit,
     *           increment index.
     */
    static void next() {
        ExhibitItem exhibitToRemove = currentExhibitsOrder.get(index);
        exhibitToRemove.added = false;
        dao.update(exhibitToRemove);
        ExhibitItem exhibitRemoved = dao.get(exhibitToRemove.id);
        ++index;
    }

    /**
     * Name:     previous
     * Behavior: Re-add teh exhibit most recently reached to the database, Decrement index.
     *           exhibits to visi.
     */
    static void previous() {
        ExhibitItem exhibitToAdd = currentExhibitsOrder.get(index - 1);
        exhibitToAdd.added = true;
        dao.update(exhibitToAdd);
        ExhibitItem exhibitAdded = dao.get(exhibitToAdd.id);
        --index;
    }

    /**
     * Name:     getNextClosestExhibitToVisit
     * Behavior: Given the node closest to the user's location and a list of exhibits to visit,
     *           return the exhibit left to visit closest to the user.
     * @param    String         currentNodeId       id of the node closest to the user's location
     * @return   ExhibitItem    closest             closest exhibit to the user
     */
    static ExhibitItem getNextClosestExhibitToVisit(String currentNodeId, List<ExhibitItem> exhibitsToVisit) {
        ExhibitItem closest = exhibitsToVisit.get(0);
        GraphPath<String, IdentifiedWeightedEdge> shortestPath = DijkstraShortestPath.findPathBetween(g, currentNodeId, exhibitsToVisit.get(0).id);

        for (int i = 1; i < exhibitsToVisit.size(); ++i) {
            ExhibitItem currentExhibit = exhibitsToVisit.get(i);
            GraphPath<String, IdentifiedWeightedEdge> currentPath = DijkstraShortestPath.findPathBetween(g,currentNodeId, exhibitsToVisit.get(i).id);
            if (currentPath.getWeight() < shortestPath.getWeight()) {
                closest = currentExhibit;
                shortestPath = currentPath;
            }
        }

        return closest;
    }

    static double getDistanceBetweenNodes(String startId, String endId) {
        return DijkstraShortestPath.findPathBetween(g, startId, endId).getWeight();
    }

    static String getGateId() {
        for (ZooData.VertexInfo vertex : vInfo.values()) {
            if (vertex.kind == ZooData.VertexInfo.Kind.GATE) return vertex.id;
        }
        return "null_no_gate";
    }

    static void loadDatabaseAndDaoByContext(Context context) {
        ExhibitDatabase db = ExhibitDatabase.getSingleton(context);
        dao = db.exhibitItemDao();
    }

    static void setDao(ExhibitItemDao dataAccessObject) { dao = dataAccessObject; }

    static ExhibitItemDao getDao() { return dao; }
}