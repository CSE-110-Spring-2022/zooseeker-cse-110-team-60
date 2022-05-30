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

    @Before
    public void setup() {
        DirectionTracker.loadGraphData(ApplicationProvider.getApplicationContext(), "test_vertex_info.JSON", "test_edge_info.JSON", "test_zoo_graph_info.JSON");
        DirectionTracker.loadDatabaseAndDaoByContext(ApplicationProvider.getApplicationContext());

        Context context = ApplicationProvider.getApplicationContext();
        NodeDatabase testDb = Room.inMemoryDatabaseBuilder(context, NodeDatabase.class)
                .allowMainThreadQueries()
                .build();
        NodeDatabase.injectTestDatabase(testDb);

        List<Node> todos = Node.loadJSON(context, "test_vertex_info.JSON");
        dao = testDb.nodeDao();
        dao.insertAll(todos);

        DirectionTracker.setDao(dao);

        List<Node> toVisit = new ArrayList<Node>();
        toVisit.add(dao.get("gorillas"));
        toVisit.add(dao.get("lions"));
        toVisit.add(dao.get("gators"));
        DirectionTracker.initDirections(DirectionTracker.getGateId(), toVisit);
    }

    @Test
    public void summaryTest(){
        RoutePlanSummaryAdapter adapter = new RoutePlanSummaryAdapter();
        adapter.setHasStableIds(true);

        adapter.setDirectionItems(DirectionTracker.getRoutePlanSummary());

        List<String> directions = adapter.getDirectionItems();
        String entranceToGators = directions.get(0);

        assertEquals(entranceToGators, "Entrance and Exit Gate to Alligators (110.0 feet)");
    }

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


        assertEquals(dist, 200.0, 0);

    }
}
