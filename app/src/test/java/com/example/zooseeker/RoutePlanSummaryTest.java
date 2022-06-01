package com.example.zooseeker;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class RoutePlanSummaryTest {
    NodeDao dao;

    /**
     * Name:     createGraph
     * Behavior: Using a given JSON file, load in the graph data as well as create a database. Then,
     *           mark 3 exhibits as visited and initialize the direction tracker.
     */
    @Before
    public void createGraph() {
        DirectionTracker.loadGraphData(ApplicationProvider.getApplicationContext(), "exhibit_info.json", "trail_info.json", "zoo_graph.json");
        DirectionTracker.loadDatabaseAndDaoByContext(ApplicationProvider.getApplicationContext());

        Context context = ApplicationProvider.getApplicationContext();
        NodeDatabase testDb = Room.inMemoryDatabaseBuilder(context, NodeDatabase.class)
                .allowMainThreadQueries()
                .build();
        NodeDatabase.injectTestDatabase(testDb);

        List<Node> todos = Node.loadJSON(context, "exhibit_info.json");
        dao = testDb.nodeDao();
        dao.insertAll(todos);

        DirectionTracker.setDao(dao);

        List<Node> toVisit = new ArrayList<Node>();
        toVisit.add(dao.get("gorilla"));
        toVisit.add(dao.get("flamingo"));
        toVisit.add(dao.get("toucan"));
        DirectionTracker.initDirections(DirectionTracker.getGateId(), toVisit);
    }

    /**
     * Name:     summaryTest
     * Behavior: Create a RoutePlan Summary adapter and verify that calling getDirectionItems()
     *           returns the expected summary given that only one exhibit is checked.
     */
    @Test
    public void summaryTest(){
        RoutePlanSummaryAdapter adapter = new RoutePlanSummaryAdapter();
        adapter.setHasStableIds(true);

        adapter.setDirectionItems(DirectionTracker.getRoutePlanSummary());

        List<String> directions = adapter.getDirectionItems();
        String entranceToGators = directions.get(0);

        assertEquals(entranceToGators, "Entrance and Exit Gate to Flamingos (5300.0 feet)");
    }

    /**
     * Name:     distanceTest
     * Behavior: Create a RoutePlan Summary adapter and verify that the distance in the
     *           for the directions in the summary is correct.
     */
    @Test
    public void distanceTest(){
        RoutePlanSummaryAdapter adapter = new RoutePlanSummaryAdapter();
        adapter.setHasStableIds(true);

        adapter.setDirectionItems(DirectionTracker.getRoutePlanSummary());

        List<String> directions = adapter.getDirectionItems();
        String gatorsToLions = directions.get(1);

        // https://stackoverflow.com/questions/17076030/how-can-i-find-int-values-within-a-string
        String clean = gatorsToLions.replaceAll("\\D+","");
        clean = clean.substring(0, clean.length()-1);
        double dist = Double.parseDouble(clean);


        assertEquals(dist, 7800.0, 0);

    }
}