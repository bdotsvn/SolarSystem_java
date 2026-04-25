package com.planetsim.utils;

import com.planetsim.model.Planet;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Planets.
 */
public class PlanetDAO {
    
    /**
     * Lấy danh sách tất cả hành tinh trong database.
     */
    public List<Planet> getAllPlanets() {
        List<Planet> list = new ArrayList<>();
        String sql = "SELECT * FROM Planets";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Planet p = new Planet();
                p.setPlanetId(rs.getInt("planet_id"));
                p.setName(rs.getString("name"));
                p.setMassKg(rs.getDouble("mass_kg"));
                p.setRadiusKm(rs.getDouble("radius_km"));
                p.setDistanceSunKm(rs.getDouble("distance_sun_km"));
                p.setOrbitalPeriodDays(rs.getDouble("orbital_period_days"));
                p.setTextureFile(rs.getString("texture_file"));
                p.setRotationSpeed(rs.getDouble("rotation_speed"));
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lấy thông tin 1 hành tinh theo ID.
     */
    public Planet getPlanetById(int id) {
        String sql = "SELECT * FROM Planets WHERE planet_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Planet(
                        rs.getInt("planet_id"),
                        rs.getString("name"),
                        rs.getDouble("mass_kg"),
                        rs.getDouble("radius_km"),
                        rs.getDouble("distance_sun_km"),
                        rs.getDouble("orbital_period_days"),
                        rs.getString("texture_file"),
                        rs.getDouble("rotation_speed")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
