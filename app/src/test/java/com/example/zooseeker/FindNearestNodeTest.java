package com.example.zooseeker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.util.Log;

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
import java.util.ArrayList;
import java.util.List;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;
import java.util.concurrent.Executors;

@RunWith(AndroidJUnit4.class)
public class FindNearestNodeTest {

    NodeDao dao;

    @Test
    public void mocking_findNearest() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.recyclerView;
            RecyclerView.ViewHolder firstVH = recyclerView.findViewHolderForAdapterPosition(0);
        });
        // Create a mock list of exhibits to be added into a list.
        DirectionTracker.loadGraphData(ApplicationProvider.getApplicationContext(), "test_vertex_info.JSON", "test_edge_info.JSON", "test_zoo_graph_info.JSON");
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

        double lat = 32.73453269952235;
        double lng = -117.1526194979577;
        String expected = "intxn_front_treetops";
        String actual = GPSTracker.findNearestNode(lat, lng);
        assertTrue(expected.equals(actual));
    }
}
