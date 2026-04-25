package com.planetsim.model;

import java.util.List;

/**
 * Helper class to store the result of Dijkstra routing.
 */
public class RoutingResult {
    private List<Satellite> path;
    private double totalDistance;
    private boolean success;

    public RoutingResult(List<Satellite> path, double totalDistance, boolean success) {
        this.path = path;
        this.totalDistance = totalDistance;
        this.success = success;
    }

    // Getters
    public List<Satellite> getPath() { return path; }
    public double getTotalDistance() { return totalDistance; }
    public boolean isSuccess() { return success; }

    @Override
    public String toString() {
        if (!success) return "No path found.";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            sb.append(path.get(i).getName());
            if (i < path.size() - 1) sb.append(" -> ");
        }
        sb.append(String.format(" (Total: %.2f km)", totalDistance));
        return sb.toString();
    }
}
