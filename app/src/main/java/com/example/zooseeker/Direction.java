/*
 *   File Name:
 *   Class Name:
 *   Description:
 *   Fields:
 *   Public Functions:
 *   Private Functions:
 */

package com.example.zooseeker;

import java.util.List;

public class Direction {
    private String start;
    private String end;
    private List<String> briefSteps;
    private List<String> detailedSteps;
    private double distance;

    public Direction(String start, String end, List<String> briefSteps, List<String> detailedSteps, double distance) {
        this.start = start;
        this.end = end;
        this.briefSteps = briefSteps;
        this.detailedSteps = detailedSteps;
        this.distance = distance;
    }

    public String getStart() {return start;}

    public String getEnd() {return end;}

    public List<String> getBriefDirections() { return briefSteps; }

    public List<String> getDetailedDirections() {return detailedSteps;}

    public double getDistance() { return distance; }

    @Override
    public String toString() {
        return "Direction{" +
                "start='" + start + '\'' +
                ", end='" + end + '\'' +
                ", briefSteps=" + briefSteps +
                ", detailedSteps=" + detailedSteps +
                ", distance=" + distance +
                '}';
    }
}
