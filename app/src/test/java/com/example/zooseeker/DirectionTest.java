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
public class DirectionTest {
    ExhibitItemDao dao;

    @Before
    public void createGraph() {
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
    public void initDirectionsTest() {
        List<String> expectedSummary = new ArrayList<>();
        expectedSummary.add("Entrance and Exit Gate to Alligators (110.0m)");
        expectedSummary.add("Alligators to Lions (200.0m)");
        expectedSummary.add("Lions to Gorillas (200.0m)");
        expectedSummary.add("Gorillas to Entrance and Exit Gate (210.0m)");
        assertEquals(expectedSummary, DirectionTracker.getRoutePlanSummary());

        ExhibitItem gators = dao.get("gators");
        ExhibitItem lions = dao.get("lions");
        ExhibitItem gorillas = dao.get("gorillas");

        assertEquals(gators.id, DirectionTracker.currentExhibitsOrder.get(0).id);
        assertEquals(lions.id, DirectionTracker.currentExhibitsOrder.get(1).id);
        assertEquals(gorillas.id, DirectionTracker.currentExhibitsOrder.get(2).id);
    }

    @Test
    public void getDirectionTest() {
        Direction currentDirection = DirectionTracker.getDirection("entrance_exit_gate");
        Direction expectedDirection = new Direction("Entrance and Exit Gate", "Alligators", new ArrayList<String>(), 0);
        assertEquals(expectedDirection.getStart(), currentDirection.getStart());
        assertEquals(expectedDirection.getEnd(), currentDirection.getEnd());
        DirectionTracker.next();

        expectedDirection = new Direction("Alligators", "Lions", new ArrayList<String>(), 0);
        currentDirection = DirectionTracker.getDirection("gators");
        assertEquals(expectedDirection.getStart(), currentDirection.getStart());
        assertEquals(expectedDirection.getEnd(), currentDirection.getEnd());
        DirectionTracker.next();

        expectedDirection = new Direction("Lions", "Gorillas", new ArrayList<String>(), 0);
        currentDirection = DirectionTracker.getDirection("lions");
        assertEquals(expectedDirection.getStart(), currentDirection.getStart());
        assertEquals(expectedDirection.getEnd(), currentDirection.getEnd());
        DirectionTracker.previous();
        DirectionTracker.previous();

        expectedDirection = new Direction("Lions", "Alligators", new ArrayList<String>(), 0);
        currentDirection = DirectionTracker.getDirection("lions");
        assertEquals(expectedDirection.getStart(), currentDirection.getStart());
        assertEquals(expectedDirection.getEnd(), currentDirection.getEnd());
        DirectionTracker.next();
        DirectionTracker.next();
        DirectionTracker.next();

        expectedDirection = new Direction("Gorillas", "Entrance and Exit Gate", new ArrayList<String>(), 0);
        currentDirection = DirectionTracker.getDirection("gorillas");
        assertEquals(expectedDirection.getStart(), currentDirection.getStart());
        assertEquals(expectedDirection.getEnd(), currentDirection.getEnd());
    }
}