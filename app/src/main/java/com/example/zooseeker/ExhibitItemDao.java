package com.example.zooseeker;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ExhibitItemDao {
    @Insert
    List<Long> insertAll(List<ExhibitItem> exhibitItemList);

    @Query("SELECT * FROM `exhibit_list_items` WHERE `id`=:id")
    ExhibitItem get(String id);

    @Query("SELECT * FROM `exhibit_list_items` ORDER BY `name`")
    List<ExhibitItem> getAll();

    @Query("SELECT * FROM `exhibit_list_items` ORDER BY `name`")
    LiveData<List<ExhibitItem>> getAllLive();

    @Update
    int update(ExhibitItem exhibitItem);
}


