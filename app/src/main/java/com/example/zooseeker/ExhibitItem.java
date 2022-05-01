package com.example.zooseeker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

@Entity(tableName = "exhibit_list_items")
public class ExhibitItem {
    @PrimaryKey @NonNull public String          id;
    @NonNull public             VertexInfo.Kind kind;
    @NonNull public             String          name;
    @NonNull public String tags;
    public          boolean  added;

    public ExhibitItem(@NonNull String id, @NonNull VertexInfo.Kind kind,
                       @NonNull String name, @NonNull String tags) {
        this.id = id;
        this.kind = kind;
        this.name = name;
        this.tags = tags;
        this.added = false;
    }

    @Override public String toString() {
        return "ExhibitListItem{" + "id=" + id + ", kind=" + kind + ", name=" +
                name + "}, tags=[" + tags + "], added=" + added + "}";
    }

    public static List<ExhibitItem> loadJSON(Context context, String path) {
        List<ExhibitItem> exhibitItems = new ArrayList<>();
        List<VertexInfo> vertexInfos   =
                VertexInfo.loadVertexInfoJSON(context, path);
        for (VertexInfo item : vertexInfos) {
            if (item.kind == VertexInfo.Kind.EXHIBIT) {
                ExhibitItem exhibitItem =
                        new ExhibitItem(item.id, item.kind, item.name,
                                String.join(", ", item.tags));
                exhibitItems.add(exhibitItem);
            }
            else {
                continue;
            }
        }
        return exhibitItems;
    }

    public static List<ExhibitItem> getSearchItems(Context context,
                                                   String path, String search) {
        List<ExhibitItem> searchItems = new ArrayList<>();
        List<ExhibitItem> exhibitItems = loadJSON(context, path);
        for (ExhibitItem item : exhibitItems) {
            String[] nameA = item.name.split(" ");
            for (String word : nameA) {
                word = word.toLowerCase();
                if (word.indexOf(search) == 0) {
                    searchItems.add(item);
                }
            }
        }
        return searchItems;
    }
}
