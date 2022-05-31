package com.example.zooseeker;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.ArraySet;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ActivityTracker {

    public static String directionsPreferences = "directionsPreferences";
    public static String directionsFlagKey = "directionsFlag";
    public static String exhibitIdsOrderKey = "exhibitIdsOrder";
    public static String indexKey = "index";
//    public static String sizeKey = "size";

    public static Context context;

    public static void setDirectionsFlag(boolean b) {
        SharedPreferences preferences = context.getSharedPreferences(directionsPreferences, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(directionsFlagKey, b);
        editor.apply();
    }

    public static boolean getDirectionsFlag() {
        SharedPreferences preferences = context.getSharedPreferences(directionsPreferences, 0);
        boolean returnToDirections = preferences.getBoolean(directionsFlagKey, false);
        Log.d("RETAIN", "skipToDirections: " + String.valueOf(returnToDirections));
        return returnToDirections;
    }

    public static int getIndex() {
        SharedPreferences preferences = context.getSharedPreferences(directionsPreferences, 0);
        int index = preferences.getInt(indexKey, -1);
        return index;
    }

    public static void setIndex(int index) {
        SharedPreferences preferences = context.getSharedPreferences(directionsPreferences, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(indexKey, index);
        editor.apply();
    }

    public static void setIds(List<String> currentExhibitIdsOrder) {
        String ids = "";
        for (int i = 0; i < currentExhibitIdsOrder.size(); ++i) {
            String id = currentExhibitIdsOrder.get(i);
            ids += id + ",";
        }

        SharedPreferences preferences = context.getSharedPreferences(directionsPreferences, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(exhibitIdsOrderKey, ids);
        editor.apply();
    }

    public static List<String> getIds() {
        SharedPreferences preferences = context.getSharedPreferences(directionsPreferences, 0);
        String ids = preferences.getString(exhibitIdsOrderKey, "");
        String[] orderIds = ids.split(",", 0);
        List<String> idsOrder = Arrays.asList(orderIds);
        return idsOrder;
    }

    public static void setContext(Context newContext) { context = newContext;}
}
