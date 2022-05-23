package com.example.zooseeker;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ExhibitViewModel extends AndroidViewModel {
    private LiveData<List<Node>> exhibits;
    private final NodeDao nodeDao;

    public ExhibitViewModel(@NonNull Application application) {
        super(application);
        Context context = getApplication().getApplicationContext();
        NodeDatabase db = NodeDatabase.getSingleton(context);
        nodeDao = db.nodeDao();
    }

    public List<Node> getAllNodes() {
        return nodeDao.getAll();
    }

    public List<Node> getAllExhibits() {
        return nodeDao.getAllExhibits();
    }

    public LiveData<List<Node>> getAllExhibitsLive() {
        if (exhibits == null) {
            loadDB();
        }
        return exhibits;
    }

    private void loadDB() {
        exhibits = nodeDao.getAllExhibitsLive();
    }

    @SuppressLint("SetTextI18n")
    public void toggleCheckbox(Node exhibit) {
        exhibit.added = !exhibit.added;
        nodeDao.update(exhibit);

        MainActivity main = MainActivity.getInstance();
        TextView numPlanned = main.findViewById(R.id.counter);
        numPlanned.setText("Planned " + ExhibitList.getNumChecked() + " Exhibit(s)");

        MainActivity.update = false;
    }

    public void uncheckExhibit(Node exhibit) {
        exhibit.added = !exhibit.added;
        nodeDao.update(exhibit);
    }
}