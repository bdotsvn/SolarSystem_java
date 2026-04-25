package com.planetsim.model;

/**
 * Entity class representing a Satellite (Natural or Artificial).
 */
public class Satellite {
    private int satelliteId;
    private int planetId;
    private String name;
    private double altitudeKm;
    private double longitude;
    private double latitude;
    private double orbitalVelocity;
    private double signalRangeKm;
    private boolean natural;

    public Satellite() {}

    public Satellite(int satelliteId, int planetId, String name, double altitudeKm, 
                     double longitude, double latitude, double orbitalVelocity, double signalRangeKm, boolean natural) {
        this.satelliteId = satelliteId;
        this.planetId = planetId;
        this.name = name;
        this.altitudeKm = altitudeKm;
        this.longitude = longitude;
        this.latitude = latitude;
        this.orbitalVelocity = orbitalVelocity;
        this.signalRangeKm = signalRangeKm;
        this.natural = natural;
    }

    // Getters and Setters
    public int getSatelliteId() { return satelliteId; }
    public void setSatelliteId(int satelliteId) { this.satelliteId = satelliteId; }

    public int getPlanetId() { return planetId; }
    public void setPlanetId(int planetId) { this.planetId = planetId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getAltitudeKm() { return altitudeKm; }
    public void setAltitudeKm(double altitudeKm) { this.altitudeKm = altitudeKm; }

    public double getOrbitalVelocity() { return orbitalVelocity; }
    public void setOrbitalVelocity(double orbitalVelocity) { this.orbitalVelocity = orbitalVelocity; }

    public double getSignalRangeKm() { return signalRangeKm; }
    public void setSignalRangeKm(double signalRangeKm) { this.signalRangeKm = signalRangeKm; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public boolean isNatural() { return natural; }
    public void setNatural(boolean natural) { this.natural = natural; }

    @Override
    public String toString() {
        return name + (natural ? " (Moon)" : " (Sat)");
    }
}
