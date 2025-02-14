package com.example;

import java.sql.Connection;

public class TestDBConnection {
    public static void main(String[] args) {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                System.out.println("Database connection established successfully!");
            } else {
                System.out.println("Failed to establish database connection.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}