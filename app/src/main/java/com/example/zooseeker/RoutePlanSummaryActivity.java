/**
 * Displays a summary of the planned route in the following format:
 *  A to B (X meters)
 *  B to C (Y meters)
 */

package com.example.zooseeker;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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

        adapter.setDirectionItems(DirectionTracker.directions);
    }

    public void onBackClicked(View view) {
        finish();
    }

    public void onGoClicked(View view) {
        Intent directionIntent = new Intent(this, DirectionActivity.class);
        startActivity(directionIntent);
    }
}