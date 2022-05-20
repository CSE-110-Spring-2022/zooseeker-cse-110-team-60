package com.example.zooseeker;

import static org.junit.Assert.assertEquals;

import android.app.Application;
import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.jgrapht.Graph;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class DirectionTest {
        DirectionTracker dt;

    @Before
    public void createGraph() {
        Context context = ApplicationProvider.getApplicationContext();
        Graph<String, IdentifiedWeightedEdge> g = ZooData.loadZooGraphJSON(context, "sample_zoo_graph.JSON");
        Map<String, ZooData.VertexInfo> vInfo = ZooData.loadVertexInfoJSON(context,"sample_node_info.JSON");
        Map<String, ZooData.EdgeInfo> eInfo = ZooData.loadEdgeInfoJSON(context,"sample_edge_info.JSON");

        dt = new DirectionTracker(g, vInfo, eInfo);
    }

    @Test
    public void getDirectionsTest() {
        List<ExhibitItem> toVisit = new ArrayList<ExhibitItem>();
        ExhibitItem gate = new ExhibitItem("entrance_exit_gate", VertexInfo.Kind.EXHIBIT, "Gate", "");
        ExhibitItem gorillas = new ExhibitItem("gorillas", VertexInfo.Kind.EXHIBIT, "Gorillas", "gorilla, monkey, ape, mammal");
        ExhibitItem lions= new ExhibitItem("lions", VertexInfo.Kind.EXHIBIT, "Lions", "lion, cat, mammal, africa");
        ExhibitItem gators = new ExhibitItem("gators", VertexInfo.Kind.EXHIBIT, "Alligators", "alligator, reptile, gator");
        ExhibitItem foxes = new ExhibitItem("arctic_foxes", VertexInfo.Kind.EXHIBIT, "Foxes", "arctic, foxes, mammal");
        Collections.addAll(toVisit, gorillas, gators, lions, foxes);

        dt.getDirections(toVisit);

        // construct the list of directions you expect to get
        // Directions are gate -> gators -> lions -> gorillas -> foxes -> gate

        List<String> d1Expected = Arrays.asList("  1. Walk 10 meters along Entrance Way from 'Entrance and Exit Gate' to 'Entrance Plaza'.\n" +
                ",   2. Walk 100 meters along Reptile Road from 'Entrance Plaza' to 'Alligators'.\n");
        List<String> d2Expected = Arrays.asList("  1. Walk 200 meters along Sharp Teeth Shortcut from 'Alligators' to 'Lions'.\n" +
                "");
        List<String> d3Expected = Arrays.asList("  1. Walk 200 meters along Africa Rocks Street from 'Gorillas' to 'Lions'.\n" +
                "");
        List<String> d4Expected = Arrays.asList("  1. Walk 200 meters along Africa Rocks Street from 'Entrance Plaza' to 'Gorillas'.\n" +
                ",   2. Walk 300 meters along Arctic Avenue from 'Entrance Plaza' to 'Arctic Foxes'.\n" +
                "");
        List<String> d5Expected = Arrays.asList("  1. Walk 300 meters along Arctic Avenue from 'Entrance Plaza' to 'Arctic Foxes'.\n" +
                ",   2. Walk 10 meters along Entrance Way from 'Entrance and Exit Gate' to 'Entrance Plaza'.\n" +
                "");

        Direction d1 = new Direction("Gate", "Alligators", d1Expected);
        Direction d2 = new Direction("Alligators", "Lions", d2Expected);
        Direction d3 = new Direction("Lions", "Gorillas", d3Expected);
        Direction d4 = new Direction("Gorillas", "Foxes", d4Expected);
        Direction d5 = new Direction("Foxes", "Gate", d5Expected);

        // build a list of expected direction objects based on the above
        List<Direction> expected = new ArrayList<Direction>();
        Collections.addAll(expected, d1, d2, d3, d4, d5);

        // assert dt.directions() matches expected at every step of the way
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(dt.directions.get(i).getStart(), expected.get(i).getStart());
            assertEquals(dt.directions.get(i).getEnd(), expected.get(i).getEnd());
        }


    }

    @Test
    public void getDirectionsTestEmpty() {
        List<ExhibitItem> toVisit = new ArrayList<ExhibitItem>();
        dt.getDirections(toVisit);

        // Create the expected list of directions
        List<Direction> expected = new ArrayList<Direction>();
        List<String> d1Expected = Arrays.asList("");
        Direction d1 = new Direction("Gate", "Gate", d1Expected);
        expected.add(d1);

        // assert dt.directions matches expected with a list with only the gate.
        assertEquals(dt.directions.get(0).getStart(), expected.get(0).getStart());
        assertEquals(dt.directions.get(0).getEnd(), expected.get(0).getEnd());


    }


}
