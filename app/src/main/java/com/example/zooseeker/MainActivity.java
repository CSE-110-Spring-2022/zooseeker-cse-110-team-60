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
    private Button clearBtn;

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
        clearBtn = findViewById(R.id.clearExhibitsBtn);

        setNumPlanned();

        searchBtn.setOnClickListener(this::searchExhibit);
        clearBtn.setOnClickListener(this::uncheckList);
    }

    private void searchExhibit(View view) {
        String search = searchBar.getText().toString();
        if (search.equals("")) {
            Utilities.showAlert(this, "Please enter a valid exhibit!");
            return;
        }
        List<ExhibitItem> searchLists = ExhibitList.getSearchItems(search);
        adapter.setExhibitListItems(searchLists);
        searchBar.setText("");
    }

    private void uncheckList(View view) {
        uncheck();
        setNumPlanned();
    }

    public static MainActivity getInstance() {
        return main;
    }

    public List<ExhibitItem> getExhibits() {
        return viewModel.getAllExhibits();
    }

    private void setNumPlanned() {
        numPlanned.setText(
                "Number of Planned Exhibits: " + ExhibitList.getNumChecked());
    }

    public void uncheck() {
        List<ExhibitItem> checkedExhibits = ExhibitList.getCheckedExhibits();
        for (ExhibitItem item : checkedExhibits) {
            viewModel.uncheckList(item);
        }
    }
}