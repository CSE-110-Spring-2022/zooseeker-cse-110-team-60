package com.example.zooseeker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ExhibitListAdapter adapter = new ExhibitListAdapter();
        adapter.setHasStableIds(true);

        RecyclerView recyclerView = findViewById(R.id.rvExhibits);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        List<ExhibitItem> testList = new ArrayList<ExhibitItem>();  // temporary test
        List<String> testTags = new ArrayList<String>();
        testTags.add("testTag1");
        testTags.add("testTag2");
        ExhibitItem testItem1 = new ExhibitItem(1, "TestName2",testTags);
        ExhibitItem testItem2 = new ExhibitItem(2, "TestName2", testTags);
        testList.add(testItem1);
        testList.add(testItem2);
        adapter.setExhibitListItems(testList); // will be loadJSON in the future
    }
}