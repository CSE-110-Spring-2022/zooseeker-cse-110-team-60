package com.example.zooseeker;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class DirectionActivity extends AppCompatActivity {
    public TextView header;
    public TextView body;
    public Button   nextButton;
    public Button previousButton;
    public Button   exitButton;
    public int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        header = findViewById(R.id.direction_header_textView);
        body = findViewById(R.id.direction_body_textView);
        nextButton = findViewById(R.id.direction_next_button);
        previousButton = findViewById(R.id.direction_previous_button);
        exitButton = findViewById(R.id.direction_exit_button);
        i = 0;

        setDirection();

        nextButton.setOnClickListener(this::nextClicked);
        previousButton.setOnClickListener(this::previousClicked);
        exitButton.setOnClickListener(this::exitClicked);

        DirectionTracker.loadDatabaseAndDaoByContext(this);
    }

    /*
    *   Name:       nextClicked
    *   Behavior:   When the next button is clicked, if the current direction is the last one,
    *               display a message notifying there are no more directions. Otherwise, increment the
    *               index of the current direction i and set the direction.
    *   @param      View     view       the view being called from
    *   @return
     */
    void nextClicked(View view) {
        if (DirectionTracker.index == DirectionTracker.currentExhibitsOrder.size()) {
            Utilities.showAlert(this, "No More Directions!");
            return;
        }

        DirectionTracker.next();
        // if current location == current index, next again
        setDirection();
    }

    /*
     *   Name:       previousClicked
     *   Behavior:   When the previous button is clicked, if the current direction is the first one,
     *               display a message notifying the user this is the case. Otherwise, decrement the
     *               index of the current direction i and set the direction.
     *   @param      View     view       the view being called from
     *   @return
     */
    void previousClicked(View view) {
        if (DirectionTracker.index == 0) {
            Utilities.showAlert(this, "This is the First Direction!");
            return;
        }

        DirectionTracker.previous();
        setDirection();
    }

    /**
     * Name:     exitClicked
     * Behavior: When the exit button is clicked, a popup alert is displayed to confirm.
     *           If "yes" is clicked, exit current path, remove all selected, and return to search.
     *           Else, stay on directions.
     *
     * @param    view the view being called from
     */
    void exitClicked(View view) {
        DialogInterface.OnClickListener dialog = (dialogInterface, i) -> {
            switch(i) {
                // "Yes" button clicked
                case DialogInterface.BUTTON_POSITIVE:
                    finish();
                    break;

                // "No" button clicked
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to exit your current path?")
                .setPositiveButton("Yes", dialog)
                .setNegativeButton("No", dialog)
                .show();

//        DirectionTracker.redirect("arctic_foxes");
//        setDirection();
    }

    /*
     *   Name:       setDirection
     *   Behavior:   Update the header and body to reflect the details of the current direction.
     *   @param      View     view       the view being called from
     *   @return
     */
    void setDirection() {

        // String currentLocationId = getCurrentLocationId()
        // Direction currentDirection = getDirection(currentLocationId)

        String currentNodeId;
        if (DirectionTracker.index == 0) currentNodeId = "entrance_exit_gate";
        else currentNodeId = DirectionTracker.currentExhibitsOrder.get(DirectionTracker.index - 1).id;
        Direction currentDirection = DirectionTracker.getDirection(currentNodeId);

        header.setText(currentDirection.getStart() + " to " + currentDirection.getEnd() + " (" + currentDirection.getDistance() + "m)");

        String directionsString = "";
        List<String> steps = currentDirection.getSteps();
        for (int j = 0; j < steps.size(); ++j) {
            directionsString += steps.get(j) + "\n";
        }

        body.setText(directionsString);
    }
}