package com.example.zooseeker;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.List;

public class ExhibitList {
    @SuppressLint("StaticFieldLeak")
    private final static MainActivity main = MainActivity.getInstance();

    public static List<Node> getAllNodes() {
        return main.getAllNodes();
    }

    public static List<Node> getAllExhibits() {
        return main.getAllExhibits();
    }

    public static List<Node> getCheckedExhibits() {
        List<Node> checkedExhibits = getAllExhibits();
        checkedExhibits.removeIf(exhibit -> !exhibit.added);
        return checkedExhibits;
    }

    public static int getNumChecked() {
        return getCheckedExhibits().size();
    }

    public static void clearCheckedExhibits() {
        main.uncheckExhibits();
    }

    public static List<Node> getSearchItems(String search) {
        List<Node> allExhibits = getAllExhibits();
        List<Node> searchItems = new ArrayList<>();
        search = search.toLowerCase();
        searchByName(allExhibits, searchItems, search);
        searchAutoComplete(allExhibits, searchItems, search);
        searchByCategories(allExhibits, searchItems, search);
        searchItems = removeDuplicate(searchItems);
        return searchItems;
    }

    /**
     * Name:       searchByName
     * Behavior:   Given the list of all exhibits, a list of search items, and a string
     * query,
     * modify the list of search items to include all exhibits whose names contain
     * the query.
     *
     * @param allExhibits the list of all exhibits to be searched through
     *                    searchItems     the list of items which match the search
     *                    String             search          the string query
     */
    private static void searchByName(List<Node> allExhibits, List<Node> searchItems,
                                     String search) {
        for (Node item : allExhibits) {
            String name = item.name;
            name = name.toLowerCase();
            if (name.contains(search)) {
                searchItems.add(item);
            }
        }
    }

    /**
     * Name:       searchAutoComplete
     * Behavior:   Given the list of all exhibits, a list of search items, and a string
     * query,
     * modify the list of search items to include all exhibits whose names contain
     * a word in the query.
     *
     * @param allExhibits the list of all exhibits to be searched through
     *                    searchItems     the list of items which match the search
     *                    String             search          the string query
     */
    private static void searchAutoComplete(List<Node> allExhibits,
                                           List<Node> searchItems, String search) {
        for (Node item : allExhibits) {
            String[] nameA = item.name.split(" ");
            for (String word : nameA) {
                word = word.toLowerCase();
                if (word.contains(search)) {
                    searchItems.add(item);
                }
            }
        }
    }

    /**
     * Name:       searchByCategories
     * Behavior:   Given the list of all exhibits, a list of search items, and a string
     * query,
     * modify the list of search items to include all exhibits whose tags contain
     * the query.
     *
     * @param allExhibits the list of all exhibits to be searched through
     *                    searchItems     the list of items which match the search
     *                    String             search          the string query
     */
    private static void searchByCategories(List<Node> allExhibits,
                                           List<Node> searchItems, String search) {
        for (Node item : allExhibits) {
            String[] categories = item.tags.split(", ");
            for (String word : categories) {
                if (word.contains(search)) {
                    searchItems.add(item);
                }
            }
        }
    }

    /**
     * Name:       removeDuplicate
     * Behavior:   Given a list of ExhibitItems which is the source result, return a copy
     * of it
     * with all duplicates removed.
     *
     * @param searchItems the list of search items to be pruned
     *
     * @return noDuplicateSearch   the list of search items stripped of duplicates
     */
    private static List<Node> removeDuplicate(List<Node> searchItems) {
        List<Node> noDuplicateSearch = new ArrayList<>();
        for (Node item : searchItems) {
            if (!noDuplicateSearch.contains(item)) {
                noDuplicateSearch.add(item);
            }
        }
        return noDuplicateSearch;
    }
}
