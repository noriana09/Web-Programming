package com.example;

import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            BufferedReader reader = request.getReader();
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            JSONObject jsonObject = new JSONObject(json.toString());
            String userType = jsonObject.getString("type");

            if (isDuplicate("username", jsonObject.getString("username"))) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                out.write("{\"success\":false,\"message\":\"Username already exists.\"}");
                return;
            }
            if (isDuplicate("email", jsonObject.getString("email"))) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                out.write("{\"success\":false,\"message\":\"Email already exists.\"}");
                return;
            }
            if (isDuplicate("telephone", jsonObject.getString("telephone"))) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                out.write("{\"success\":false,\"message\":\"Telephone already exists.\"}");
                return;
            }

            boolean isSuccess = false;
            String successMessage = "";
            if ("user".equals(userType)) {
                isSuccess = saveSimpleUser(jsonObject);
                successMessage = "Simple user registered successfully.";
            } else if ("volunteer".equals(userType)) {
                isSuccess = saveVolunteer(jsonObject);
                successMessage = "Volunteer registered successfully.";
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"success\":false,\"message\":\"Invalid user type.\"}");
                return;
            }

            if (isSuccess) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"success\":true,\"message\":\"" + successMessage + "\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.write("{\"success\":false,\"message\":\"Failed to save the user.\"}");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"success\":false,\"message\":\"An unexpected error occurred: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    private boolean isDuplicate(String field, String value) throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE " + field + " = ? UNION SELECT COUNT(*) FROM volunteers WHERE " + field + " = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, value);
            stmt.setString(2, value);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    if (rs.getInt(1) > 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean saveSimpleUser(JSONObject jsonObject) throws SQLException {
        String query = "INSERT INTO users (username, email, password, firstname, lastname, birthdate, gender, afm, country, address, municipality, prefecture, job, telephone, lat, lon) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            System.out.println("Inserting user with username: " + jsonObject.getString("username"));
            stmt.setString(1, jsonObject.getString("username"));
            stmt.setString(2, jsonObject.getString("email"));
            stmt.setString(3, jsonObject.getString("password"));
            stmt.setString(4, jsonObject.getString("firstname"));
            stmt.setString(5, jsonObject.getString("lastname"));
            stmt.setString(6, jsonObject.getString("birthdate"));
            stmt.setString(7, jsonObject.getString("gender"));
            stmt.setString(8, jsonObject.getString("afm"));
            stmt.setString(9, jsonObject.getString("country"));
            stmt.setString(10, jsonObject.getString("address"));
            stmt.setString(11, jsonObject.getString("municipality"));
            stmt.setString(12, jsonObject.getString("prefecture"));
            stmt.setString(13, jsonObject.getString("job"));
            stmt.setString(14, jsonObject.getString("telephone"));
            stmt.setString(15, jsonObject.getString("latitude"));
            stmt.setString(16, jsonObject.getString("longitude"));

            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error during user insertion: " + e.getMessage());
            throw e;
        }
    }

    private boolean saveVolunteer(JSONObject jsonObject) throws SQLException {
        String query = "INSERT INTO volunteers (username, email, password, firstname, lastname, birthdate, gender, afm, country, address, municipality, prefecture, job, telephone, lat, lon, volunteer_type, height, weight) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, jsonObject.getString("username"));
            stmt.setString(2, jsonObject.getString("email"));
            stmt.setString(3, jsonObject.getString("password"));
            stmt.setString(4, jsonObject.getString("firstname"));
            stmt.setString(5, jsonObject.getString("lastname"));
            stmt.setString(6, jsonObject.getString("birthdate"));
            stmt.setString(7, jsonObject.getString("gender"));
            stmt.setString(8, jsonObject.getString("afm"));
            stmt.setString(9, jsonObject.getString("country"));
            stmt.setString(10, jsonObject.getString("address"));
            stmt.setString(11, jsonObject.getString("municipality"));
            stmt.setString(12, jsonObject.getString("prefecture"));
            stmt.setString(13, jsonObject.getString("job"));
            stmt.setString(14, jsonObject.getString("telephone"));
            stmt.setString(15, jsonObject.getString("latitude"));
            stmt.setString(16, jsonObject.getString("longitude"));
            stmt.setString(17, jsonObject.getString("volunteer_type"));
            stmt.setString(18, jsonObject.getString("height"));
            stmt.setString(19, jsonObject.getString("weight"));
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}