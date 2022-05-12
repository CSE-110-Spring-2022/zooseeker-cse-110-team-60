package com.example.zooseeker;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DirectionTracker {

    Graph g;
    Map<String, ZooData.VertexInfo> vInfo;
    Map<String, ZooData.EdgeInfo> eInfo;

    public static List<Direction> directions;

    public DirectionTracker(Graph g, Map<String, ZooData.VertexInfo> vInfo, Map<String, ZooData.EdgeInfo> eInfo) {
        this.g = g;
        this.vInfo = vInfo;
        this.eInfo = eInfo;
    }

    /*
     *   Name:       getDirections
     *   Behavior:   Given a list of ExhibitItems to visit, calculate a list of directions describing
     *               an efficient route from the gate to the desired exhibits and back, set the
     *               directions field of the tracker to reflect the updated directions.
     *   @param      List<ExhibitItem>  exhibitsToVisit
     *   @return
     */
    public void getDirections(List<ExhibitItem> exhibitsToVisit) {
        ArrayList<Direction> directions = new ArrayList<Direction>();
        ExhibitItem gate = new ExhibitItem("entrance_exit_gate", VertexInfo.Kind.EXHIBIT, "Gate", "");

        ExhibitItem previous = gate;

        while (!exhibitsToVisit.isEmpty()) {
            GraphPath<String, IdentifiedWeightedEdge> shortestPath = DijkstraShortestPath.findPathBetween(g, previous.id, exhibitsToVisit.get(0).id);
            ExhibitItem nextExhibit = exhibitsToVisit.get(0);

            for (int i = 1; i < exhibitsToVisit.size(); i++) {
                GraphPath<String, IdentifiedWeightedEdge> currentPath = DijkstraShortestPath.findPathBetween(g, previous.id, exhibitsToVisit.get(i).id);
                if (currentPath.getWeight() < shortestPath.getWeight()) {
                    shortestPath = currentPath;
                    nextExhibit = exhibitsToVisit.get(i);
                }
            }

            exhibitsToVisit.remove(nextExhibit);

            ArrayList<String> steps = new ArrayList<String>();

            int i = 1;
            for (IdentifiedWeightedEdge e : shortestPath.getEdgeList()) {
                steps.add(String.format("  %d. Walk %.0f meters along %s from '%s' to '%s'.\n",
                        i,
                        g.getEdgeWeight(e),
                        eInfo.get(e.getId()).street,
                        vInfo.get(g.getEdgeSource(e).toString()).name,
                        vInfo.get(g.getEdgeTarget(e).toString()).name));
                i++;
            }

            directions.add(new Direction(previous.name, nextExhibit.name, steps));

            previous = nextExhibit;
        }

        GraphPath<String, IdentifiedWeightedEdge> pathBack = DijkstraShortestPath.findPathBetween(g, previous.id, gate.id);

        ArrayList<String> steps = new ArrayList<String>();
        int i = 1;
        for (IdentifiedWeightedEdge e : pathBack.getEdgeList()) {
            steps.add(String.format("  %d. Walk %.0f meters along %s from '%s' to '%s'.\n",
                    i,
                    g.getEdgeWeight(e),
                    eInfo.get(e.getId()).street,
                    vInfo.get(g.getEdgeSource(e).toString()).name,
                    vInfo.get(g.getEdgeTarget(e).toString()).name));
            i++;
        }

        directions.add(new Direction(previous.name, gate.name, steps));

        DirectionTracker.directions = directions;
    }
}