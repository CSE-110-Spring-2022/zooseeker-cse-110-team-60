package com.example.zooseeker;

import android.content.Context;
import android.util.Log;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DirectionTracker {

    public static Graph g;
    private static Map<String, ZooData.VertexInfo> vInfo;
    private static Map<String, ZooData.EdgeInfo> eInfo;

    public static int index;
    public static List<String> currentExhibitIdsOrder;
    private static List<String> routePlanSummary;
    public static Direction currentDirection;

    private static NodeDao dao;

    private static ArrayList<DirectionObserver> observers = new ArrayList<>();

    static void loadGraphData(Context context, String vertexPath, String edgePath, String graphPath) {
        vInfo = ZooData.loadVertexInfoJSON(context,vertexPath);
        eInfo = ZooData.loadEdgeInfoJSON(context, edgePath);
        g = ZooData.loadZooGraphJSON(context, graphPath);
    }

    /**
     * Name:     initDirections
     * Behavior: calculate the order of exhibits to be visited by starting from the user's
     *           location and going to the closest exhibit left to visit repeatedly, build up the
     *           route plan summary accordingly.
     *
     * _ @param     String      startNodeId         id of the node closest to the user
     * _ @param     List<Node>  exhibitsToVisit     the list of exhibits to be visited
     */
    static void initDirections(String startNodeId, List<Node> exhibitsToVisit) {
        index = 0;
        currentExhibitIdsOrder = new ArrayList<String>();
        routePlanSummary = new ArrayList<String>();
        String previousId = startNodeId;

        while (!exhibitsToVisit.isEmpty()) {
            Node exhibitToRemove = getNextClosestExhibitToVisit(previousId, exhibitsToVisit);
            exhibitsToVisit.remove(exhibitToRemove);
            Node nextNode = getParentNodeIfExists(exhibitToRemove);
            String nextId = nextNode.id;
            double distance = getDistanceBetweenNodes(previousId, nextId);
            currentExhibitIdsOrder.add(exhibitToRemove.id);
            routePlanSummary.add(vInfo.get(previousId).name + " to " + nextNode.name + " (" + distance + " feet)");
            previousId = nextId;
        }

        notifyOrderChange();
        notifyOrderChange();

        ZooData.VertexInfo gate = DirectionTracker.vInfo.get(DirectionTracker.getGateId());
        double distance = getDistanceBetweenNodes(previousId, gate.id);
        routePlanSummary.add(vInfo.get(previousId).name + " to " + gate.name + " (" + distance + " feet)");
    }

    /**
     * Name:     getDirection
     * Behavior: Given the user's current location, return a direction object to the next exhibit
     *           to be visited.
     *
     * @param     startNodeId     the node the user is closest to
     * @return    Direction        a direction object to the next exhibit to be visited
     */
    static Direction getDirection(String startNodeId) {
        String startNodeName = vInfo.get(startNodeId).name;
        String nextNodeId;
        String nextNodeName;
        if (index < currentExhibitIdsOrder.size()) {
            String nextExhibitId = currentExhibitIdsOrder.get(index);
            Node nextExhibit = dao.get(nextExhibitId);
            Node nextNode = getParentNodeIfExists(nextExhibit);
            nextNodeId = nextNode.id;
            nextNodeName = nextNode.name;
        } else {
            nextNodeId = getGateId();
            nextNodeName = vInfo.get(getGateId()).name;
        }

        GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.findPathBetween(g, startNodeId, nextNodeId);

        List<String> briefSteps = new ArrayList<String>();
        ArrayList<String> detailedSteps = new ArrayList<String>();

        double previousDistance = 0;
        String previousStreet = null;
        String previousSourceName = null;

        int i = 1;
        int j = 0;

        // iterate through vertices and edges at the same time
        // n nodes, n-1 edges
        // edge 1 from node 1 to node 2, edge 2 from node 2 to node 3, etc.
        // for edge x, if node x == edge x sink, swap source and sink of the edge
        // android.core.pair

        List<IdentifiedWeightedEdge> edges = path.getEdgeList();
        List<String> nodes = path.getVertexList();

        Log.d("DIR", "----------");
        Log.d("DIR|VERTS", nodes.toString());
        Log.d("DIR|EDGES", edges.toString());

        for (IdentifiedWeightedEdge e : path.getEdgeList()) {

            double currentDistance = g.getEdgeWeight(e);
            String currentStreet = eInfo.get(e.getId()).street;
            String currentSourceName = vInfo.get(g.getEdgeSource(e).toString()).name;
            String currentSinkName = vInfo.get(g.getEdgeTarget(e).toString()).name;

            if (nodes.get(i - 1).equals(g.getEdgeTarget(e))) {
                String temp = currentSourceName;
                currentSourceName = currentSinkName;
                currentSinkName = temp;
            }

            detailedSteps.add(String.format("  %d. Walk %.0f feet along %s from '%s' to '%s'.\n",
                                            i,
                                            currentDistance,
                                            currentStreet,
                                            currentSourceName,
                                            currentSinkName));

//            Log.d("DIR|", "current: "+ currentStreet + ", previous: " + previousStreet);
            if (currentStreet.equals(previousStreet)) {
//                Log.d("DIR", "SAME STREET");
                briefSteps.remove(briefSteps.size() - 1);

                currentDistance = previousDistance + currentDistance;
                currentSourceName = previousSourceName;

                briefSteps.add(String.format("  %d. Walk %.0f feet along %s from '%s' to '%s'.\n",
                        j,
                        currentDistance,
                        currentStreet,
                        currentSourceName,
                        currentSinkName));
            } else {
                briefSteps.add(String.format("  %d. Walk %.0f feet along %s from '%s' to '%s'.\n",
                        ++j,
                        currentDistance,
                        currentStreet,
                        currentSourceName,
                        currentSinkName));
            }

            previousDistance = currentDistance;
            previousStreet = currentStreet;
            previousSourceName = currentSourceName;
            i++;
        }

        currentDirection = new Direction(startNodeName, nextNodeName, briefSteps, detailedSteps, path.getWeight(), nodes);

        notifyDirectionChange();

//        Log.d("DIR", currentDirection.toString());
        return currentDirection;
    }

    /**
     * Name:     next
     * Behavior: Remove the exhibit just reached from the database storing exhibits to visit,
     *           increment index.
     */
    static void next() {
        String exhibitToRemoveId = currentExhibitIdsOrder.get(index);
        removeExhibit(exhibitToRemoveId);
        ++index;
        getDirection(GPSTracker.findNearestNode(GPSTracker.latitude, GPSTracker.longitude));
    }

    private static void removeExhibit(String exhibitToRemoveId) {
        Node exhibitToRemove = dao.get(exhibitToRemoveId);
        exhibitToRemove.added = false;
        dao.update(exhibitToRemove);
    }

    /**
     * Name:     previous
     * Behavior: Re-add the exhibit most recently reached to the database, decrement index.
     */
    static void previous() {
        String exhibitToAddId = currentExhibitIdsOrder.get(index - 1);
        addExhibit(exhibitToAddId);
        --index;
        getDirection(GPSTracker.findNearestNode(GPSTracker.latitude, GPSTracker.longitude));
    }

    private static void addExhibit(String exhibitToAddId) {
        Node exhibitToAdd = dao.get(exhibitToAddId);
        exhibitToAdd.added = true;
        dao.update(exhibitToAdd);
    }

    /**
     * Name:     getNextClosestExhibitToVisit
     * Behavior: Given the node closest to the user's location and a list of exhibits to visit,
     *           return the exhibit left to visit closest to the user.
     * @param    currentNodeId       id of the node closest to the user's location
     * @return   closest             closest exhibit to the user
     */
    static Node getNextClosestExhibitToVisit(String currentNodeId, List<Node> exhibitsToVisit) {
        String sourceId = getParentNodeIfExists(dao.get(currentNodeId)).id;
        Node closestExhibit = exhibitsToVisit.get(0);
        Node closestNode = getParentNodeIfExists(closestExhibit);
        GraphPath<String, IdentifiedWeightedEdge> shortestPath = DijkstraShortestPath.findPathBetween(g, sourceId, closestNode.id);

        for (int i = 1; i < exhibitsToVisit.size(); ++i) {
            Node currentExhibit = exhibitsToVisit.get(i);
            Node currentNode = getParentNodeIfExists(currentExhibit);
            GraphPath<String, IdentifiedWeightedEdge> currentPath = DijkstraShortestPath.findPathBetween(g,sourceId, currentNode.id);
            if (currentPath.getWeight() < shortestPath.getWeight()) {
                closestExhibit = currentExhibit;
                closestNode = currentNode;
                shortestPath = currentPath;
            }
        }

        return closestExhibit;
    }

    /**
     * Name:     redirect
     * Behavior: Given the node closest to the user's location, efficiently
     *           reorder the remaining exhibits to be visited.
     * @param    currentNodeId       id of the node closest to the user's location
     */
    static void redirect(String currentNodeId) {
//        List<String> exhibitsToBeReorderedIds = new ArrayList<String>();

        currentExhibitIdsOrder.remove(getGateId());
        Log.d("REDIRECT", "currentOrderBefore: " + currentExhibitIdsOrder.toString());

        List<Node> exhibitsToBeReordered = new ArrayList<Node>();
        for (int i = currentExhibitIdsOrder.size() - 1; i >= index; --i) {
            String currId = currentExhibitIdsOrder.get(i);
            Log.d("REDIRECT", "currId:" + currId);
//            exhibitsToBeReorderedIds.add(currId);
            exhibitsToBeReordered.add(dao.get(currId));
            currentExhibitIdsOrder.remove(currId);
        }

        String previousId = currentNodeId;

        while (!exhibitsToBeReordered.isEmpty()) {
            Node nextExhibit = getNextClosestExhibitToVisit(previousId, exhibitsToBeReordered);
            Log.d("REDIRECT", "nextExhibit: " + nextExhibit.toString());
            exhibitsToBeReordered.remove(nextExhibit);
            String nextId = nextExhibit.id;
            previousId = nextId;
            Log.d("REDIRECT", "nextId: " + nextId);
            currentExhibitIdsOrder.add(nextId);
        }

        notifyOrderChange();

        getDirection(currentNodeId);
    }

    /**
     * Name:     skip
     * Behavior: Given the node closest to the user's location, skip the next exhibit planned to be
     *           visited and reorder the remaining exhibits accordingly.
     * @param    currentNodeId       id of the node closest to the user's location
     */
    static void skip(String currentNodeId) {
        removeExhibit(currentExhibitIdsOrder.get(index));
        currentExhibitIdsOrder.remove(index);
        redirect(currentNodeId);
    }

    /**
     * Name:     getParentNodeIfExists
     * Behavior: Given a node, return that node's parent if it exists, otherwise return the
     *           original node.
     * - @param    Node                             node
     * - @return   node/dao.get(node.parentId)      the given node, or its parent if it exists
     */
    static Node getParentNodeIfExists(Node node) {
        if (node.parentId.equals("")) return node;
        else {
            return dao.get(node.parentId);
        }
    }

    static double getDistanceBetweenNodes(String startNodeId, String endNodeId) {
        String sourceId = getParentNodeIfExists(dao.get(startNodeId)).id;
        String sinkId = getParentNodeIfExists(dao.get(endNodeId)).id;
        return DijkstraShortestPath.findPathBetween(g, sourceId, sinkId).getWeight();
    }

    static String getGateId() {
        for (ZooData.VertexInfo vertex : vInfo.values()) {
            if (vertex.kind == ZooData.VertexInfo.Kind.GATE) return vertex.id;
        }
        return "null_no_gate";
    }

    static void loadDatabaseAndDaoByContext(Context context) {
        NodeDatabase db = NodeDatabase.getSingleton(context);
        dao = db.nodeDao();
    }

    static void setDao(NodeDao dataAccessObject) { dao = dataAccessObject; }

    static NodeDao getDao() { return dao; }

    static List<String> getRoutePlanSummary() { return routePlanSummary; }

    static Node getCurrentExhibit() { return dao.get(currentExhibitIdsOrder.get(index)); }

    public static void register(DirectionObserver directionObserver) {
        observers.add(directionObserver);
    }

    public static void notifyOrderChange() {
        for (DirectionObserver observer : observers) {

Log.d("MOCK notify of order change", "***");

            observer.updateOrder(currentExhibitIdsOrder);
        }
    }

    public static void notifyDirectionChange() {
        for (DirectionObserver observer : observers) {

Log.d("MOCK notify of direction change", "***");

            observer.updateDirection(currentDirection);
        }
    }
}