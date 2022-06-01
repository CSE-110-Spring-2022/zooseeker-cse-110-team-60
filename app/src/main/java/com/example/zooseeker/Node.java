package com.example.zooseeker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity / table in NodeDatabase. Contains id, parentId, kind, name, tags,
 * added (boolean to indicate whether it is checked / selected), latitude, and
 * longitude. Nodes that do not have a parentId or tags have those fields as
 * empty strings.
 */
@Entity(tableName = "node_list")
public class Node {
    @PrimaryKey
    @NonNull
    public String id;
    @NonNull
    public String parentId;
    @NonNull
    public VertexInfo.Kind kind;
    @NonNull
    public String name;
    @NonNull
    public String tags;
    public boolean added;
    public double latitude;
    public double longitude;

    /**
     * Node constructor.
     *
     * @param id        Id of Node.
     * @param parentId  ParentId of Node. Empty if does not exist.
     * @param kind      Kind of Node.
     * @param name      Name of Node.
     * @param tags      Tags of Node. Empty if does not exist. List of Strings
     *                  joined using delimeter ','.
     * @param latitude  Latitude of Node.
     * @param longitude Longitude of Node.
     */
    public Node(@NonNull String id, @NonNull String parentId,
                @NonNull VertexInfo.Kind kind, @NonNull String name,
                @NonNull String tags, double latitude, double longitude) {
        this.id = id;
        this.parentId = parentId;
        this.kind = kind;
        this.name = name;
        this.tags = tags;
        this.added = false;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Overrides toString() for Node.
     *
     * @return String.
     */
    @NonNull
    @Override
    public String toString() {
        return "Node{" + "id=" + id + ", parentId=" + parentId + ", kind=" + kind + ", " + "name=" + name + ", tags=[" + tags + "], added=" + added + ", latitude=" + latitude + ", " + "longitude" + longitude + "}";
    }

    /**
     * Loads JSON file and takes care of exhibits with a parent but without
     * latitude and longitude.
     *
     * @param context Context.
     * @param path    Path of the asset JSON file to read from.
     *
     * @return List of Nodes to be injected into the database.
     */
    public static List<Node> loadJSON(Context context, String path) {
        List<Node> nodes = new ArrayList<>();
        VertexInfo.vertexInfos = VertexInfo.loadVertexInfoJSON(context, path);

        for (VertexInfo vertexInfo : VertexInfo.vertexInfos) {

            String parentId = "";

            // Finds VertexInfos of kind 'exhibit' to give exhibits in parent
            // groups a valid latitude and longitude
            if (vertexInfo.kind == VertexInfo.Kind.EXHIBIT) {
                double latitude;
                double longitude;

                if (vertexInfo.group_id != null && vertexInfo.lat == null && vertexInfo.lng == null) {
                    parentId = vertexInfo.group_id;
                    latitude =
                            Double.parseDouble(VertexInfo.findByParentId(vertexInfo.group_id).lat);
                    longitude =
                            Double.parseDouble(VertexInfo.findByParentId(vertexInfo.group_id).lng);
                }
                else {

                    // Edge case
                    if (vertexInfo.group_id != null && (vertexInfo.lat == null || vertexInfo.lng == null)) {
                        throw new RuntimeException("Nodes must have a " +
                                                   "lat/long unless they are "
                                                   + "grouped.");
                    }

                    latitude = Double.parseDouble(vertexInfo.lat);
                    longitude = Double.parseDouble(vertexInfo.lng);
                }

                // Create new Node and add to list
                Node exhibit = new Node(vertexInfo.id, parentId,
                                        vertexInfo.kind, vertexInfo.name,
                                        String.join(", ", vertexInfo.tags),
                                        latitude, longitude);
                nodes.add(exhibit);
            }

            // Convert VertexInfos of other kinds into Nodes and those to list
            else {
                Node node = new Node(vertexInfo.id, parentId, vertexInfo.kind
                        , vertexInfo.name, String.join(", ", vertexInfo.tags)
                        , Double.parseDouble(vertexInfo.lat),
                                     Double.parseDouble(vertexInfo.lng));
                nodes.add(node);
            }
        }

        return nodes;
    }
}