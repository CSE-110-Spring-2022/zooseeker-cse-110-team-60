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
public class getDirectionTest {
    NodeDao dao;
    NodeDatabase testDb;

    /**
     * Name:     createGraph
     * Behavior: Using a given JSON file, load in the graph data as well as create a database. Then,
     * mark 3 exhibits as visited and initialize the direction tracker.
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
     * Name:     getDirectionTest
     * Behavior: Verify that the directions generated from DirectionTracker match the expected
     *           directions.
     *
     */
    @Test
    public void getDirectionTest() {

        Direction currentDirection = DirectionTracker.getDirection("entrance_exit_gate");
        Direction expectedDirection = new Direction("Entrance and Exit Gate", "Flamingos", new ArrayList<String>(), new ArrayList<String>(), 0, new ArrayList<String>());
        assertEquals(expectedDirection.getStart(), currentDirection.getStart());
        assertEquals(expectedDirection.getEnd(), currentDirection.getEnd());
        DirectionTracker.next();

        expectedDirection = new Direction("Flamingos", "Gorillas", new ArrayList<String>(), new ArrayList<String>(), 0, new ArrayList<String>());
        currentDirection = DirectionTracker.getDirection("flamingo");
        assertEquals(expectedDirection.getStart(), currentDirection.getStart());
        assertEquals(expectedDirection.getEnd(), currentDirection.getEnd());
        DirectionTracker.next();

        expectedDirection = new Direction("Gorillas", "Parker Aviary", new ArrayList<String>(), new ArrayList<String>(), 0, new ArrayList<String>());
        currentDirection = DirectionTracker.getDirection("gorilla");
        assertEquals(expectedDirection.getStart(), currentDirection.getStart());
        assertEquals(expectedDirection.getEnd(), currentDirection.getEnd());
        DirectionTracker.previous();
        DirectionTracker.previous();

        expectedDirection = new Direction("Gorillas", "Flamingos", new ArrayList<String>(), new ArrayList<String>(), 0, new ArrayList<String>());
        currentDirection = DirectionTracker.getDirection("gorilla");
        assertEquals(expectedDirection.getStart(), currentDirection.getStart());
        assertEquals(expectedDirection.getEnd(), currentDirection.getEnd());
        DirectionTracker.next();
        DirectionTracker.next();
        DirectionTracker.next();

        // Three mock ExhibitItems used for Testing
        Node Toucan = new Node("toucan", "parker_aviary", VertexInfo.Kind.EXHIBIT, "Toucan", "", 32.73870593465047, -117.1695850705821);
        expectedDirection = new Direction("Parker Aviary", "Entrance and Exit Gate", new ArrayList<String>(), new ArrayList<String>(), 0, new ArrayList<String>());
        Node x = DirectionTracker.getParentNodeIfExists(Toucan);
        currentDirection = DirectionTracker.getDirection(x.id);
        assertEquals(expectedDirection.getStart(), currentDirection.getStart());
        assertEquals(expectedDirection.getEnd(), currentDirection.getEnd());
    }
}