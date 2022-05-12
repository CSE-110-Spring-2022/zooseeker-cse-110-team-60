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

    /*
     *   Name:       searchByName
     *   Behavior:   Given the list of all exhibits, a list of search items, and a string query,
     *               modify the list of search items to include all exhibits whose names contain
     *               the query.
     *   @param      List<ExhibitItem>  allExhibits     the list of all exhibits to be searched through
     *               List<ExhibitItem>  searchItems     the list of items which match the search
     *               String             search          the string query
     *   @return
     */
    private static void searchByName(List<ExhibitItem> allExhibits,
                                     List<ExhibitItem> searchItems, String search) {
        for (ExhibitItem item : allExhibits) {
            String name = item.name;
            if (name.contains(search)) {
                searchItems.add(item);
            }
        }
    }

    /*
     *   Name:       searchAutoComplete
     *   Behavior:   Given the list of all exhibits, a list of search items, and a string query,
     *               modify the list of search items to include all exhibits whose names contain
     *               a word in the query.
     *   @param      List<ExhibitItem>  allExhibits     the list of all exhibits to be searched through
     *               List<ExhibitItem>  searchItems     the list of items which match the search
     *               String             search          the string query
     *   @return
     */
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


    /*
     *   Name:       searchByCategories
     *   Behavior:   Given the list of all exhibits, a list of search items, and a string query,
     *               modify the list of search items to include all exhibits whose tags contain
     *               the query.
     *   @param      List<ExhibitItem>  allExhibits     the list of all exhibits to be searched through
     *               List<ExhibitItem>  searchItems     the list of items which match the search
     *               String             search          the string query
     *   @return
     */
    private static void searchByCategories(List<ExhibitItem> allExhibits,
                                           List<ExhibitItem> searchItems, String search) {
        for (ExhibitItem item : allExhibits) {
            String[] categories = item.tags.split(", ");
            for (String word : categories) {
                if (word.contains(search)) {
                    searchItems.add(item);
                }
            }
        }
    }

    /*
     *   Name:       removeDuplicate
     *   Behavior:   Given a list of ExhibitItems which is the source result, return a copy of it
     *               with all duplicates removed.
     *   @param      List<ExhibitItem>  searchItems         the list of search items to be pruned
     *   @return     List<ExhibitItem>  noDuplicateSearch   the list of search items stripped of duplicates
     */
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
