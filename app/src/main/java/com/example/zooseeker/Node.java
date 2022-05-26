package com.example.zooseeker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @NonNull
    @Override
    public String toString() {
        return "Node{" + "id=" + id + ", parentId=" + parentId + ", kind=" + kind + ", " +
               "name=" + name + ", tags=[" + tags + "], added=" + added + ", latitude=" + latitude + ", " + "longitude" + longitude + "}";
    }

    public static List<Node> loadJSON(Context context, String path) {
        List<Node> nodes = new ArrayList<>();
        VertexInfo.vertexInfos = VertexInfo.loadVertexInfoJSON(context, path);

        for (VertexInfo vertexInfo : VertexInfo.vertexInfos) {

            String parentId = "";

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

                    if (vertexInfo.group_id != null && (vertexInfo.lat == null || vertexInfo.lng == null)) {
                        throw new RuntimeException("Nodes must have a lat/long unless they are grouped.");
                    }

                    latitude = Double.parseDouble(vertexInfo.lat);
                    longitude = Double.parseDouble(vertexInfo.lng);
                }

                Node exhibit = new Node(vertexInfo.id, parentId, vertexInfo.kind,
                                        vertexInfo.name, String.join(", ",
                                                                     vertexInfo.tags),
                                        latitude, longitude);
                nodes.add(exhibit);
            }

            else {
                Node node = new Node(vertexInfo.id, parentId, vertexInfo.kind,
                                     vertexInfo.name, String.join(", ",
                                                                  vertexInfo.tags),
                                     Double.parseDouble(vertexInfo.lat),
                                     Double.parseDouble(vertexInfo.lng));
                nodes.add(node);
            }
        }

        return nodes;
    }
}