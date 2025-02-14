package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Database URL, username, and password
    private static final String URL = "jdbc:mysql://localhost:3306/hy359_2024";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Load the MySQL driver
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load MySQL driver", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        connection.setAutoCommit(true);
        System.out.println("Database connection established to: " + URL);
        return connection;
    }

}

