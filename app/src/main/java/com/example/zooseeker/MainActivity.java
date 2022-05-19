package com.example.zooseeker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import org.jgrapht.Graph;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private final PermissionChecker permissionChecker = new PermissionChecker(this);

    public RecyclerView recyclerView;
    private ExhibitViewModel viewModel;
    private ExhibitListAdapter adapter;

    private AutoCompleteTextView searchBar;
    private TextView deleteSearchBtn;
    private Button searchBtn;
    private TextView numPlanned;
    private Button clearBtn;
    private Button showCheckedBtn;
    private Button returnToSearchBtn;
    private Button  getDirectionsBtn;

    public static boolean update = true;

    @SuppressLint("StaticFieldLeak")
    static MainActivity main;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = this;
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(ExhibitViewModel.class);

        adapter = new ExhibitListAdapter();
        adapter.setHasStableIds(true);
        adapter.setOnCheckBoxClickedHandler(viewModel::toggleAdded);
        viewModel.getExhibitItems().observe(this, adapter::setExhibitListItems);

        recyclerView = findViewById(R.id.main_exhibits_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        searchBar = findViewById(R.id.main_search_textView);
        deleteSearchBtn = findViewById(R.id.main_delete_button);
        searchBtn = findViewById(R.id.main_search_button);
        numPlanned = findViewById(R.id.counter);
        clearBtn = findViewById(R.id.main_clear_button);
        showCheckedBtn = findViewById(R.id.showCheckedBtn);
        returnToSearchBtn = findViewById(R.id.main_show_and_back_button);
        getDirectionsBtn = findViewById(R.id.main_getDirections_button);

        setNumPlanned();

        if (permissionChecker.ensurePermissions()) return;


        String provider = LocationManager.GPS_PROVIDER;
        LocationManager locationManager =
                (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                // TODO
            }
        };
        locationManager.requestLocationUpdates(provider, 0, 0f, locationListener);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1,
                                          int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence != null) {
                    if (charSequence.length() != 0) {
                        update = true;
                        displaySearch(String.valueOf(charSequence));
                    }
                    else {
                        update = true;
                        displayAll();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        deleteSearchBtn.setOnClickListener(this::deleteSearch);
        searchBtn.setOnClickListener(this::searchExhibit);
        showCheckedBtn.setOnClickListener(this::showChecked);
        returnToSearchBtn.setOnClickListener(this::returnToSearch);
        clearBtn.setOnClickListener(this::uncheckList);
        getDirectionsBtn.setOnClickListener(this::getDirectionsClicked);
    }

    private void deleteSearch(View view) {
        update = true;
        searchBar.getText().clear();
        displayAll();
    }

    private void searchExhibit(View view) {
        update = true;
        String search = searchBar.getText().toString();
        if (search.equals("")) {
            Utilities.showAlert(this, "Please enter a valid exhibit!");
            return;
        }
        displaySearch(search);
    }


    private void uncheckList(View view) {
        update = true;
        uncheck();
        setNumPlanned();
        displayAll();
    }

    private void showChecked(View view) {
        update = true;
        List<ExhibitItem> checkedExhibits = ExhibitList.getCheckedExhibits();
        adapter.setExhibitListItems(checkedExhibits);
        showCheckedBtn.setVisibility(View.INVISIBLE);
        returnToSearchBtn.setVisibility(View.VISIBLE);
    }

    // after "clear" showing the entire list, if the search bar is not empty, checking any exhibit,
    // "show" and then "back" return all search items following the string in the search bar
    private void returnToSearch(View view) {
        update = true;
        displaySearch();
        returnToSearchBtn.setVisibility(View.INVISIBLE);
        showCheckedBtn.setVisibility(View.VISIBLE);
    }

    public static MainActivity getInstance() {
        return main;
    }

    void getDirectionsClicked(View view) {
        List<ExhibitItem> toVisit = ExhibitList.getCheckedExhibits();

        if (toVisit.size() == 0) {
            Utilities.showAlert(this, "Select Exhibit(s) Before Continuing!");
            return;
        }

        Graph<String, IdentifiedWeightedEdge> g = ZooData.loadZooGraphJSON(this, "sample_zoo_graph.JSON");
        Map<String, ZooData.VertexInfo> vInfo = ZooData.loadVertexInfoJSON(this,"sample_node_info.JSON");
        Map<String, ZooData.EdgeInfo> eInfo = ZooData.loadEdgeInfoJSON(this,"sample_edge_info.JSON");

        DirectionTracker dt = new DirectionTracker(g, vInfo, eInfo);
        dt.getDirections(toVisit);

        Intent directionIntent = new Intent(this, DirectionActivity.class);
        startActivity(directionIntent);
    }

    public List<ExhibitItem> getExhibits() {
        return viewModel.getAllExhibits();
    }

    @SuppressLint("SetTextI18n")
    private void setNumPlanned() {
        numPlanned.setText("Planned " + ExhibitList.getNumChecked() + " Exhibit(s)");
    }

    public void uncheck() {
        List<ExhibitItem> checkedExhibits = ExhibitList.getCheckedExhibits();
        for (ExhibitItem item : checkedExhibits) {
            viewModel.uncheckList(item);
        }
        setNumPlanned();
    }

    private void displayAll() {
        List<ExhibitItem> allExhibits = ExhibitList.getAllExhibits();
        adapter.setExhibitListItems(allExhibits);
    }

    public void displaySearch() {
        String search = searchBar.getText().toString();
        if (search.equals("")) {
            displayAll();
        }
        else {
            displaySearch(search);
        }
    }

    private void displaySearch(String search) {
        List<ExhibitItem> searchLists = ExhibitList.getSearchItems(search);
        adapter.setExhibitListItems(searchLists);
    }
}
