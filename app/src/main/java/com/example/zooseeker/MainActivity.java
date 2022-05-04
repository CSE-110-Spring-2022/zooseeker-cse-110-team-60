package com.example.zooseeker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public  RecyclerView       recyclerView;
    private ExhibitViewModel   viewModel;
    private ExhibitListAdapter adapter;

    private AutoCompleteTextView searchBar;
    private Button               searchBtn;
    private TextView             numPlanned;

    @SuppressLint("StaticFieldLeak") static MainActivity main;

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
        numPlanned = findViewById(R.id.counter);

        searchBtn.setOnClickListener(this::searchClicked);
        numPlanned.setText(
                "Number of Planned Exhibits: " + ExhibitList.getNumChecked());
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

    static MainActivity getInstance() {
        return main;
    }

    List<ExhibitItem> getExhibits() {
        return viewModel.getAllExhibits();
    }
}