package com.example.zooseeker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.widget.AutoCompleteTextView;
import android.widget.Button;

import android.view.View;


import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

public class SearchTest {

    @Test
    public void testExhibitExists() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            AutoCompleteTextView searchBar = activity.findViewById(R.id.searchBar);
            Button searchButton = activity.findViewById(R.id.searchButton);
            RecyclerView recyclerView = activity.findViewById(R.id.rvExhibits);

            searchBar.setText("G");
            searchButton.performClick();

            RecyclerView.ViewHolder firstVH = recyclerView.findViewHolderForAdapterPosition(0);
            assertNotNull(firstVH);
//
//            RecyclerView.ViewHolder firstVH = recyclerView.findViewHolderForAdapterPosition(0);
//            assertNotNull(firstVH);
//            assertEquals("Test", firstVH.itemView.findViewById(R.id.exhibit_item_text).toString());
        });
    }

    @Test public void testExhibitNotExist() {

    }
}
