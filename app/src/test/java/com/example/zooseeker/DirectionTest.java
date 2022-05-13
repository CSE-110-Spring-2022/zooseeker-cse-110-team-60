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
        ExhibitItem gorillas = new ExhibitItem("gorillas", VertexInfo.Kind.EXHIBIT, "Gorillas", "");
        ExhibitItem lions = new ExhibitItem("Lions", VertexInfo.Kind.EXHIBIT, "Lions", "");
        ExhibitItem gators = new ExhibitItem("gators", VertexInfo.Kind.EXHIBIT, "Gators", "");
        ExhibitItem foxes = new ExhibitItem("arctic_foxes", VertexInfo.Kind.EXHIBIT, "Foxes", "");
        Collections.addAll(toVisit, gorillas, gators, lions, foxes);

        dt.getDirections(toVisit);

        // construct the list of directions you expect to get
        Direction d1 = new Direction("", "", Arrays.asList(""));

        // build a list of expected direction objects based on the above



        // assert dt.directions() matches expected at every step of the way

    }

    @Test
    public void getDirectionsTestEmpty() {

        // do the same thing, but give getDirections() an empty list
        // make sure the resulting list of directions is also empty

    }


}
