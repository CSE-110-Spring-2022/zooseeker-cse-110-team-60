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
    public List<String> getSteps() { return steps;}

    @Override
    public String toString() {
        return "Direction{" +
                "start='" + start + '\'' +
                ", end='" + end + '\'' +
                ", steps=" + steps +
                '}';
    }
    // toString version for summary
    // **Still need to implement '(distance in meters)' after listing step
    public String toSummaryString() {
        return start + " to " + end + " (n meters)";
    }
}
