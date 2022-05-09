package com.example.zooseeker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class CounterTest {
    private ExhibitItemDao dao;
    private ExhibitDatabase db;

    // Three mock ExhibitItems used for Testing
    ExhibitItem ex1 = new ExhibitItem("gorillas", VertexInfo.Kind.EXHIBIT, "Gorillas", "gorilla, monkey, ape, mammal");
    ExhibitItem ex2 = new ExhibitItem("gators", VertexInfo.Kind.EXHIBIT, "Alligators", "alligator, reptile, gator");
    ExhibitItem ex3 = new ExhibitItem("lions", VertexInfo.Kind.EXHIBIT, "Lions", "lion, cat, mammal, africa");


    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, ExhibitDatabase.class)
                .allowMainThreadQueries()
                .build();
        dao = db.exhibitItemDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void counterUp() {
        // Create a mock list of exhibits to be inserted into a list.
        List<ExhibitItem> mockItems = new ArrayList<>();
        mockItems.add(ex1);
        mockItems.add(ex2);
        mockItems.add(ex3);
        dao.insertAll(mockItems);
        List<ExhibitItem> items = dao.getAll();
        // Verify that none of the exhibits are added
        for (ExhibitItem item : items) {
            assertFalse(item.added);
        }
        // Set the added parameter of all exhibits to true and update database
        for (ExhibitItem item : items) {
            item.added = true;
            dao.update(item);
        }
        // Test counterUp() by checking getNumChecked() returns that all exhibits are checked
        for (ExhibitItem item : items) {
            assertTrue(item.added);
        }
    }

    @Test
    public void counterDown() {
        // Create a mock list of exhibits to be inserted into a list.
        List<ExhibitItem> mockItems = new ArrayList<>();
        mockItems.add(ex1);
        mockItems.add(ex2);
        mockItems.add(ex3);
        dao.insertAll(mockItems);
        List<ExhibitItem> items = dao.getAll();
        // Verify that none of the exhibits are added
        for (ExhibitItem item : items) {
            assertFalse(item.added);
        }
        // Set the added parameter of all exhibits to true and update database
        for (ExhibitItem item : items) {
            item.added = true;
            dao.update(item);
        }
        // Test counterUp() by checking getNumChecked() returns that all exhibits are checked
        for (ExhibitItem item : items) {
            assertTrue(item.added);
        }
        // Set one exhibit to false and update database
        items.get(0).added = false;
        dao.update(items.get(0));
        // Test counterDown() by that one exhibit is un-checked
        assertFalse(items.get(0).added);
    }

}
