package com.planetsim.utils;

import com.planetsim.model.RoutingLog;
import java.sql.*;

/**
 * Data Access Object for Routing Logs.
 */
public class LogDAO {

    public boolean saveLog(RoutingLog log) {
        String sql = "INSERT INTO RoutingLogs (source_sat_id, dest_sat_id, path_description, total_distance_km) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, log.getSourceSatId());
            pstmt.setInt(2, log.getDestSatId());
            pstmt.setString(3, log.getPathDescription());
            pstmt.setDouble(4, log.getTotalDistanceKm());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
