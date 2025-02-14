package com.example.servlets;

import com.example.DBConnection;
import java.io.IOException;
import java.io.BufferedReader;
import java.sql.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

@WebServlet("/admin/incidents/new")
public class AdminCreate extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        // 1) Read JSON
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        String requestBody = sb.toString();

        // 2) Parse JSON
        JsonObject body;
        try {
            body = JsonParser.parseString(requestBody).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"success\": false, \"message\": \"Invalid JSON.\"}");
            return;
        }

        // 3) Extract the new fields
        String incidentType = body.has("incident_type")
                ? body.get("incident_type").getAsString() : "";
        String description  = body.has("description")
                ? body.get("description").getAsString() : "";
        String userType     = body.has("user_type")
                ? body.get("user_type").getAsString()   : "";
        String userPhone    = body.has("user_phone")
                ? body.get("user_phone").getAsString()  : "";
        String address      = body.has("address")
                ? body.get("address").getAsString()     : "";
        String municipality = body.has("municipality")
                ? body.get("municipality").getAsString(): "";
        String prefecture   = body.has("prefecture")
                ? body.get("prefecture").getAsString()  : "";
        String finalResult  = body.has("finalResult")
                ? body.get("finalResult").getAsString() : "null";
        String danger       = body.has("danger")
                ? body.get("danger").getAsString()      : "unknown";

        double lat = body.has("lat") ? body.get("lat").getAsDouble() : 0.0;
        double lon = body.has("lon") ? body.get("lon").getAsDouble() : 0.0;

        int vehicles = body.has("vehicles") ? body.get("vehicles").getAsInt() : 0;
        int firemen  = body.has("firemen")  ? body.get("firemen").getAsInt()  : 0;

        // Validate a couple key fields:
        if (incidentType.isEmpty() || description.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"success\": false, \"message\": \"incident_type and description are required.\"}");
            return;
        }

        // 4) Insert: status=running, start_datetime=NOW() (because admin creates real incident)
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO incidents (" +
                    " incident_type, description, user_type, user_phone, address," +
                    " lat, lon, municipality, prefecture, finalResult," +
                    " danger, status, vehicles, firemen, start_datetime" +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'running', ?, ?, NOW())";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, incidentType);
            ps.setString(2, description);
            ps.setString(3, userType);
            ps.setString(4, userPhone);
            ps.setString(5, address);
            ps.setDouble(6, lat);
            ps.setDouble(7, lon);
            ps.setString(8, municipality);
            ps.setString(9, prefecture);
            ps.setString(10, finalResult);
            ps.setString(11, danger);
            ps.setInt(12, vehicles);
            ps.setInt(13, firemen);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                resp.getWriter().write("{\"success\":true,\"message\":\"Incident inserted.\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"success\":false,\"message\":\"Failed to insert incident.\"}");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"success\":false,\"message\":\"Database error.\"}");
        }
    }
}
