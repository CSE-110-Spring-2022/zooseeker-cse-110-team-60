package com.example.zooseeker;

import static org.jgrapht.alg.shortestpath.DijkstraShortestPath.findPathBetween;

import android.util.Log;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DirectionTracker {
    Graph g;
    Map<String, ZooData.VertexInfo> vInfo;
    Map<String, ZooData.EdgeInfo> eInfo;

    public DirectionTracker(Graph g, Map<String, ZooData.VertexInfo> vInfo, Map<String, ZooData.EdgeInfo> eInfo) {
        this.g = g;
        this.vInfo = vInfo;
        this.eInfo = eInfo;
    }

    public List<Direction> getDirections(List<ExhibitItem> exhibitsToVisit) {
        List<Direction> directions = new ArrayList<Direction>();

        ExhibitItem gate = new ExhibitItem("entrance_exit_gate", VertexInfo.Kind.EXHIBIT, "Gate", "");

        ExhibitItem previous = gate;

        Log.d("START", "!!!!!!!!!");

        while (!exhibitsToVisit.isEmpty()) {
            Log.d("previous", previous.id);

            GraphPath<String, IdentifiedWeightedEdge> shortestPath = DijkstraShortestPath.findPathBetween(g, previous.id, exhibitsToVisit.get(0).id);
            ExhibitItem nextExhibit = exhibitsToVisit.get(0);

            Log.d("pathCurrent", shortestPath.toString() + ", WEIGHT: " + String.valueOf(shortestPath.getWeight()));

            for (int i = 1; i < exhibitsToVisit.size(); i++) {
                GraphPath<String, IdentifiedWeightedEdge> currentPath = DijkstraShortestPath.findPathBetween(g, previous.id, exhibitsToVisit.get(i).id);
                Log.d("pathCurrent", currentPath.toString() + ", WEIGHT: " + String.valueOf(currentPath.getWeight()));
                if (currentPath.getWeight() < shortestPath.getWeight()) {
                    shortestPath = currentPath;
                    nextExhibit = exhibitsToVisit.get(i);
                }
            }

            Log.d("pathShortest", shortestPath.toString());


            Log.d("next", nextExhibit.id);

//            previous = nextExhibit;
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
        Log.d("pathCurrent", pathBack.toString() + ", WEIGHT: " + String.valueOf(pathBack.getWeight()));

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

        Log.d("END", "!!!!!!!!!!");

        return directions;
    }
}