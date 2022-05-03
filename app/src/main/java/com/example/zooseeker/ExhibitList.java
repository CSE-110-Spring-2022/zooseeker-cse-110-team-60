package com.example.zooseeker;

import java.util.ArrayList;
import java.util.List;

public class ExhibitList {
    public static List<ExhibitItem> allExhibits = new ArrayList<>();

    List<ExhibitItem> getCheckedExhibits() {
        List<ExhibitItem> checkedExhibits = new ArrayList<>();
        for (ExhibitItem exhibit : allExhibits) {
            if (exhibit.added) {
                checkedExhibits.add(exhibit);
            }
        }
        return checkedExhibits;
    }
}
