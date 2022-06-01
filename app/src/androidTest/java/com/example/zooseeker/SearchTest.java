package com.example.zooseeker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;
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

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class SearchTest {
    NodeDatabase testDb;
    NodeDao todoListItemDao;

    private static void forceLayout(RecyclerView recyclerView) {
        recyclerView.measure(View.MeasureSpec.UNSPECIFIED,
                             View.MeasureSpec.UNSPECIFIED);
        recyclerView.layout(0, 0, 1080, 2280);
    }

    @Before
    public void resetDatabase() {
        Context context = ApplicationProvider.getApplicationContext();
        testDb = Room.inMemoryDatabaseBuilder(context, NodeDatabase.class)
                     .allowMainThreadQueries()
                     .build();
        NodeDatabase.injectTestDatabase(testDb);

        List<Node>
                todos = Node.loadJSON(context, "exhibit_info.json");
        todoListItemDao = testDb.nodeDao();
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
            assert firstVH != null;
            TextView exhibit =
                    firstVH.itemView.findViewById(R.id.node_name);
            assertNotNull(firstVH);
            assertEquals("Alligators", exhibit.getText().toString());
        });
    }

    @Test
    public void testExhibitNotExist() { // 2nd testing
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.recyclerView;
            AutoCompleteTextView searchBar =
                    activity.findViewById(R.id.main_search_textView);
            Button searchButton = activity.findViewById(R.id.main_search_button);

            searchBar.setText("HELLO");
            searchButton.performClick();

            RecyclerView.ViewHolder firstVH = recyclerView.findViewHolderForAdapterPosition(0);
            assertNull(firstVH);
        });
    }
}

/*
@Test
    public void testExhibitExists() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.recyclerView;
            AutoCompleteTextView searchBar =
                    activity.findViewById(R.id.searchBar);
            Button searchButton = activity.findViewById(R.id.searchButton);

            searchBar.setText("G");
            searchButton.performClick();

            RecyclerView.ViewHolder firstVH = recyclerView.findViewHolderForAdapterPosition(0);
            assertNotNull(firstVH);

//            TextView exhibit =
//                    firstVH.itemView.findViewById(R.id.exhibit_item_text);
//            assertEquals("Gorillas", exhibit.getText().toString());
        });
    }
 */