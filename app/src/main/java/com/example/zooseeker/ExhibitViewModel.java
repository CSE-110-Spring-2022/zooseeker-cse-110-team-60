package com.example.zooseeker;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * ExhibitViewModel is in charge of updating the Dao.
 */
public class ExhibitViewModel extends AndroidViewModel {
    private LiveData<List<Node>> exhibits;
    private final NodeDao nodeDao;

    public ExhibitViewModel(@NonNull Application application) {
        super(application);
        Context context = getApplication().getApplicationContext();
        NodeDatabase db = NodeDatabase.getSingleton(context);
        nodeDao = db.nodeDao();
    }

    public Node getGate() {
        return nodeDao.getGate();
    }

    /**
     * Delegates to nodeDao's getAll method to getAllNodes.
     * @return A List of all Nodes (exhibits, group exhibits, intersections, etc.)
     */
    public List<Node> getAllNodes() {
        return nodeDao.getAll();
    }

    /**
     * Delegates to nodeDao's getAll method to getAllExhibits.
     * @return A List of all Exhibits.
     */
    public List<Node> getAllExhibits() {
        return nodeDao.getAllExhibits();
    }

    /**
     * Delegates to loadDB to load the database.
     * @return LiveData
     */
    public LiveData<List<Node>> getAllExhibitsLive() {
        if (exhibits == null) {
            loadDB();
        }
        return exhibits;
    }

    /**
     * Sets the LiveData to all the live exhibits by delegating to nodeDao's getAllExhibitsLive.
     */
    private void loadDB() {
        exhibits = nodeDao.getAllExhibitsLive();
    }

    /**
     * Updates the counter to reflect the toggle.
     * Updates the database to reflect the toggled exhibit.
     * @param exhibit the Node of type exhibit to be checked/unchecked.
     */
    @SuppressLint("SetTextI18n")
    public void toggleCheckbox(Node exhibit) {
        exhibit.added = !exhibit.added;
        nodeDao.update(exhibit);

        MainActivity main = MainActivity.getInstance();
        TextView numPlanned = main.findViewById(R.id.counter);
        numPlanned.setText("Planned " + ExhibitList.getNumChecked() + " Exhibit(s)");

        MainActivity.update = false;
    }

    /**
     * Sets the exhibit to be deleted's added field to false.
     * Updates the database to reflect the unchecked exhibit.
     * @param exhibit the Node of type exhibit to be deleted.
     */
    public void uncheckExhibit(Node exhibit) {
        exhibit.added = !exhibit.added;
        nodeDao.update(exhibit);
    }
}