package com.planetsim.utils;

import com.planetsim.model.Satellite;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Satellites.
 */
public class SatelliteDAO {

    public List<Satellite> getSatellitesByPlanet(int planetId) {
        List<Satellite> list = new ArrayList<>();
        String sql = "SELECT * FROM Satellites WHERE planet_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, planetId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Satellite s = new Satellite();
                    s.setSatelliteId(rs.getInt("satellite_id"));
                    s.setPlanetId(rs.getInt("planet_id"));
                    s.setName(rs.getString("name"));
                    s.setAltitudeKm(rs.getDouble("altitude_km"));
                    s.setLongitude(rs.getDouble("longitude"));
                    s.setLatitude(rs.getDouble("latitude"));
                    s.setOrbitalVelocity(rs.getDouble("orbital_velocity"));
                    s.setSignalRangeKm(rs.getDouble("signal_range_km"));
                    s.setNatural(rs.getBoolean("is_natural"));
                    list.add(s);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addSatellite(Satellite s) {
        String sql = "INSERT INTO Satellites (planet_id, name, altitude_km, longitude, latitude, orbital_velocity, signal_range_km, is_natural) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, s.getPlanetId());
            pstmt.setString(2, s.getName());
            pstmt.setDouble(3, s.getAltitudeKm());
            pstmt.setDouble(4, s.getLongitude());
            pstmt.setDouble(5, s.getLatitude());
            pstmt.setDouble(6, s.getOrbitalVelocity());
            pstmt.setDouble(7, s.getSignalRangeKm());
            pstmt.setBoolean(8, s.isNatural());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSatellite(int id) {
        String sql = "DELETE FROM Satellites WHERE satellite_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
