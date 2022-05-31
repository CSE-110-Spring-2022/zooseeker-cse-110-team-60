package com.example.zooseeker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class NodeDatabaseTest {
    private NodeDao dao;
    private NodeDatabase db;

    // Three mock ExhibitItems used for Testing
    Node ex1 = new Node("gorillas", "monkey", VertexInfo.Kind.EXHIBIT, "Gorillas", "gorilla, monkey, ape, mammal", 30, 60);
    Node ex2 = new Node("gators", "", VertexInfo.Kind.EXHIBIT, "Alligators", "alligator, reptile, gator", 40, 70);
    Node ex3 = new Node("lions", "mammal", VertexInfo.Kind.EXHIBIT, "Lions", "lion, cat, mammal, africa", 50, 80);
    Node ex4 = new Node("intersection1", "", VertexInfo.Kind.INTERSECTION, "Intersection 1", "", 55, 88);

    /**
     * Name:     createDb
     * Behavior: Get the context, create a database, and initialize it before tests run
     */
    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, NodeDatabase.class)
                 .allowMainThreadQueries()
                 .build();
        dao = db.nodeDao();
    }

    /**
     * Name:     closeDb
     * Behavior: Close the database after tests are finished running
     */
    @After
    public void closeDb() {
        db.close();
    }

    /**
     * Name:     testInsertAll
     * Behavior: Verify that after inserting items into the database that the size is correct.
     */
    @Test
    public void testInsertAll() {
        // Create a mock list of exhibits to be added into a list.
        List<Node> items = new ArrayList<>();
        items.add(ex1);
        items.add(ex2);
        items.add(ex3);
        items.add(ex4);
        // Test insertAll() by inserting the list into the database and checking its size
        List<Long> numInserted = dao.insertAll(items);
        assertEquals(4, numInserted.size());
    }

    /**
     * Name:     testGet
     * Behavior: Verify that the items received from the dao are correct.
     */
    @Test
    public void testGet() {
        // Create a mock exhibit and insert it as a list into the database
        List<Node> items = new ArrayList<>();
        items.add(ex1);
        items.add(ex4);
        dao.insertAll(items);

        // Test get() by checking if the Node returned from get() has the correct parameters
        String id1 = "gorillas";
        Node item1 = dao.get(id1);
        assertEquals(id1, item1.id);
        assertEquals("monkey", item1.parentId);
        assertEquals(VertexInfo.Kind.EXHIBIT, item1.kind);
        assertEquals("Gorillas", item1.name);
        assertEquals("gorilla, monkey, ape, mammal", item1.tags);
        assertFalse(item1.added);
        assertEquals(30, item1.latitude, 0.000001);
        assertEquals(60, item1.longitude, 0.000001);

        String id2 = "intersection1";
        Node item2 = dao.get(id2);
        assertEquals(id2, item2.id);
        assertEquals("", item2.parentId);
        assertEquals(VertexInfo.Kind.INTERSECTION, item2.kind);
        assertEquals("Intersection 1", item2.name);
        assertEquals("", item2.tags);
        assertFalse(item2.added);
        assertEquals(55, item2.latitude, 0.000001);
        assertEquals(88, item2.longitude, 0.000001);
    }

    /**
     * Name:     getAll
     * Behavior: Verify that all exhibits, intersections,
     *           and groups inserted are returned in the correct order
     */
    @Test
    public void getAll() {
        // Create a mock list of exhibits to be inserted into a list.
        List<Node> items = new ArrayList<>();
        items.add(ex1);
        items.add(ex2);
        items.add(ex3);
        items.add(ex4);
        dao.insertAll(items);
        List<Node> listToTest = dao.getAll();
        // Test getAll() by verifying the list is the correct size and that exhibits are returned in the correct order.
        // Gators should be the first exhibit, then gorillas, and then lions
        assertEquals(items.size(), listToTest.size());
        assertEquals("gators", listToTest.get(0).id);
        assertEquals("gorillas", listToTest.get(1).id);
        assertEquals("intersection1", listToTest.get(2).id);
        assertEquals("lions", listToTest.get(3).id);
    }

    /**
     * Name:     getAllExhibits
     * Behavior: Verify that all exhibits are returned in the correct order
     */
    @Test
    public void getAllExhibits() {
        // Create a mock list of exhibits to be inserted into a list.
        List<Node> items = new ArrayList<>();
        items.add(ex1);
        items.add(ex2);
        items.add(ex3);
        items.add(ex4);
        dao.insertAll(items);
        List<Node> listToTest = dao.getAllExhibits();
        // Test getAll() by verifying the list is the correct size and that exhibits are returned in the correct order.
        // Gators should be the first exhibit, then gorillas, and then lions
        assertEquals(items.size() - 1, listToTest.size());
        assertEquals("gators", listToTest.get(0).id);
        assertEquals("gorillas", listToTest.get(1).id);
        assertEquals("lions", listToTest.get(2).id);
    }

    /**
     * Name:     update
     * Behavior: Verify that update works after changing the added parameter. There should be one
     *           item marked as updated.
     */
    @Test
    public void update() {
        // Create a mock exhibit and insert it as a list into the database
        Node ex1 = new Node("gorillas", "mammal", VertexInfo.Kind.EXHIBIT, "Gorillas", "gorillas, monkey, ape, mammal", 60, 70);
        List<Node> items = new ArrayList<>();
        items.add(ex1);
        dao.insertAll(items);
        // Test update() by checking if itemsUpdated matches and if the added parameter set to True remains True.
        String id = "gorillas";
        Node item = dao.get(id);
        item.added = true;
        int itemsUpdated = dao.update(item);
        assertEquals(1, itemsUpdated);
        assertTrue(item.added);
    }
}
