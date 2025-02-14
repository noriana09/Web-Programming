package com.example.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.example.database.DB_Connection;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@WebServlet(name = "VolunteerIncidentServlet", urlPatterns = {"/volunteer/incidents"})
public class VolunteerIncidentServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        HttpSession session = req.getSession(false); // Get the existing session
        if (session == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"success\": false, \"message\": \"User not logged in\"}");
            return;
        }

        String username = (String) session.getAttribute("username"); // Get username from the session
        if (username == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"success\": false, \"message\": \"Username not found in session\"}");
            return;
        }

        try (Connection connection = DB_Connection.getConnection()) {
            // Query to fetch volunteer_id based on username
            String query = "SELECT volunteer_id FROM volunteers WHERE username = ?";
            try (PreparedStatement volunteerStmt = connection.prepareStatement(query)) {
                volunteerStmt.setString(1, username);

                try (ResultSet rs = volunteerStmt.executeQuery()) {
                    if (rs.next()) {
                        int volunteerId = rs.getInt("volunteer_id");
                        System.out.println("Volunteer ID: " + volunteerId);

                        // Simplified SQL query to fetch incident IDs from volunteer_requests
                        String sql = "SELECT incident_id " +
                                "FROM volunteer_requests " +
                                "WHERE volunteer_id = ? AND status = 'approved'";

                        try (PreparedStatement incidentStmt = connection.prepareStatement(sql)) {
                            incidentStmt.setInt(1, volunteerId);

                            try (ResultSet incidentRs = incidentStmt.executeQuery()) {
                                List<JsonObject> incidents = new ArrayList<>();

                                while (incidentRs.next()) {
                                    JsonObject incident = new JsonObject();
                                    incident.addProperty("incident_id", incidentRs.getInt("incident_id"));
                                    incidents.add(incident);
                                }

                                Gson gson = new Gson();
                                resp.setStatus(HttpServletResponse.SC_OK);
                                resp.getWriter().write(gson.toJson(incidents));
                            }
                        }
                    } else {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        resp.getWriter().write("{\"success\": false, \"message\": \"Volunteer not found\"}");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"success\": false, \"message\": \"Database error\"}");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"success\": false, \"message\": \"Unexpected error occurred\"}");
        }
    }
}
