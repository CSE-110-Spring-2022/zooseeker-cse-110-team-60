package com.example.zooseeker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;
import java.util.concurrent.Executors;

@Database(entities = {Node.class}, version = 1)
public abstract class NodeDatabase extends RoomDatabase {
    private static NodeDatabase singleton = null;

    public abstract NodeDao nodeDao();

    public synchronized static NodeDatabase getSingleton(Context context) {
        if (singleton == null) {
            singleton = NodeDatabase.makeDatabase(context);
        }
        return singleton;
    }

    private static NodeDatabase makeDatabase(Context context) {
        return Room.databaseBuilder(context, NodeDatabase.class, "node_list.db")
                   .allowMainThreadQueries().addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadExecutor().execute(() -> {
                            List<Node> nodes = Node.loadJSON(context, "exhibit_info" +
                                                                      ".json");
                            getSingleton(context).nodeDao().insertAll(nodes);
                        });
                    }
                }).build();
    }

    @VisibleForTesting
    public static void injectTestDatabase(NodeDatabase testDatabase) {
        if (singleton != null) {
            singleton.close();
        }
        singleton = testDatabase;
    }
}