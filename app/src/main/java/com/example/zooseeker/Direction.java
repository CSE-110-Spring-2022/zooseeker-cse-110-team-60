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
    private List<String> steps;
    private double distance;

    public Direction(String start, String end, List<String> steps, double distance) {
        this.start = start;
        this.end = end;
        this.steps = steps;
        this.distance = distance;
    }

    public String getStart() {return start;}

    public String getEnd() {return end;}

    public List<String> getSteps() {return steps;}

    public double getDistance() { return distance; }

    @Override
    public String toString() {
        return "Direction{" + "start='" + start + '\'' + ", end='" + end + '\'' + ", " +
               "steps=" + steps + '}';
    }
}
