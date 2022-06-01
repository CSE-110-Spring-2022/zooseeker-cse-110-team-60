/**
 * Displays a summary of the planned route in the following format:
 *  A to B (X meters)
 *  B to C (Y meters)
 */

package com.example.zooseeker;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RoutePlanSummaryActivity extends AppCompatActivity {
    // Testing
    public RecyclerView recyclerView;
    private RoutePlanSummaryAdapter adapter;

    public Button mockButton;
    public LinearLayout mockLocation;
    public EditText mockLatitude;
    public EditText mockLongitude;
    public Button enterMockLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_plan_summary);

        adapter = new RoutePlanSummaryAdapter();
        adapter.setHasStableIds(true);

        recyclerView = findViewById(R.id.summary_RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        mockLocation = findViewById(R.id.summary_enter_mock_location);
        mockButton = findViewById(R.id.summary_mockButton);
        mockLatitude = findViewById(R.id.summary_mock_lat);
        mockLongitude = findViewById(R.id.summary_mock_lng);
        enterMockLocation = findViewById(R.id.summary_mock_mock);

        adapter.setDirectionItems(DirectionTracker.getRoutePlanSummary());

        mockButton.setOnClickListener(this::mockClicked);
        enterMockLocation.setOnClickListener(this::enterMockLocationClicked);
    }

    public void onBackClicked(View view) {
        finish();
    }

    public void onGoClicked(View view) {
        Intent directionIntent = new Intent(this, DirectionActivity.class);
        startActivity(directionIntent);
        finish();
    }

    void mockClicked(View view) {
        AlertUtilities alert = new AlertUtilities(this, response -> {
            if (response) {
                GPSTracker.manualLocation = true;
                mockLocation.setVisibility(View.VISIBLE);
            }
        });
        alert.showAlert("Do you want to set your current location manually?", "Yes", "No");
    }

    void enterMockLocationClicked(View view) {

        if (!mockLatitude.getText().toString().equals("")) {
            double latitude = Double.parseDouble(mockLatitude.getText().toString());

            if (-90 <= latitude && latitude <= 90) {
                GPSTracker.latitude = Double.parseDouble(mockLatitude.getText().toString());
            }
            else {
                Utilities.showAlert(this, "Please enter a valid latitude!", "Ok", "Cancel");
                return;
            }
        }
        else {
            Utilities.showAlert(this, "Please enter a valid latitude!", "Ok", "Cancel");
            return;
        }

        if (!mockLongitude.getText().toString().equals("")) {
            double longitude = Double.parseDouble(mockLongitude.getText().toString());

            if (-180 <= longitude && longitude <= 180) {
                GPSTracker.longitude = Double.parseDouble(mockLongitude.getText().toString());
            }
            else {
                Utilities.showAlert(this, "Please enter a valid longitude!", "Ok", "Cancel");
                return;
            }
        }
        else {
            Utilities.showAlert(this, "Please enter a valid longitude!", "Ok", "Cancel");
            return;
        }

        Log.d("MOCK manual latitude", String.valueOf(GPSTracker.latitude));
        Log.d("MOCK manual longitude", String.valueOf(GPSTracker.longitude));

        mockLocation.setVisibility(View.INVISIBLE);
        mockLatitude.getText().clear();
        mockLongitude.getText().clear();

        Log.d("MOCK calling offTrack", "***");

        List<Node> exhibitsToVisit = ExhibitList.getCheckedExhibits();

        String startNodeId = GPSTracker.findNearestNode(GPSTracker.latitude, GPSTracker.longitude);
        DirectionTracker.initDirections(startNodeId, exhibitsToVisit);

        DirectionTracker.getDirection(startNodeId);

        adapter.setDirectionItems(DirectionTracker.getRoutePlanSummary());
    }
}