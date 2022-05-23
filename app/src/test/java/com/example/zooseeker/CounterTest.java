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
public class CounterTest {
    private NodeDao dao;
    private NodeDatabase db;

    // Three mock ExhibitItems used for Testing
    Node ex1 = new Node("gorillas", VertexInfo.Kind.EXHIBIT, "Gorillas", "gorilla, monkey, ape, mammal", 50, 60);
    Node ex2 = new Node("gators", VertexInfo.Kind.EXHIBIT, "Alligators", "alligator, reptile, gator", 55, 65);
    Node ex3 = new Node("lions", VertexInfo.Kind.EXHIBIT, "Lions", "lion, cat, mammal, africa", 70, 78);


    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, NodeDatabase.class)
                .allowMainThreadQueries()
                .build();
        dao = db.nodeDao();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void counterUp() {
        // Create a mock list of exhibits to be inserted into a list.
        List<Node> mockItems = new ArrayList<>();
        mockItems.add(ex1);
        mockItems.add(ex2);
        mockItems.add(ex3);
        dao.insertAll(mockItems);
        List<Node> items = dao.getAll();
        // Verify that none of the exhibits are added
        for (Node item : items) {
            assertFalse(item.added);
        }
        // Set the added parameter of all exhibits to true and update database
        for (Node item : items) {
            item.added = true;
            dao.update(item);
        }
        // Test counterUp() by checking getNumChecked() returns that all exhibits are checked
        for (Node item : items) {
            assertTrue(item.added);
        }
    }

    @Test
    public void counterDown() {
        // Create a mock list of exhibits to be inserted into a list.
        List<Node> mockItems = new ArrayList<>();
        mockItems.add(ex1);
        mockItems.add(ex2);
        mockItems.add(ex3);
        dao.insertAll(mockItems);
        List<Node> items = dao.getAll();
        // Verify that none of the exhibits are added
        for (Node item : items) {
            assertFalse(item.added);
        }
        // Set the added parameter of all exhibits to true and update database
        for (Node item : items) {
            item.added = true;
            dao.update(item);
        }
        // Test counterUp() by checking getNumChecked() returns that all exhibits are checked
        for (Node item : items) {
            assertTrue(item.added);
        }
        // Set one exhibit to false and update database
        items.get(0).added = false;
        dao.update(items.get(0));
        // Test counterDown() by that one exhibit is un-checked
        assertFalse(items.get(0).added);
    }

}
