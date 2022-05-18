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



    // Credit: https://stackoverflow.com/questions/17076030/how-can-i-find-int-values-within-a-string
    public int getDistance() {
        int dist = 0;
        for (String s : steps) {
            String clean = s.replaceAll("\\D+","");
            dist += Integer.parseInt(clean);
        }
        return dist;
    }
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
        return String.format("%s to %s (%d meters)",
                start,
                end,
                getDistance()
        );
    }
}
