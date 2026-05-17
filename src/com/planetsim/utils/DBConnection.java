package com.planetsim.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for Database Connection using JDBC for SQL Server.
 */
public class DBConnection {
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=PlanetSim;encrypt=false;trustServerCertificate=true;";
    private static final String USER = "sa";
    private static final String PASS = "123456";

    private DBConnection() {}

    /**
     * Trả về kết nối mới tới database mỗi khi được gọi.
     * DAO sử dụng try-with-resources sẽ tự đóng kết nối này.
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Lỗi: Không tìm thấy JDBC Driver!", e);
        }
    }

    /**
     * @deprecated Không còn dùng Singleton connection.
     */
    public static void close() {
        // No-op
    }
}
