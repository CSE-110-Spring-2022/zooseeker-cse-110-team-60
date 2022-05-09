package com.example.zooseeker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.util.Log;
import android.widget.CheckBox;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.Update;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@RunWith(AndroidJUnit4.class)
public class ExhibitDatabaseTest {
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
    public void testInsertAll() {
        // Create a mock list of exhibits to be added into a list.
        List<ExhibitItem> items = new ArrayList<>();
        items.add(ex1);
        items.add(ex2);
        // Test insertAll() by inserting the list into the database and checking its size
        List<Long> numInserted = dao.insertAll(items);
        assertEquals(2, numInserted.size());
    }

    @Test
    public void testGet() {
        // Create a mock exhibit and insert it as a list into the database
        List<ExhibitItem> items = new ArrayList<>();
        items.add(ex1);
        dao.insertAll(items);
        // Test get() by checking if the ExhibitItem returned from get() has the correct parameters
        String id = "gorillas";
        ExhibitItem item = dao.get(id);
        assertEquals("gorillas", item.id);
        assertEquals(VertexInfo.Kind.EXHIBIT, item.kind);
        assertEquals("Gorillas", item.name);
        assertEquals("gorilla, monkey, ape, mammal", item.tags);
        assertFalse(item.added);
    }

    @Test
    public void getAll() {
        // Create a mock list of exhibits to be inserted into a list.
        List<ExhibitItem> items = new ArrayList<>();
        items.add(ex1);
        items.add(ex2);
        items.add(ex3);
        dao.insertAll(items);
        List<ExhibitItem> listToTest = dao.getAll();
        // Test getAll() by verifying the list is the correct size and that exhibits are returned in the correct order.
        // Gators should be the first exhibit, then gorillas, and then lions
        assertEquals(items.size(), listToTest.size());
        assertEquals("gators", listToTest.get(0).id);
        assertEquals("gorillas", listToTest.get(1).id);
        assertEquals("lions", listToTest.get(2).id);
    }

    /*
    @Test
    public void getAllLive() {

    }
     */


    @Test
    public void update() {
        // Create a mock exhibit and insert it as a list into the database
        ExhibitItem ex1 = new ExhibitItem("gorillas", VertexInfo.Kind.EXHIBIT, "Gorillas", "gorillas, monkey, ape, mammal");
        List<ExhibitItem> items = new ArrayList<>();
        items.add(ex1);
        dao.insertAll(items);
        // Test update() by checking if itemsUpdated matches and if the added parameter set to True remains True.
        String id = "gorillas";
        ExhibitItem item = dao.get(id);
        item.added = true;
        int itemsUpdated = dao.update(item);
        assertEquals(1, itemsUpdated);
        assertTrue(item.added);
    }
}
