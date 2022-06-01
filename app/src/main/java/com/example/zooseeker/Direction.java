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

/**
 * Direction object for directions between start and end Nodes. Called in
 * DirectionTracker and updated whenever start and end Nodes are changed.
 * Contains start and end Nodes, steps and distance between those, as well as
 * nodes to be passed to get to the end Node from the start Node.
 */
public class Direction {
    private String start;
    private String end;
    private List<String> briefSteps;
    private List<String> detailedSteps;
    private double distance;
    List<String> nodeIds;

    /**
     * Direction constructor.
     *
     * @param start         Id of start Node.
     * @param end           Id of end Node.
     * @param briefSteps    List of strings to be displayed in brief directions.
     * @param detailedSteps List of strings to be displayed in detailed
     *                      directions.
     * @param distance      Distance between start and end Nodes.
     * @param nodeIds       List of ids of Nodes to pass.
     */
    public Direction(String start, String end, List<String> briefSteps, List<String> detailedSteps, double distance, List<String> nodeIds) {
        this.start = start;
        this.end = end;
        this.briefSteps = briefSteps;
        this.detailedSteps = detailedSteps;
        this.distance = distance;
        this.nodeIds = nodeIds;
    }

    /**
     * Getter method for id of start Node.
     *
     * @return Id of start Node.
     */
    public String getStart() {return start;}

    /**
     * Getter method for id of end Node.
     *
     * @return Id of end Node.
     */
    public String getEnd() {return end;}

    /**
     * Getter method for briefSteps.
     *
     * @return BriefSteps.
     */
    public List<String> getBriefDirections() { return briefSteps; }

    /**
     * Getter method for detailedSteps.
     *
     * @return DetailedSteps.
     */
    public List<String> getDetailedDirections() {return detailedSteps;}

    /**
     * Getter method for distance.
     *
     * @return Distance between start and end Nodes.
     */
    public double getDistance() { return distance; }

    /**
     * Overrides toString() of Direction.
     *
     * @return String.
     */
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
