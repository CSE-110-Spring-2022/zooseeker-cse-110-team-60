package com.example.zooseeker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
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

        header = findViewById(R.id.header_txt);
        body = findViewById(R.id.body_txt);
        nextButton = findViewById(R.id.next_btn);
        previousButton = findViewById(R.id.previous_btn);
        exitButton = findViewById(R.id.exit_btn);
        i = 0;

        setDirection(i);

        nextButton.setOnClickListener(this::nextClicked);
        previousButton.setOnClickListener(this::previousClicked);
    }

    void nextClicked(View view) {
        if (this.i == DirectionTracker.directions.size()) {
            Utilities.showAlert(this, "No More Directions!");
            return;
        }

        Log.d("directions", "next");
        this.i++;
        setDirection(i);
    }

    void previousClicked(View view) {
        if (this.i == 0) {
            Utilities.showAlert(this, "This is the First Direction!");
            return;
        }

        this.i--;
        setDirection(i);
    }

    void setDirection(int i) {
        header.setText(DirectionTracker.directions.get(i).getStart() + " to " + DirectionTracker.directions.get(i).getEnd());

        String directionsString = "";
        List<String> steps = DirectionTracker.directions.get(i).getSteps();
        for (int j = 0; j < steps.size(); ++j) {
            directionsString += steps.get(j) + "\n";
        }

        body.setText(directionsString);
    }
}