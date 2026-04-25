package com.planetsim.model;

/**
 * Entity class representing a Planet.
 * Uses Java 8 compatible syntax.
 */
public class Planet {
    private int planetId;
    private String name;
    private double massKg;
    private double radiusKm;
    private double distanceSunKm;
    private double orbitalPeriodDays;
    private String textureFile;
    private double rotationSpeed;

    public Planet() {}

    public Planet(int planetId, String name, double massKg, double radiusKm, double distanceSunKm, double orbitalPeriodDays, String textureFile, double rotationSpeed) {
        this.planetId = planetId;
        this.name = name;
        this.massKg = massKg;
        this.radiusKm = radiusKm;
        this.distanceSunKm = distanceSunKm;
        this.orbitalPeriodDays = orbitalPeriodDays;
        this.textureFile = textureFile;
        this.rotationSpeed = rotationSpeed;
    }

    // Getters and Setters
    public int getPlanetId() { return planetId; }
    public void setPlanetId(int planetId) { this.planetId = planetId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getMassKg() { return massKg; }
    public void setMassKg(double massKg) { this.massKg = massKg; }

    public double getRadiusKm() { return radiusKm; }
    public void setRadiusKm(double radiusKm) { this.radiusKm = radiusKm; }

    public String getTextureFile() { return textureFile; }
    public void setTextureFile(String textureFile) { this.textureFile = textureFile; }

    public double getDistanceSunKm() { return distanceSunKm; }
    public void setDistanceSunKm(double distanceSunKm) { this.distanceSunKm = distanceSunKm; }

    public double getOrbitalPeriodDays() { return orbitalPeriodDays; }
    public void setOrbitalPeriodDays(double orbitalPeriodDays) { this.orbitalPeriodDays = orbitalPeriodDays; }

    public double getRotationSpeed() { return rotationSpeed; }
    public void setRotationSpeed(double rotationSpeed) { this.rotationSpeed = rotationSpeed; }

    @Override
    public String toString() {
        return name;
    }
}
