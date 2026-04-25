package com.planetsim.utils;

/**
 * Utility class for Physics calculations based on Newton's Law of Gravitation.
 */
public class PhysicsEngine {
    // Gravitational Constant G (m^3 kg^-1 s^-2)
    public static final double GRAVITATIONAL_CONSTANT = 6.67430e-11;

    /**
     * Tính toán vận tốc quỹ đạo để vật thể không rơi (Vận tốc vũ trụ cấp 1).
     * v = sqrt(G * M / r)
     */
    public static double calculateOrbitalVelocity(double planetMass, double planetRadiusKm, double altitudeKm) {
        double r = (planetRadiusKm + altitudeKm) * 1000; // Chuyển sang mét
        return Math.sqrt(GRAVITATIONAL_CONSTANT * planetMass / r) / 1000; // Trả về km/s
    }

    /**
     * Tính chu kỳ quỹ đạo (giây)
     * T = 2 * PI * r / v
     */
    public static double calculateOrbitalPeriod(double planetRadiusKm, double altitudeKm, double velocityKmPerSec) {
        if (velocityKmPerSec == 0) return 3600; // Default 1 hour
        double r = (planetRadiusKm + altitudeKm);
        return (2 * Math.PI * r) / velocityKmPerSec;
    }

    /**
     * Chuyển đổi tọa độ Địa lý (Kinh, Vĩ, Cao) sang tọa độ Cartesian 3D (X, Y, Z) cho JavaFX.
     * @param radius Tổng bán kính từ tâm (Planet radius + Altitude)
     * @param lat Vĩ độ (độ)
     * @param lon Kinh độ (độ)
     * @return mảng [x, y, z]
     */
    public static double[] getCartesian(double radius, double lat, double lon) {
        double latRad = Math.toRadians(lat);
        double lonRad = Math.toRadians(lon);
        
        // JavaFX: Y là trục đứng (up/down), X là ngang, Z là sâu
        double x = radius * Math.cos(latRad) * Math.sin(lonRad);
        double y = -radius * Math.sin(latRad); 
        double z = -radius * Math.cos(latRad) * Math.cos(lonRad);
        
        return new double[]{x, y, z};
    }
}
