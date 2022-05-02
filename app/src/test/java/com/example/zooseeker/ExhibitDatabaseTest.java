package com.example.zooseeker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class ExhibitDatabaseTest {
    private ExhibitItemDao dao;
    private ExhibitDatabase db;

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
    public void testInsert() {

    }

    @Test
    public void testGet() {
        String id = "gorillas";
        ExhibitItem item = dao.get(id);
        assertEquals("gorillas", item.id);
        assertEquals(VertexInfo.Kind.EXHIBIT, item.kind);
        assertEquals("Gorillas", item.name);
        assertEquals("gorillas, monkey, ape, mammal", item.tags);
        assertFalse(item.added);
    }
}
