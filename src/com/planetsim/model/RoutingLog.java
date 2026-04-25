package com.planetsim.model;

import java.sql.Timestamp;

/**
 * Entity class for Satellite Routing Logs.
 */
public class RoutingLog {
    private int logId;
    private int sourceSatId;
    private int destSatId;
    private String pathDescription;
    private double totalDistanceKm;
    private Timestamp logTime;

    public RoutingLog() {}

    public RoutingLog(int logId, int sourceSatId, int destSatId, String pathDescription, double totalDistanceKm, Timestamp logTime) {
        this.logId = logId;
        this.sourceSatId = sourceSatId;
        this.destSatId = destSatId;
        this.pathDescription = pathDescription;
        this.totalDistanceKm = totalDistanceKm;
        this.logTime = logTime;
    }

    // Getters and Setters
    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }

    public int getSourceSatId() { return sourceSatId; }
    public void setSourceSatId(int sourceSatId) { this.sourceSatId = sourceSatId; }

    public int getDestSatId() { return destSatId; }
    public void setDestSatId(int destSatId) { this.destSatId = destSatId; }

    public String getPathDescription() { return pathDescription; }
    public void setPathDescription(String pathDescription) { this.pathDescription = pathDescription; }

    public double getTotalDistanceKm() { return totalDistanceKm; }
    public void setTotalDistanceKm(double totalDistanceKm) { this.totalDistanceKm = totalDistanceKm; }

    public Timestamp getLogTime() { return logTime; }
    public void setLogTime(Timestamp logTime) { this.logTime = logTime; }
}
