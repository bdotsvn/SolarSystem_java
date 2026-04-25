package com.planetsim.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton class for Database Connection using JDBC for SQL Server.
 */
public class DBConnection {
    // Thông số kết nối mặc định
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=PlanetSim;encrypt=false;trustServerCertificate=true;";
    private static final String USER = "sa";
    private static final String PASS = "123456";

    private static Connection connection = null;

    private DBConnection() {}

    /**
     * Trả về kết nối tới database. Nếu chưa có hoặc đã đóng thì tạo mới.
     */
    public static Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                // Đăng ký driver (Cần file mssql-jdbc.jar trong classpath)
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                connection = DriverManager.getConnection(URL, USER, PASS);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Lỗi: Không tìm thấy JDBC Driver!");
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Đóng kết nối an toàn.
     */
    public static void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
