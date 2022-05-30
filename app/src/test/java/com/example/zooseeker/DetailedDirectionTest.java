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

    @Before
    public void createGraph() {
        DirectionTracker.loadGraphData(ApplicationProvider.getApplicationContext(), "sample_node_info.JSON", "sample_edge_info.JSON", "sample_zoo_graph.JSON");
        DirectionTracker.loadDatabaseAndDaoByContext(ApplicationProvider.getApplicationContext());

        Context context = ApplicationProvider.getApplicationContext();
        NodeDatabase testDb = Room.inMemoryDatabaseBuilder(context, NodeDatabase.class)
                .allowMainThreadQueries()
                .build();
        NodeDatabase.injectTestDatabase(testDb);

        List<Node> todos = Node.loadJSON(context, "sample_node_info.JSON");
        dao = testDb.nodeDao();
        dao.insertAll(todos);

        DirectionTracker.setDao(dao);

        List<Node> toVisit = new ArrayList<Node>();
        toVisit.add(dao.get("flamingo"));
        toVisit.add(dao.get("gorilla"));
        toVisit.add(dao.get("toucan"));
        DirectionTracker.initDirections(DirectionTracker.getGateId(), toVisit);
    }

    @Test
    public void briefToDetailed() {
        Direction currentDirection = DirectionTracker.getDirection("entrance_exit_gate");
        Direction expectedDirection = new Direction("Entrance and Exit Gate", "Flamingos", new ArrayList<String>(), new ArrayList<String>(), 0);
        DirectionTracker.next();

        expectedDirection = new Direction("Flamingos", "Gorillas", new ArrayList<String>(), new ArrayList<String>(), 0);
        currentDirection = DirectionTracker.getDirection("flamingo");

        assertNotEquals(currentDirection.getBriefDirections(), currentDirection.getDetailedDirections());
        List <String> expected = currentDirection.getBriefDirections();
        expected = currentDirection.getDetailedDirections();
        List <String> actual = new ArrayList<String>();
        String d1 = new String("  1. Walk 3100 feet along Monkey Trail from 'Flamingos' to 'Capuchin Monkeys'.\n");
        String d2 = new String("  2. Walk 2300 feet along Monkey Trail from 'Capuchin Monkeys' to 'Monkey Trail / Hippo Trail'.\n");
        String d3 = new String("  3. Walk 1200 feet along Monkey Trail from 'Monkey Trail / Hippo Trail' to 'Scripps Aviary'.\n");
        String d4 = new String("  4. Walk 1200 feet along Monkey Trail from 'Scripps Aviary' to 'Gorillas'.\n");
        actual.add(d1);
        actual.add(d2);
        actual.add(d3);
        actual.add(d4);
        assertEquals(expected, actual);
    }

    @Test
    public void detailedToBrief() {
        Direction currentDirection = DirectionTracker.getDirection("entrance_exit_gate");
        Direction expectedDirection = new Direction("Entrance and Exit Gate", "Flamingos", new ArrayList<String>(), new ArrayList<String>(), 0);
        DirectionTracker.next();

        expectedDirection = new Direction("Flamingos", "Gorillas", new ArrayList<String>(), new ArrayList<String>(), 0);
        currentDirection = DirectionTracker.getDirection("flamingo");

        assertNotEquals(currentDirection.getBriefDirections(), currentDirection.getDetailedDirections());
        List <String> expected = currentDirection.getDetailedDirections();
        expected = currentDirection.getBriefDirections();;
        List <String> actual = new ArrayList<String>();
        String d1 = new String("  1. Walk 7800 feet along Monkey Trail from 'Flamingos' to 'Gorillas'.\n");
        actual.add(d1);
        assertEquals(expected, actual);

    }


}
