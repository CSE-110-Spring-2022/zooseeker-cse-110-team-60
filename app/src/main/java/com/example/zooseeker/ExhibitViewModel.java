package com.example.zooseeker;

import android.app.Application;
import android.content.Context;

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

    public LiveData<List<ExhibitItem>> getExhibitItems() {
        if (exhibitItems == null) {
            loadUsers();
        }
        return exhibitItems;
    }

    private void loadUsers() {
        exhibitItems = exhibitItemDao.getAllLive();
    }

    public void toggleAdded(ExhibitItem exhibitItem) {
        exhibitItem.added = !exhibitItem.added;
        exhibitItemDao.update(exhibitItem); // TODO
    }
}
