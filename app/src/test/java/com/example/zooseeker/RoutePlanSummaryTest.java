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
    ExhibitItemDao dao;

    @Before
    public void setup() {
        DirectionTracker.loadGraphData(ApplicationProvider.getApplicationContext(), "sample_node_info.JSON", "sample_edge_info.JSON", "sample_zoo_graph.JSON");
        DirectionTracker.loadDatabaseAndDaoByContext(ApplicationProvider.getApplicationContext());

        Context context = ApplicationProvider.getApplicationContext();
        ExhibitDatabase testDb = Room.inMemoryDatabaseBuilder(context, ExhibitDatabase.class)
                .allowMainThreadQueries()
                .build();
        ExhibitDatabase.injectTestDatabase(testDb);

        List<ExhibitItem> todos = ExhibitItem.loadJSON(context, "sample_node_info.JSON");
        dao = testDb.exhibitItemDao();
        dao.insertAll(todos);

        DirectionTracker.setDao(dao);

        List<ExhibitItem> toVisit = new ArrayList<ExhibitItem>();
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

        assertEquals(entranceToGators, "Entrance and Exit Gate to Alligators (110.0m)");
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
