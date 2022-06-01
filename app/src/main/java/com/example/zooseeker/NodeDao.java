package com.example.zooseeker;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Gets Nodes from the database using SQL queries.
 */
@Dao
public interface NodeDao {
    @Insert
    List<Long> insertAll(List<Node> nodes);

    /**
     * Gets Node from the database using its id.
     *
     * @param id Id of Node to be searched.
     *
     * @return Node.
     */
    @Query("SELECT * FROM `node_list` WHERE `id`=:id")
    Node get(String id);

    /**
     * Gets all Nodes from the database.
     *
     * @return List of all nodes.
     */
    @Query("SELECT * FROM `node_list` ORDER BY `name`")
    List<Node> getAll();

    /**
     * Gets Node of kind 'gate' from the database.
     *
     * @return Node of kind 'gate'.
     */
    @Query("SELECT * FROM `node_list` WHERE `kind`='GATE' ORDER BY `name`")
    Node getGate();

    /**
     * Gets all Nodes of kind 'exhibit' from the database.
     *
     * @return List of all exhibits.
     */
    @Query("SELECT * FROM `node_list` WHERE `kind`='EXHIBIT' ORDER BY `name`")
    List<Node> getAllExhibits();

    /**
     * Gets all Nodes of kind 'exhibit' from the database.
     *
     * @return LiveData of list of all exhibits.
     */
    @Query("SELECT * FROM `node_list` WHERE `kind`='EXHIBIT' ORDER BY `name`")
    LiveData<List<Node>> getAllExhibitsLive();

    /**
     * Updates Node in the database.
     *
     * @param node Node to be updated.
     *
     * @return Int.
     */
    @Update
    int update(Node node);
}