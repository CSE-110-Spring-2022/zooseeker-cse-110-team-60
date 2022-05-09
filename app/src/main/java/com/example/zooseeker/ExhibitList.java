package com.example.zooseeker;

import java.util.ArrayList;
import java.util.List;

public class ExhibitList {
    public static List<ExhibitItem> allExhibits = new ArrayList<>();

    public static List<ExhibitItem> getCheckedExhibits() {
        MainActivity main = MainActivity.getInstance();
        List<ExhibitItem> checkedExhibits = main.getExhibits();
        checkedExhibits.removeIf(exhibit -> !exhibit.added);
        return checkedExhibits;
    }

    public static int getNumChecked() {
        return getCheckedExhibits().size();
    }

    public static void clearCheckedExhibits() {
        List<ExhibitItem> checkedExhibits = getCheckedExhibits();
        for (ExhibitItem item : checkedExhibits) {
            item.added = false;
        }
    }

    public static List<ExhibitItem> getSearchItems(String search) {
        List<ExhibitItem> searchItems  = new ArrayList<>();
        search = search.toLowerCase();
        searchByName(searchItems, search);
        searchAutoComplete(searchItems, search);
        searchByCategories(searchItems, search);
        searchItems = removeDuplicate(searchItems);
        return searchItems;
    }

    // Current problem: should "arctic fox" return exhibit "The Arctic Foxes"?
    private static void searchByName(List<ExhibitItem> searchItems, String search) {
        for (ExhibitItem item : ExhibitList.allExhibits) {
            String name = item.name;
            if (name.indexOf(search) == 0) {
                searchItems.add(item);
            }
        }
    }

    // Current problem: should "th" return exhibit "The Arctic Foxes"?
    // should "ar" return exhibit "The Arctic Foxes"?
    // should "Foxes" return exhibit "The Arctic Foxes"?
    private static void searchAutoComplete(List<ExhibitItem> searchItems, String search) {
        for (ExhibitItem item : ExhibitList.allExhibits) {
            String[] nameA = item.name.split(" ");
            for (String word : nameA) {
                word = word.toLowerCase();
                if (word.indexOf(search) == 0) {
                    searchItems.add(item);
                }
            }
        }
    }

    // Current question: "g" returns both Gorillas and Alligators because
    // Alligators have a "gator" tag that begins with g
    private static void searchByCategories(List<ExhibitItem> searchItems,
                                          String search) {
        for (ExhibitItem item : ExhibitList.allExhibits) {
            String [] categories = item.tags.split(", ");
            for (String word : categories) {
                if (word.indexOf(search) == 0) {
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
