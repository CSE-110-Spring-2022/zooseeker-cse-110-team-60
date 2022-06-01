package com.example.zooseeker;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class DirectionTest {
    NodeDao dao;
    NodeDatabase testDb;

    /**
     * Name:     createGraph
     * Behavior: Using a given JSON file, load in the graph data as well as create a database. Then,
     *           mark 3 exhibits as visited and initialize the direction tracker.
     *
     */
    @Before
    public void createGraph() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.recyclerView;
            RecyclerView.ViewHolder firstVH = recyclerView.findViewHolderForAdapterPosition(0);
        });

        DirectionTracker.loadGraphData(ApplicationProvider.getApplicationContext(), "exhibit_info.json", "trail_info.json", "zoo_graph.json");
        DirectionTracker.loadDatabaseAndDaoByContext(ApplicationProvider.getApplicationContext());

        Context context = ApplicationProvider.getApplicationContext();
        testDb = Room.inMemoryDatabaseBuilder(context, NodeDatabase.class)
                .allowMainThreadQueries()
                .build();
        NodeDatabase.injectTestDatabase(testDb);
        List<Node> todos = Node.loadJSON(context, "exhibit_info.json");
        dao = testDb.nodeDao();
        dao.insertAll(todos);

        DirectionTracker.setDao(dao);

        List<Node> toVisit = new ArrayList<Node>();
        toVisit.add(dao.get("flamingo"));
        toVisit.add(dao.get("gorilla"));
        toVisit.add(dao.get("toucan"));
        DirectionTracker.initDirections(DirectionTracker.getGateId(), toVisit);
    }

    /**
     * Name:     closeDb
     * Behavior: Close the database after tests are finished running
     */
    @After
    public void closeDb() {
        testDb.close();
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
        expectedSummary.add("Entrance and Exit Gate to Flamingos (5300.0 feet)");
        expectedSummary.add("Flamingos to Gorillas (7800.0 feet)");
        expectedSummary.add("Gorillas to Parker Aviary (6300.0 feet)");
        expectedSummary.add("Parker Aviary to Entrance and Exit Gate (7400.0 feet)");
        assertEquals(expectedSummary, DirectionTracker.getRoutePlanSummary());

        // Create 3 nodes
        Node flamingos = dao.get("flamingo");
        Node gorillas = dao.get("gorilla");
        Node toucans = dao.get("toucan");


        // Verify that they're in the correct order in direction tracker
        assertEquals(flamingos.id, DirectionTracker.currentExhibitIdsOrder.get(0));
        assertEquals(gorillas.id, DirectionTracker.currentExhibitIdsOrder.get(1));
        assertEquals(toucans.id, DirectionTracker.currentExhibitIdsOrder.get(2));
    }

    /**
     * Name:     getDirectionTest
     * Behavior: Verify that the directions generated from DirectionTracker match the expected
     *           directions.
     *
     */
//    @Test
//    public void getDirectionTest() {
//
//        Direction currentDirection = DirectionTracker.getDirection("entrance_exit_gate");
//        Direction expectedDirection = new Direction("Entrance and Exit Gate", "Flamingos", new ArrayList<String>(), new ArrayList<String>(), 0, new ArrayList<String>());
//        assertEquals(expectedDirection.getStart(), currentDirection.getStart());
//        assertEquals(expectedDirection.getEnd(), currentDirection.getEnd());
//        DirectionTracker.next();
//
//        expectedDirection = new Direction("Flamingos", "Gorillas", new ArrayList<String>(), new ArrayList<String>(), 0, new ArrayList<String>());
//        currentDirection = DirectionTracker.getDirection("flamingo");
//        assertEquals(expectedDirection.getStart(), currentDirection.getStart());
//        assertEquals(expectedDirection.getEnd(), currentDirection.getEnd());
//        DirectionTracker.next();
//
//        expectedDirection = new Direction("Gorillas", "Parker Aviary", new ArrayList<String>(), new ArrayList<String>(), 0, new ArrayList<String>());
//        currentDirection = DirectionTracker.getDirection("gorilla");
//        assertEquals(expectedDirection.getStart(), currentDirection.getStart());
//        assertEquals(expectedDirection.getEnd(), currentDirection.getEnd());
//        DirectionTracker.previous();
//        DirectionTracker.previous();
//
//        expectedDirection = new Direction("Gorillas", "Flamingos", new ArrayList<String>(), new ArrayList<String>(), 0, new ArrayList<String>());
//        currentDirection = DirectionTracker.getDirection("gorilla");
//        assertEquals(expectedDirection.getStart(), currentDirection.getStart());
//        assertEquals(expectedDirection.getEnd(), currentDirection.getEnd());
//        DirectionTracker.next();
//        DirectionTracker.next();
//        DirectionTracker.next();
//
//        // Three mock ExhibitItems used for Testing
//        Node Toucan = new Node("toucan", "parker_aviary", VertexInfo.Kind.EXHIBIT, "Toucan", "", 32.73870593465047, -117.1695850705821);
//        expectedDirection = new Direction("Parker Aviary", "Entrance and Exit Gate", new ArrayList<String>(), new ArrayList<String>(), 0, new ArrayList<String>());
//        Node x = DirectionTracker.getParentNodeIfExists(Toucan);
//        currentDirection = DirectionTracker.getDirection(x.id);
//        assertEquals(expectedDirection.getStart(), currentDirection.getStart());
//        assertEquals(expectedDirection.getEnd(), currentDirection.getEnd());
//    }
    /**
     * Name:     redirectTest
     * Behavior: Verify after using redirect, the order of exhibits is changed to its expected
     *           direction.
     */
    @Test
    public void redirectTest() {
        DirectionTracker.redirect("owens_aviary");
        assertEquals("toucan", DirectionTracker.currentExhibitIdsOrder.get(0));
        assertEquals("gorilla", DirectionTracker.currentExhibitIdsOrder.get(1));
        assertEquals("flamingo", DirectionTracker.currentExhibitIdsOrder.get(2));

        DirectionTracker.next();
        DirectionTracker.redirect("benchley_plaza");
        assertEquals("gorilla", DirectionTracker.currentExhibitIdsOrder.get(1));
        assertEquals("flamingo", DirectionTracker.currentExhibitIdsOrder.get(2));

        DirectionTracker.previous();
        DirectionTracker.redirect("hippo");
        assertEquals("toucan", DirectionTracker.currentExhibitIdsOrder.get(0));
        assertEquals("gorilla", DirectionTracker.currentExhibitIdsOrder.get(1));
        assertEquals("flamingo", DirectionTracker.currentExhibitIdsOrder.get(2));

        DirectionTracker.redirect("koi");
        assertEquals("flamingo", DirectionTracker.currentExhibitIdsOrder.get(0));
        assertEquals("gorilla", DirectionTracker.currentExhibitIdsOrder.get(1));
        assertEquals("toucan", DirectionTracker.currentExhibitIdsOrder.get(2));
    }

    /**
     * Name:     skipTest
     * Behavior: Verify after using skip, the order of exhibits is maintained with the skipped
     *           exhibit removed from the directions.
     */
    @Test
    public void skipTest() {
        DirectionTracker.skip("entrance_exit_gate");
        assertEquals("toucan", DirectionTracker.currentExhibitIdsOrder.get(0));
        assertEquals("gorilla", DirectionTracker.currentExhibitIdsOrder.get(1));
    }
}