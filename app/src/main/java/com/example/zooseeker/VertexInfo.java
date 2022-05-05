package com.example.zooseeker;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class VertexInfo {
    public enum Kind {
        // The SerializedName annotation tells GSON how to convert
        // from the strings in our JSON to this Enum.
        @SerializedName("gate") GATE,
        @SerializedName("exhibit") EXHIBIT,
        @SerializedName("intersection") INTERSECTION
    }

    public String id;
    public Kind kind;
    public String name;
    public List<String> tags;

    @NonNull
    @Override
    public String toString() {
        return "VertexInfo{" + "id=" + id + ", kind=" + kind + ", name=" + name + ", " + "tags=[" + tags + "]}";
    }

    public static List<VertexInfo> loadVertexInfoJSON(Context context, String path) {
        try {
            InputStream input = context.getAssets().open(path);
            Reader reader = new InputStreamReader(input);

            Gson gson = new Gson();
            Type type = new TypeToken<List<VertexInfo>>() {}.getType();

            return gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}