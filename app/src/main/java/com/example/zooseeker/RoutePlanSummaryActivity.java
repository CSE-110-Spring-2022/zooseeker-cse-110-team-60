package com.example.zooseeker;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RoutePlanSummaryActivity extends AppCompatActivity {
    // Testing
    public RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_plan_summary);

        RoutePlanSummaryAdapter adapter = new RoutePlanSummaryAdapter();
        adapter.setHasStableIds(true);

        recyclerView = findViewById(R.id.direction_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // NEED TO PASS DIRECTIONS INTO HERE FROM MAIN
        adapter.setDirectionItems();
    }
}