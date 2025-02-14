package com.example;

import org.json.JSONObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/updateVolunteerDetails")
public class UpdateVolunteerDetails extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\":false,\"message\":\"No active session.\"}");
            return;
        }

        String username = (String) session.getAttribute("username");
        StringBuilder json = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            json.append(line);
        }

        JSONObject jsonObject = new JSONObject(json.toString());

        try (Connection conn = DBConnection.getConnection()) {
            String query = "UPDATE volunteers SET email = ?, firstname = ?, lastname = ?, birthdate = ?, gender = ?, country = ?, address = ?, municipality = ?, prefecture = ?, job = ?, lat = ?, lon = ? ,volunteer_type = ?,height = ?, weight = ? WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, jsonObject.getString("email"));
                stmt.setString(2, jsonObject.getString("firstname"));
                stmt.setString(3, jsonObject.getString("lastname"));
                stmt.setString(4, jsonObject.getString("birthdate"));
                stmt.setString(5, jsonObject.getString("gender"));
                stmt.setString(6, jsonObject.getString("country"));
                stmt.setString(7, jsonObject.getString("address"));
                stmt.setString(8, jsonObject.getString("municipality"));
                stmt.setString(9, jsonObject.getString("prefecture"));
                stmt.setString(10, jsonObject.getString("job"));
                stmt.setString(11, jsonObject.getString("latitude"));
                stmt.setString(12, jsonObject.getString("longitude"));
                stmt.setString(13, jsonObject.getString("volunteer_type"));
                stmt.setString(14, jsonObject.getString("height"));
                stmt.setString(15, jsonObject.getString("weight"));
                stmt.setString(16, username);

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    response.getWriter().write("{\"success\":true,\"message\":\"Profile updated successfully.\"}");
                } else {
                    response.getWriter().write("{\"success\":false,\"message\":\"No changes were made.\"}");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\":false,\"message\":\"An error occurred while updating profile.\"}");
        }
    }
}
