package com.example.zooseeker;

import java.util.List;

public class Direction {
    private String start;
    private String end;
    private List<String> steps;

    public Direction(String start, String end, List<String> steps) {
        this.start = start;
        this.end = end;
        this.steps = steps;
    }

    public String getStart() { return start; }
    public String getEnd() { return end; }

    @Override
    public String toString() {
        return "Direction{" +
                "start='" + start + '\'' +
                ", end='" + end + '\'' +
                ", steps=" + steps +
                '}';
    }
}
