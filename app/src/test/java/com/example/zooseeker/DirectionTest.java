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
    NodeDao dao;

    /**
     * Name:     createGraph
     * Behavior: Using a given JSON file, load in the graph data as well as create a database. Then,
     *           mark 3 exhibits as visited and initialize the direction tracker.
     *
     */
    @Before
    public void createGraph() {
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

    /**
     * Name:     initDirectionsTest
     * Behavior: Verify that initializing directions creates a proper route plan summary
     *           Then, verify that the mocked exhibits are in the correct order for directions.
     *
     */
    @Test
    public void initDirectionsTest() {

        // Verify that the route plan summary matches the expected summary.
        List<String> expectedSummary = new ArrayList<>();
        expectedSummary.add("Entrance and Exit Gate to Alligators (110.0 feet)");
        expectedSummary.add("Alligators to Lions (200.0 feet)");
        expectedSummary.add("Lions to Gorillas (200.0 feet)");
        expectedSummary.add("Gorillas to Entrance and Exit Gate (210.0 feet)");
        assertEquals(expectedSummary, DirectionTracker.getRoutePlanSummary());

        // Create 3 nodes
        Node gators = dao.get("gators");
        Node lions = dao.get("lions");
        Node gorillas = dao.get("gorillas");

        // Verify that they're in the correct order in direction tracker
        assertEquals(gators.id, DirectionTracker.currentExhibitIdsOrder.get(0));
        assertEquals(lions.id, DirectionTracker.currentExhibitIdsOrder.get(1));
        assertEquals(gorillas.id, DirectionTracker.currentExhibitIdsOrder.get(2));
    }

    /**
     * Name:     getDirectionTest
     * Behavior: Verify that the directions generated from DirectionTracker match the expected
     *           directions.
     *
     */
    @Test
    public void getDirectionTest() {
        Direction currentDirection = DirectionTracker.getDirection("entrance_exit_gate");
        Direction expectedDirection = new Direction("Entrance and Exit Gate", "Alligators", new ArrayList<String>(), new ArrayList<String>(), 0, new ArrayList<String>());
        assertEquals(expectedDirection.getStart(), currentDirection.getStart());
        assertEquals(expectedDirection.getEnd(), currentDirection.getEnd());
        DirectionTracker.next();

        expectedDirection = new Direction("Alligators", "Lions", new ArrayList<String>(), new ArrayList<String>(), 0, new ArrayList<String>());
        currentDirection = DirectionTracker.getDirection("gators");
        assertEquals(expectedDirection.getStart(), currentDirection.getStart());
        assertEquals(expectedDirection.getEnd(), currentDirection.getEnd());
        DirectionTracker.next();

        expectedDirection = new Direction("Lions", "Gorillas", new ArrayList<String>(), new ArrayList<String>(), 0, new ArrayList<String>());
        currentDirection = DirectionTracker.getDirection("lions");
        assertEquals(expectedDirection.getStart(), currentDirection.getStart());
        assertEquals(expectedDirection.getEnd(), currentDirection.getEnd());
        DirectionTracker.previous();
        DirectionTracker.previous();

        expectedDirection = new Direction("Lions", "Alligators", new ArrayList<String>(), new ArrayList<String>(), 0, new ArrayList<String>());
        currentDirection = DirectionTracker.getDirection("lions");
        assertEquals(expectedDirection.getStart(), currentDirection.getStart());
        assertEquals(expectedDirection.getEnd(), currentDirection.getEnd());
        DirectionTracker.next();
        DirectionTracker.next();
        DirectionTracker.next();

        expectedDirection = new Direction("Gorillas", "Entrance and Exit Gate", new ArrayList<String>(), new ArrayList<String>(), 0, new ArrayList<String>());
        currentDirection = DirectionTracker.getDirection("gorillas");
        assertEquals(expectedDirection.getStart(), currentDirection.getStart());
        assertEquals(expectedDirection.getEnd(), currentDirection.getEnd());
    }
    /**
     * Name:     redirectTest
     * Behavior: Verify after using redirect, the order of exhibits is changed to its expected
     *           direction.
     */
    @Test
    public void redirectTest() {
        DirectionTracker.redirect("elephant_odyssey");
        assertEquals("lions", DirectionTracker.currentExhibitIdsOrder.get(0));
        assertEquals("gorillas", DirectionTracker.currentExhibitIdsOrder.get(1));
        assertEquals("gators", DirectionTracker.currentExhibitIdsOrder.get(2));

        DirectionTracker.next();
        DirectionTracker.redirect("entrance_plaza");
        assertEquals("gators", DirectionTracker.currentExhibitIdsOrder.get(1));
        assertEquals("gorillas", DirectionTracker.currentExhibitIdsOrder.get(2));

        DirectionTracker.previous();
        DirectionTracker.redirect("arctic_foxes");
        assertEquals("gators", DirectionTracker.currentExhibitIdsOrder.get(0));
        assertEquals("lions", DirectionTracker.currentExhibitIdsOrder.get(1));
        assertEquals("gorillas", DirectionTracker.currentExhibitIdsOrder.get(2));

        DirectionTracker.redirect("gorillas");
        assertEquals("gorillas", DirectionTracker.currentExhibitIdsOrder.get(0));
        assertEquals("lions", DirectionTracker.currentExhibitIdsOrder.get(1));
        assertEquals("gators", DirectionTracker.currentExhibitIdsOrder.get(2));
    }

    /**
     * Name:     skipTest
     * Behavior: Verify after using skip, the order of exhibits is maintained with the skipped
     *           exhibit removed from the directions.
     */
    @Test
    public void skipTest() {
        DirectionTracker.skip("entrance_exit_gate");
        assertEquals("gorillas", DirectionTracker.currentExhibitIdsOrder.get(0));
        assertEquals("lions", DirectionTracker.currentExhibitIdsOrder.get(1));
    }
}