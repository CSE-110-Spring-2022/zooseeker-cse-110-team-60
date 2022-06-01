package com.example.zooseeker;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DirectionActivity extends AppCompatActivity implements DirectionObserver {
    public TextView header;
//    public TextView body;
    public Button nextButton;
    public Button previousButton;
    public Button exitButton;
    public Button skipButton;
    public Button toggleButton;
    public Button mockButton;
    public LinearLayout mockLocation;
    public EditText mockLatitude;
    public EditText mockLongitude;
    public Button enterMockLocation;
    public int i;
    public boolean detailed = false;

    public RecyclerView recyclerView;
    public DirectionAdapter adapter;

    // for redirect testing
    public String currentId;

//    private GPSTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        MainActivity.gpsTracker.updateContext(this);

        adapter = new DirectionAdapter();
        adapter.setHasStableIds(true);

        recyclerView = findViewById(R.id.direction_reycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        header = findViewById(R.id.direction_header_textView);
//        body = findViewById(R.id.direction_body_textView);
        nextButton = findViewById(R.id.direction_next_button);
        previousButton = findViewById(R.id.direction_previous_button);
        exitButton = findViewById(R.id.direction_exit_button);
        skipButton = findViewById(R.id.direction_skip_button);
        toggleButton = findViewById(R.id.direction_toggle_button);
        mockLocation = findViewById(R.id.direction_enter_mock_location);
        mockButton = findViewById(R.id.direction_mockButton);
        mockLatitude = findViewById(R.id.direction_mock_lat);
        mockLongitude = findViewById(R.id.direction_mock_lng);
        enterMockLocation = findViewById(R.id.direction_mock_mock);

        i = 0;

        setDirection();

        DirectionTracker.register(this);

        nextButton.setOnClickListener(this::nextClicked);
        previousButton.setOnClickListener(this::previousClicked);
        exitButton.setOnClickListener(this::exitClicked);
        skipButton.setOnClickListener(this::skipClicked);
        toggleButton.setOnClickListener(this::toggleClicked);
        mockButton.setOnClickListener(this::mockClicked);
        enterMockLocation.setOnClickListener(this::enterMockLocationClicked);

        DirectionTracker.loadDatabaseAndDaoByContext(this);

        // for redirect testing
        currentId = DirectionTracker.getGateId();
    }

    /*
     *   Name:       nextClicked
     *   Behavior:   When the next button is clicked, if the current direction is the
     * last one,
     *               display a message notifying there are no more directions.
     * Otherwise, increment the
     *               index of the current direction i and set the direction.
     *   @param      View     view       the view being called from
     *   @return
     */
    void nextClicked(View view) {
        if (DirectionTracker.index == DirectionTracker.currentExhibitIdsOrder.size() - 1) {
            Utilities.showAlert(this, "No More Directions!", "Ok", "Cancel");
            return;
        }

        DirectionTracker.next();
        // if current location == current index, next again
//        setDirection();
    }

    /*
     *   Name:       previousClicked
     *   Behavior:   When the previous button is clicked, if the current direction is
     * the first one,
     *               display a message notifying the user this is the case. Otherwise,
     * decrement the
     *               index of the current direction i and set the direction.
     *   @param      View     view       the view being called from
     *   @return
     */
    void previousClicked(View view) {
        if (DirectionTracker.index == 0) {
            Utilities.showAlert(this, "This is the First Direction!", "Ok", "Cancel");
            return;
        }

        DirectionTracker.previous();
//        setDirection();
    }

    /**
     * Name:     exitClicked
     * Behavior: When the exit button is clicked, a popup alert is displayed to confirm.
     * If "yes" is clicked, exit current path, remove all selected, and return to search.
     * Else, stay on directions.
     *
     * @param view the view being called from
     */
    void exitClicked(View view) {
        DialogInterface.OnClickListener dialog = (dialogInterface, i) -> {
            switch (i) {
                // "Yes" button clicked
                case DialogInterface.BUTTON_POSITIVE:
                    ExhibitList.clearCheckedExhibits();
                    finish();
                    break;

                // "No" button clicked
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
               .setPositiveButton("Yes", dialog).setNegativeButton("No", dialog).show();
    }

    void skipClicked(View view) {
        if (DirectionTracker.index == DirectionTracker.currentExhibitIdsOrder.size()) {
            Utilities.showAlert(this, "Last direction, can't skip!", "Ok", "Cancel");
            return;
        }

        DialogInterface.OnClickListener dialog = (dialogInterface, i) -> {
            switch (i) {
                // "Yes" button clicked
                case DialogInterface.BUTTON_POSITIVE:
                    DirectionTracker.skip(GPSTracker.findNearestNode(GPSTracker.latitude, GPSTracker.longitude)); // change to from current location
                    setDirection();
                    break;

                // "No" button clicked
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to skip?")
                .setPositiveButton("Yes", dialog).setNegativeButton("No", dialog).show();
    }

    void toggleClicked(View view) {
        if (detailed) {
            detailed = false;
            toggleButton.setText("DETAILED");
        } else {
            detailed = true;
            toggleButton.setText("BRIEF");
        }

        setDirection();
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

        MainActivity.gpsTracker.offTrack();
    }

    /**
     *   Name:       setDirection
     *   Behavior:   Update the header and body to reflect the details of the current
     * direction.
     */
    void setDirection() {
//        String currentLocationId = getCurrentLocationId();
//        Direction currentDirection = getDirection(currentLocationId);

        String currentNodeId;
        if (DirectionTracker.index == 0) currentNodeId = "entrance_exit_gate";
        else {
            String currentExhibitId = DirectionTracker.currentExhibitIdsOrder.get(DirectionTracker.index - 1);
            currentNodeId = DirectionTracker.getParentNodeIfExists(DirectionTracker.getDao().get(currentExhibitId)).id;
        }

        Direction currentDirection = DirectionTracker.currentDirection; // = DirectionTracker.getDirection(currentNodeId);

        header.setText(currentDirection.getStart() + " to " + currentDirection.getEnd() + "\n(" + currentDirection.getDistance() + " feet)");

//        String directionsString = "";
//        List<String> steps = currentDirection.get();
//        for (int j = 0; j < steps.size(); ++j) {
//            directionsString += steps.get(j) + "\n";
//        }

//        body.setText(directionsString);
        List<String> steps;
        if (detailed) steps = new ArrayList<String>(currentDirection.getDetailedDirections());
        else steps = new ArrayList<String>(currentDirection.getBriefDirections());

        adapter.setDirections(steps);

        // for redirect testing
        currentId = currentNodeId;
    }

    @Override
    public void updateDirection(Direction direction) {

Log.d("MOCK updated directions, setting new directions, updating screen", "***");

        setDirection();
    }

    @Override
    public void updateOrder(List<String> exhibitIds) {}
}