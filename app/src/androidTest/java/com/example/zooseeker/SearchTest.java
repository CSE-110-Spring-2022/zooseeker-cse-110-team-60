package com.example.zooseeker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import android.view.View;
import android.widget.TextView;


import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class SearchTest {
    ExhibitDatabase testDb;
    ExhibitItemDao todoListItemDao;

    private static void forceLayout(RecyclerView recyclerView) {
        recyclerView.measure(View.MeasureSpec.UNSPECIFIED,
                             View.MeasureSpec.UNSPECIFIED);
        recyclerView.layout(0, 0, 1080, 2280);
    }

    @Before
    public void resetDatabase() {
        Context context = ApplicationProvider.getApplicationContext();
        testDb = Room.inMemoryDatabaseBuilder(context, ExhibitDatabase.class)
                     .allowMainThreadQueries()
                     .build();
        ExhibitDatabase.injectTestDatabase(testDb);

        List<ExhibitItem>
                todos = ExhibitItem.loadJSON(context, "sample_node_info.JSON");
        todoListItemDao = testDb.exhibitItemDao();
        todoListItemDao.insertAll(todos);
    }

    @Test
    public void testDisplayExhibits() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.recyclerView;

            RecyclerView.ViewHolder firstVH = recyclerView.findViewHolderForAdapterPosition(0);
            TextView exhibit =
                    firstVH.itemView.findViewById(R.id.exhibit_item_text);
            assertNotNull(firstVH);
            assertEquals("Alligators", exhibit.getText().toString());
        });
    }

    @Test
    public void testExhibitExists() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.recyclerView;
            RecyclerView.ViewHolder firstVH = recyclerView.findViewHolderForAdapterPosition(0);
            assertNotNull(firstVH);

            AutoCompleteTextView searchBar =
                    activity.findViewById(R.id.searchBar);
            Button searchButton = activity.findViewById(R.id.searchButton);

            searchBar.setText("G");
            searchButton.performClick();
            firstVH = recyclerView.findViewHolderForAdapterPosition(0);
            assertNotNull(firstVH);

//            TextView exhibit =
//                    firstVH.itemView.findViewById(R.id.exhibit_item_text);
//            assertEquals("Gorillas", exhibit.getText().toString());
        });
    }

    @Test public void testExhibitNotExist() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.recyclerView;
            RecyclerView.ViewHolder firstVH = recyclerView.findViewHolderForAdapterPosition(0);
            assertNotNull(firstVH);
        });
    }
}
