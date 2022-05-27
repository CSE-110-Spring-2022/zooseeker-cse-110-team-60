package com.example.zooseeker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import org.jgrapht.Graph;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private final PermissionChecker permissionChecker = new PermissionChecker(this);
    private static final String[] requiredPermissions =
            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                         Manifest.permission.ACCESS_COARSE_LOCATION};

    public GPSTracker gpsTracker;

    public RecyclerView recyclerView;
    private ExhibitViewModel viewModel;
    private ExhibitListAdapter adapter;

    private AutoCompleteTextView searchBar;
    private TextView deleteBtn;
    private Button searchBtn;
    private TextView numPlanned;
    private Button clearBtn;
    private Button showBtn;
    private Button returnBtn;
    private Button directionsBtn;

    public static boolean update = true;

    @SuppressLint("StaticFieldLeak")
    static MainActivity main;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = this;
        setContentView(R.layout.activity_main);

        /* viewModel, adapter, recyclerView Setup */
        {
            viewModel = new ViewModelProvider(this).get(ExhibitViewModel.class);

            adapter = new ExhibitListAdapter();
            adapter.setHasStableIds(true);
            adapter.setOnCheckBoxClickedHandler(viewModel::toggleCheckbox);
            viewModel.getAllExhibitsLive().observe(this, adapter::setExhibitList);

            recyclerView = findViewById(R.id.main_exhibits_recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        }

        /* findViewById Setup */
        {
            searchBar = findViewById(R.id.main_search_textView);
            deleteBtn = findViewById(R.id.main_delete_button);
            searchBtn = findViewById(R.id.main_search_button);
            numPlanned = findViewById(R.id.counter);
            clearBtn = findViewById(R.id.main_clear_button);
            showBtn = findViewById(R.id.showCheckedBtn);
            returnBtn = findViewById(R.id.main_show_and_back_button);
            directionsBtn = findViewById(R.id.main_getDirections_button);
        }

        setNumPlanned();

        /* Location Permissions Setup */
        {
            boolean hasNoLocationPerms = Arrays.stream(requiredPermissions)
                                               .map(perm -> ContextCompat.checkSelfPermission(this, perm))
                                               .allMatch(status -> status == PackageManager.PERMISSION_DENIED);

            if (hasNoLocationPerms) {
                permissionChecker.requestPermissionLauncher.launch(requiredPermissions);
            }
        }

        /* Views Setup */
        {
            searchBar.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1,
                                              int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1,
                                          int i2) {
                    if (charSequence != null) {
                        if (charSequence.length() != 0) {
                            update = true;
                            displaySearchedExhibits(String.valueOf(charSequence));
                            if (returnBtn.getVisibility() == View.VISIBLE) {
                                returnBtn.setVisibility(View.INVISIBLE);
                                showBtn.setVisibility(View.VISIBLE);
                            }
                        }
                        else {
                            update = true;
                            displayAllExhibits();
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {}
            });
            deleteBtn.setOnClickListener(this::deleteClicked);
            searchBtn.setOnClickListener(this::searchClicked);
            showBtn.setOnClickListener(this::showClicked);
            returnBtn.setOnClickListener(this::returnClicked);
            clearBtn.setOnClickListener(this::clearClicked);
            directionsBtn.setOnClickListener(this::getDirectionsClicked);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        update = true;
        setNumPlanned();
        displayAllExhibits();
    }

    private void deleteClicked(View view) {
        update = true;
        searchBar.getText().clear();
        displayAllExhibits();
    }

    private void searchClicked(View view) {
        update = true;
        String search = searchBar.getText().toString();
        if (search.equals("")) {
            Utilities.showAlert(this, "Please enter a valid exhibit!", "Ok", "Cancel");
            return;
        }
        displaySearchedExhibits(search);
    }

    private void showClicked(View view) {
        update = true;
        List<Node> checkedExhibits = ExhibitList.getCheckedExhibits();
        adapter.setExhibitList(checkedExhibits);
        showBtn.setVisibility(View.INVISIBLE);
        returnBtn.setVisibility(View.VISIBLE);
    }

    private void returnClicked(View view) {
        update = true;
        displaySearchedExhibits();
        returnBtn.setVisibility(View.INVISIBLE);
        showBtn.setVisibility(View.VISIBLE);
    }

    private void clearClicked(View view) {
        update = true;
        uncheckExhibits();
        setNumPlanned();
        displayAllExhibits();
    }

    private void getDirectionsClicked(View view) {

        List<Node> toVisit = ExhibitList.getCheckedExhibits();

        if (toVisit.size() == 0) {
            Utilities.showAlert(this, "Select Exhibit(s) Before Continuing!", "Ok", "Cancel");
            return;
        }

        DirectionTracker.loadGraphData(this,"sample_node_info.JSON", "sample_edge_info.JSON", "sample_zoo_graph.JSON");
        DirectionTracker.loadDatabaseAndDaoByContext(this);
        DirectionTracker.initDirections("entrance_exit_gate", toVisit);

        Intent directionIntent = new Intent(this, DirectionActivity.class);
        startActivity(directionIntent);
    }

    public static MainActivity getInstance() {
        return main;
    }

    public List<Node> getAllNodes() {
        return viewModel.getAllNodes();
    }

    public List<Node> getAllExhibits() {
        return viewModel.getAllExhibits();
    }

    @SuppressLint("SetTextI18n")
    private void setNumPlanned() {
        numPlanned.setText("Planned " + ExhibitList.getNumChecked() + " Exhibit(s)");
    }

    public void uncheckExhibits() {
        List<Node> checkedExhibits = ExhibitList.getCheckedExhibits();
        for (Node item : checkedExhibits) {
            viewModel.uncheckExhibit(item);
        }
        setNumPlanned();
    }

    private void displayAllExhibits() {
        List<Node> allExhibits = ExhibitList.getAllExhibits();
        adapter.setExhibitList(allExhibits);
    }

    private void displaySearchedExhibits() {
        String search = searchBar.getText().toString();
        if (search.equals("")) {
            displayAllExhibits();
        }
        else {
            displaySearchedExhibits(search);
        }
    }

    private void displaySearchedExhibits(String search) {
        List<Node> searchLists = ExhibitList.getSearchItems(search);
        adapter.setExhibitList(searchLists);
    }
}
