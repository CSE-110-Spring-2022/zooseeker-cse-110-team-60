package com.example.zooseeker; //

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ExhibitViewModel extends AndroidViewModel {
    private       LiveData<List<ExhibitItem>> exhibitItems;
    private final ExhibitItemDao              exhibitItemDao;

    public ExhibitViewModel(@NonNull Application application) {
        super(application);
        Context         context = getApplication().getApplicationContext();
        ExhibitDatabase db      = ExhibitDatabase.getSingleton(context);
        exhibitItemDao = db.exhibitItemDao();
    }

    public List<ExhibitItem> getAllExhibits() {
        return exhibitItemDao.getAll();
    }

    public LiveData<List<ExhibitItem>> getExhibitItems() {
        if (exhibitItems == null) {
            loadUsers();
        }
        return exhibitItems;
    }

    private void loadUsers() {
        exhibitItems = exhibitItemDao.getAllLive();
    }

    @SuppressLint("SetTextI18n")
    public void toggleAdded(ExhibitItem exhibitItem) {
        exhibitItem.added = !exhibitItem.added;
        exhibitItemDao.update(exhibitItem);

        MainActivity main       = MainActivity.getInstance();
        TextView     numPlanned = main.findViewById(R.id.counter);
        numPlanned.setText(
                "Planned " + ExhibitList.getNumChecked() + " Exhibit(s)");
    }

    public void uncheckList(ExhibitItem exhibitItem) {
        exhibitItem.added = !exhibitItem.added;
        exhibitItemDao.update(exhibitItem);
    }
}