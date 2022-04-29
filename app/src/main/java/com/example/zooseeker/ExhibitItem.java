package com.example.zooseeker;

import java.util.List;

public class ExhibitItem {
    public long id;
    public String name;
    public List<String> tags;

    public ExhibitItem(long id, String name, List<String> tags) {
        this.id = id;
        this.name = name;
        this.tags = tags;
    }
}
