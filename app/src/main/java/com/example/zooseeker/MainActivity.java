package com.example.zooseeker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import org.jgrapht.Graph;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public  RecyclerView         recyclerView;
    private ExhibitViewModel     viewModel;
    private ExhibitListAdapter   adapter;
    private AutoCompleteTextView searchBar;
    private Button   searchBtn;
    private Button   getDirectionsBtn;
    private TextView numPlanned;

    static MainActivity main;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = this;
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(ExhibitViewModel.class);

        adapter = new ExhibitListAdapter();
        adapter.setHasStableIds(true);
        adapter.setOnCheckBoxClickedHandler(viewModel::toggleAdded);
        viewModel.getExhibitItems().observe(this, adapter::setExhibitListItems);

        recyclerView = findViewById(R.id.rvExhibits);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        searchBar = findViewById(R.id.searchBar);
        searchBtn = findViewById(R.id.searchButton);
        getDirectionsBtn = findViewById(R.id.getDirectionsButton);
        numPlanned = findViewById(R.id.counter);

        searchBtn.setOnClickListener(this::searchClicked);
        getDirectionsBtn.setOnClickListener(this::getDirectionsClicked);

        numPlanned.setText("Number of Planned Exhibits: " + ExhibitList.getNumChecked());
    }

    void searchClicked(View view) {
        String search = searchBar.getText().toString();
        if (search.equals("")) {
            Utilities.showAlert(this, "Please enter a valid exhibit!");
            return;
        }
        List<ExhibitItem> searchLists = ExhibitList.getSearchItems(search);
        adapter.setExhibitListItems(searchLists);
        searchBar.setText("");
    }

    void getDirectionsClicked(View view) {
        List<ExhibitItem> toVisit = ExhibitList.getCheckedExhibits();
        Graph<String, IdentifiedWeightedEdge> g = ZooData.loadZooGraphJSON(this, "sample_zoo_graph.JSON");
        Map<String, ZooData.VertexInfo> vInfo = ZooData.loadVertexInfoJSON(this,"sample_node_info.JSON");
        Map<String, ZooData.EdgeInfo> eInfo = ZooData.loadEdgeInfoJSON(this,"sample_edge_info.JSON");

        Log.d("toVisit", String.valueOf(toVisit.size()));

        DirectionTracker dt = new DirectionTracker(g, vInfo, eInfo);
        List<Direction> directions = dt.getDirections(toVisit);

        for (int i = 0; i < directions.size(); i++) {
            Log.d("direction " + String.valueOf(i), directions.get(i).toString());
        }
    }

    static MainActivity getInstance() {
        return main;
    }

    List<ExhibitItem> getExhibits() {
        return viewModel.getAllExhibits();
    }
}