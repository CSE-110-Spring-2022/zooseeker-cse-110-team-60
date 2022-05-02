package com.example.zooseeker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public  RecyclerView     recyclerView;
    private ExhibitViewModel     viewModel;
    private ExhibitListAdapter adapter;
    private AutoCompleteTextView searchBar;
    private Button               searchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*List<ExhibitItem> list = ExhibitItem.loadJSON(this,
                                                       "sample_node_info" +
                                                            ".JSON");
        Log.d("MainActivity", list.toString()); */

        viewModel = new ViewModelProvider(this).get(ExhibitViewModel.class);

        adapter = new ExhibitListAdapter();
        adapter.setHasStableIds(true);
        adapter.setOnCheckBoxClickedHandler(viewModel::toggleAdded);
        viewModel.getExhibitItems().observe(this,
                                            adapter::setExhibitListItems);
        recyclerView = findViewById(R.id.rvExhibits);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        this.searchBar = this.findViewById(R.id.searchBar);
        this.searchBtn = this.findViewById(R.id.searchButton);

        searchBtn.setOnClickListener(this::searchClicked);
    }

    void searchClicked(View view) {
        String search = searchBar.getText().toString();
        List<ExhibitItem> searchLists = ExhibitItem.getSearchItems(this,
                                                                   "sample_node_info" +
                                                                   ".JSON",
                                                                   search);
        adapter.setExhibitListItems(searchLists);
        searchBar.setText("");
    }
}