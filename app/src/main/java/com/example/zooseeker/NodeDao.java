package com.example.zooseeker;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NodeDao {
    @Insert
    List<Long> insertAll(List<Node> nodes);

    @Query("SELECT * FROM `node_list` WHERE `id`=:id")
    Node get(String id);

    @Query("SELECT * FROM `node_list` ORDER BY `name`")
    List<Node> getAll();

    @Query("SELECT * FROM `node_list` WHERE `kind`='EXHIBIT' ORDER BY `name`")
    List<Node> getAllExhibits();

    @Query("SELECT * FROM `node_list` WHERE `kind`='GROUP' ORDER BY `name`")
    List<Node> getAllExhibitGroups();

    @Query("SELECT * FROM `node_list` WHERE `kind`='INTERSECTION' ORDER BY `name`")
    List<Node> getAllIntersections();

    @Query("SELECT * FROM `node_list` ORDER BY `name`")
    LiveData<List<Node>> getAllLive();

    @Query("SELECT * FROM `node_list` WHERE `kind`='EXHIBIT' ORDER BY `name`")
    LiveData<List<Node>> getAllExhibitsLive();

    @Update
    int update(Node node);
}