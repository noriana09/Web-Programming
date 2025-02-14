package com.example.servlets;

import com.example.DBConnection;
import com.example.mainClasses.Incident;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Map BOTH routes to the same servlet
@WebServlet(name = "IncidentServlet", urlPatterns = {"/incident", "/incident/all/running"})
public class IncidentServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath() + (req.getPathInfo() == null ? "" : req.getPathInfo());

        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {

            if ("/incident/all/running".equals(path)) {
                List<Incident> activeIncidents = new ArrayList<>();
                try (Connection connection = DBConnection.getConnection()) {
                    String sql = "SELECT * FROM incidents WHERE status = 'running'";
                    PreparedStatement stmt = connection.prepareStatement(sql);

                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        Incident inc = new Incident();
                        inc.setIncident_id(rs.getInt("incident_id"));
                        inc.setIncident_type(rs.getString("incident_type"));
                        inc.setDescription(rs.getString("description"));
                        inc.setAddress(rs.getString("address"));
                        inc.setPrefecture(rs.getString("prefecture"));
                        inc.setMunicipality(rs.getString("municipality"));
                        inc.setLat(rs.getDouble("lat")); // Fetch lat
                        inc.setLon(rs.getDouble("lon")); // Fetch lon
                        activeIncidents.add(inc);
                    }
                }
                out.println(gson.toJson(activeIncidents)); // Return JSON array
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.println("{\"message\": \"GET /incident not found\"}");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"message\": \"Database error on GET\"}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"message\": \"An unexpected error occurred on GET\"}");
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        // Set response headers
        resp.setContentType("application/json");

        // Log POST request
        System.out.println("POST request received at /incident");

        // Parse JSON body
        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }

        // Debugging: Print the request body
        System.out.println("Request Body: " + requestBody);

        try {
            // Convert JSON string to Incident object
            Incident incident = gson.fromJson(requestBody.toString(), Incident.class);

            // Validate input
            if (!isValidIncident(incident)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"message\": \"Invalid input data\"}");
                return;
            }

            // Insert the incident into the database
            try (Connection connection = DBConnection.getConnection()) {
                String sql = "INSERT INTO incidents " +
                        "(incident_type, description, user_type, user_phone, address, lat, lon, " +
                        " prefecture, municipality, status, danger, start_datetime) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'submitted', 'unknown', NOW())";

                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, incident.getIncident_type());
                stmt.setString(2, incident.getDescription());
                stmt.setString(3, incident.getUser_type());
                stmt.setString(4, incident.getUser_phone());
                stmt.setString(5, incident.getAddress());
                stmt.setObject(6, incident.getLat());
                stmt.setObject(7, incident.getLon());
                stmt.setString(8, incident.getPrefecture());
                stmt.setString(9, incident.getMunicipality());

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                    resp.getWriter().write("{\"message\": \"Incident added successfully\"}");
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().write("{\"message\": \"Failed to add incident\"}");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"message\": \"Database error\"}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"message\": \"An unexpected error occurred\"}");
        }
    }

    // Validate incident data
    private boolean isValidIncident(Incident incident) {
        if (incident == null) return false;
        if (incident.getIncident_type() == null || incident.getIncident_type().isEmpty()) return false;
        if (incident.getDescription() == null || incident.getDescription().isEmpty()) return false;
        if (incident.getUser_type() == null || incident.getUser_type().isEmpty()) return false;
        if (incident.getUser_phone() == null || incident.getUser_phone().isEmpty()) return false;
        if (incident.getAddress() == null || incident.getAddress().isEmpty()) return false;
        if (incident.getPrefecture() == null || incident.getPrefecture().isEmpty()) return false;
        if (incident.getMunicipality() == null || incident.getMunicipality().isEmpty()) return false;
        return true;
    }
}
