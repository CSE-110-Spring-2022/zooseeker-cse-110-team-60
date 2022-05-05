package com.example.zooseeker;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.List;

public class ExhibitList {
    @SuppressLint("StaticFieldLeak")
    private final static MainActivity main = MainActivity.getInstance();

    public static List<ExhibitItem> getAllExhibits() {
        return main.getExhibits();
    }

    public static List<ExhibitItem> getCheckedExhibits() {
        List<ExhibitItem> checkedExhibits = getAllExhibits();
        checkedExhibits.removeIf(exhibit -> !exhibit.added);
        return checkedExhibits;
    }

    public static int getNumChecked() {
        return getCheckedExhibits().size();
    }

    public static void clearCheckedExhibits() {
        main.uncheck();
    }

    public static List<ExhibitItem> getSearchItems(String search) {
        List<ExhibitItem> allExhibits = getAllExhibits();
        List<ExhibitItem> searchItems = new ArrayList<>();
        search = search.toLowerCase();
        searchByName(allExhibits, searchItems, search);
        searchAutoComplete(allExhibits, searchItems, search);
        searchByCategories(allExhibits, searchItems, search);
        searchItems = removeDuplicate(searchItems);
        return searchItems;
    }

    // Current problem: should "arctic fox" return exhibit "The Arctic Foxes"?
    private static void searchByName(List<ExhibitItem> allExhibits,
                                     List<ExhibitItem> searchItems, String search) {
        for (ExhibitItem item : allExhibits) {
            String name = item.name;
//            if (name.indexOf(search) == 0) {
            if (name.contains(search)) { // updated according to Piazza @504
                searchItems.add(item);
            }
        }
    }

    // Current problem: should "th" return exhibit "The Arctic Foxes"?
    // should "ar" return exhibit "The Arctic Foxes"?
    // should "Foxes" return exhibit "The Arctic Foxes"?
    private static void searchAutoComplete(List<ExhibitItem> allExhibits,
                                           List<ExhibitItem> searchItems, String search) {
        for (ExhibitItem item : allExhibits) {
            String[] nameA = item.name.split(" ");
            for (String word : nameA) {
                word = word.toLowerCase();
//                if (word.indexOf(search) == 0) {
                if (word.contains(search)) { // updated according to Piazza @504
                    searchItems.add(item);
                }
            }
        }
    }

    // Current question: "g" returns both Gorillas and Alligators because
    // Alligators have a "gator" tag that begins with g
    private static void searchByCategories(List<ExhibitItem> allExhibits,
                                           List<ExhibitItem> searchItems, String search) {
        for (ExhibitItem item : allExhibits) {
            String[] categories = item.tags.split(", ");
            for (String word : categories) {
//                if (word.indexOf(search) == 0) {
                if (word.contains(search)) { // updated according to Piazza @504
                    searchItems.add(item);
                }
            }
        }
    }

    private static List<ExhibitItem> removeDuplicate(List<ExhibitItem> searchItems) {
        List<ExhibitItem> noDuplicateSearch = new ArrayList<>();
        for (ExhibitItem item : searchItems) {
            if (!noDuplicateSearch.contains(item)) {
                noDuplicateSearch.add(item);
            }
        }
        return noDuplicateSearch;
    }
}
