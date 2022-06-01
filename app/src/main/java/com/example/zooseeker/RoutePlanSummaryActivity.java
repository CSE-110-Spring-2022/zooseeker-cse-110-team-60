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

/**
 * Activity to display route plan summary. Contains buttons to go back to
 * search and advance to directions. Has a button to inject mock location and
 * update the RecyclerView if needed.
 *
 * Displays a summary of the planned route in the following format:
 * A to B (X meters)
 * B to C (Y meters)
 */
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

        /* Adapter Setup */
        {
            adapter = new RoutePlanSummaryAdapter();
            adapter.setHasStableIds(true);
        }

        /* RecyclerView Setup */
        {
            recyclerView = findViewById(R.id.summary_RecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        }

        /* Views Setup */
        {
            mockLocation = findViewById(R.id.summary_enter_mock_location);
            mockButton = findViewById(R.id.summary_mockButton);
            mockLatitude = findViewById(R.id.summary_mock_lat);
            mockLongitude = findViewById(R.id.summary_mock_lng);
            enterMockLocation = findViewById(R.id.summary_mock_mock);
        }

        adapter.setDirectionItems(DirectionTracker.getRoutePlanSummary());

        mockButton.setOnClickListener(this::mockClicked);
        enterMockLocation.setOnClickListener(this::enterMockLocationClicked);
    }

    /**
     * Called when 'back' button is clicked. Intent finishes and returns back
     * to MainActivity.
     * @param view
     */
    public void onBackClicked(View view) {
        finish();
    }

    /**
     * Called when 'go' button is clicked. Starts DirectionActivity and
     * finishes intent.
     * @param view
     */
    public void onGoClicked(View view) {
        Intent directionIntent = new Intent(this, DirectionActivity.class);
        startActivity(directionIntent);
        finish();
    }

    /**
     * Called when 'mock' button is clicked. Displays an alert to confirm
     * whether the user wants to manually inject their current location. If
     * yes, then option to enter latitude and longitude is shown.
     * @param view
     */
    void mockClicked(View view) {
        AlertUtilities alert = new AlertUtilities(this, response -> {
            if (response) {
                GPSTracker.manualLocation = true;
                mockLocation.setVisibility(View.VISIBLE);
            }
        });
        alert.showAlert("Do you want to set your current location manually?", "Yes", "No");
    }

    /**
     * Called when 'mock location' is clicked. After user selects to inject
     * their entered location, directions are updated and displayed on the
     * screen.
     * @param view
     */
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