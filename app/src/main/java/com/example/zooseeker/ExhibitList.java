package com.example.zooseeker;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.List;

/**
 * Gets and manipulates the lists of nodes and exhibits in the database for the
 * search functionality.
 */
public class ExhibitList {
    @SuppressLint("StaticFieldLeak")
    private final static MainActivity main = MainActivity.getInstance();

    /**
     * Gets all nodes, except of kind 'exhibit group', from the database.
     *
     * @return List of nodes, except exhibit groups.
     */
    public static List<Node> getAllNodes() {
        List<Node> nodes = main.getAllNodes();
        nodes.removeIf(node -> !node.parentId.isEmpty());
        return nodes;
    }

    /**
     * Gets all nodes of kind 'exhibit' from the database.
     *
     * @return List of exhibits.
     */
    public static List<Node> getAllExhibits() {
        return main.getAllExhibits();
    }

    /**
     * Gets all nodes of kind 'exhibit' from the database and return those who
     * have been checked to visit.
     *
     * @return List of checked exhibits.
     */
    public static List<Node> getCheckedExhibits() {
        List<Node> checkedExhibits = getAllExhibits();
        checkedExhibits.removeIf(exhibit -> !exhibit.added);
        return checkedExhibits;
    }

    /**
     * Calculates number of checked exhibits.
     *
     * @return Non-negative number of checked exhibits.
     */
    public static int getNumChecked() {
        return getCheckedExhibits().size();
    }

    /**
     * Unchecks all selected exhibits in the database.
     */
    public static void clearCheckedExhibits() {
        main.uncheckExhibits();
    }

    /**
     * Searches and returns exhibits whose names or tags contain the string to
     * be searched.
     *
     * @param search String to be searched.
     *
     * @return List of exhibits containing the search.
     */
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
     * Given the list of all exhibits, a list of search items, and a string
     * query, modify the list of search items to include all exhibits whose
     * names contain the query.
     *
     * @param allExhibits List of all exhibits to be searched through.
     * @param searchItems List of items which match the search.
     * @param search      String query.
     */
    private static void searchByName(List<Node> allExhibits,
                                     List<Node> searchItems, String search) {
        for (Node item : allExhibits) {
            String name = item.name;
            name = name.toLowerCase();
            if (name.contains(search)) {
                searchItems.add(item);
            }
        }
    }

    /**
     * Given the list of all exhibits, a list of search items, and a string
     * query, modify the list of search items to include all exhibits whose
     * names contain a word in the query.
     *
     * @param allExhibits The list of all exhibits to be searched through.
     * @param searchItems The list of items which match the search .
     * @param search      The string query.
     */
    private static void searchAutoComplete(List<Node> allExhibits,
                                           List<Node> searchItems,
                                           String search) {
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
     * Given the list of all exhibits, a list of search items, and a string
     * query, modify the list of search items to include all exhibits whose tags
     * contain the query.
     *
     * @param allExhibits The list of all exhibits to be searched through.
     * @param searchItems The list of items which match the search.
     * @param search      The string query.
     */
    private static void searchByCategories(List<Node> allExhibits,
                                           List<Node> searchItems,
                                           String search) {
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
     * Given a list of ExhibitItems which is the source result, return a copy
     * of it with all duplicates removed.
     *
     * @param searchItems The list of search items to be pruned.
     *
     * @return The list of search items stripped of duplicates.
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
