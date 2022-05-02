package com.example.zooseeker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.widget.CheckBox;

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
    public void testGet() {
        String id = "gorillas";
        ExhibitItem item = dao.get(id);
        assertEquals("gorillas", item.id);
        assertEquals(VertexInfo.Kind.EXHIBIT, item.kind);
        assertEquals("Gorillas", item.name);
        assertEquals("gorillas, monkey, ape, mammal", item.tags);
        assertFalse(item.added);
    }

    @Test
    public void testCheckBox() {
        Boolean completed = true;
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.recyclerView;;
            RecyclerView.ViewHolder firstVH = recyclerView.findViewHolderForAdapterPosition(0);
            assertNotNull(firstVH);
            long id = firstVH.getItemId();

            CheckBox checkBox = firstVH.itemView.findViewById(R.id.added);
            checkBox.setChecked(true);

            assertTrue(checkBox.isChecked());

            checkBox.setChecked(false);
            assertTrue(!checkBox.isChecked());
        });
    }
}
