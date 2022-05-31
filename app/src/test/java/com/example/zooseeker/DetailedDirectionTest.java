package com.example.zooseeker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class DetailedDirectionTest {
    NodeDao dao;

    /**
     * Name:     createGraph
     * Behavior: Using a given JSON file, load in the graph data as well as create a database. Then,
     *           mark 3 exhibits as visited and initialize the direction tracker.
     *
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
        toVisit.add(dao.get("flamingo"));
        toVisit.add(dao.get("gorilla"));
        toVisit.add(dao.get("toucan"));
        DirectionTracker.initDirections(DirectionTracker.getGateId(), toVisit);
    }

    /**
     * Name:     briefToDetailed
     * Behavior: Set an directions item and first verify that the brief and detailed directions
     *           are different. Then, set the brief directions object to detailed directions
     *           and verify that they match the expected output.
     */
    @Test
    public void briefToDetailed() {
        Direction currentDirection = DirectionTracker.getDirection("entrance_exit_gate");
        Direction expectedDirection = new Direction("Entrance and Exit Gate", "Flamingos", new ArrayList<String>(), new ArrayList<String>(), 0, new ArrayList<String>());
        DirectionTracker.next();

        expectedDirection = new Direction("Flamingos", "Gorillas", new ArrayList<String>(), new ArrayList<String>(), 0, new ArrayList<String>());
        currentDirection = DirectionTracker.getDirection("flamingo");

        // Verify that brief directions and detailed directions are different.
        assertNotEquals(currentDirection.getBriefDirections(), currentDirection.getDetailedDirections());
        List <String> actual = currentDirection.getBriefDirections();
        actual = currentDirection.getDetailedDirections();

        // Create the expected directions for Detailed directions
        List <String> expected = new ArrayList<String>();
        String d1 = new String("  1. Walk 3100 feet along Monkey Trail from 'Flamingos' to 'Capuchin Monkeys'.\n");
        String d2 = new String("  2. Walk 2300 feet along Monkey Trail from 'Capuchin Monkeys' to 'Monkey Trail / Hippo Trail'.\n");
        String d3 = new String("  3. Walk 1200 feet along Monkey Trail from 'Monkey Trail / Hippo Trail' to 'Scripps Aviary'.\n");
        String d4 = new String("  4. Walk 1200 feet along Monkey Trail from 'Scripps Aviary' to 'Gorillas'.\n");
        expected.add(d1);
        expected.add(d2);
        expected.add(d3);
        expected.add(d4);

        // Verify that the expected output for detailed directions matches the actual.
        assertEquals(expected, actual);
    }

    /**
     * Name:     detailedToBrief
     * Behavior: Set an directions item and first verify that the brief and detailed directions
     *           are different. Then, set the detailed directions object to brief directions
     *           and verify that they match the expected output.
     */
    @Test
    public void detailedToBrief() {
        Direction currentDirection = DirectionTracker.getDirection("entrance_exit_gate");
        Direction expectedDirection = new Direction("Entrance and Exit Gate", "Flamingos", new ArrayList<String>(), new ArrayList<String>(), 0, new ArrayList<String>());
        DirectionTracker.next();

        expectedDirection = new Direction("Flamingos", "Gorillas", new ArrayList<String>(), new ArrayList<String>(), 0, new ArrayList<String>());
        currentDirection = DirectionTracker.getDirection("flamingo");

        // Verify that brief directions and detailed directions are different.
        assertNotEquals(currentDirection.getBriefDirections(), currentDirection.getDetailedDirections());
        List <String> actual = currentDirection.getDetailedDirections();
        actual = currentDirection.getBriefDirections();;

        // Create the expected directions for brief directions
        List <String> expected = new ArrayList<String>();
        String d1 = new String("  1. Walk 7800 feet along Monkey Trail from 'Flamingos' to 'Gorillas'.\n");
        expected.add(d1);

        // Verify that the expected output for brief directions matches the actual.
        assertEquals(expected, actual);

    }


}
