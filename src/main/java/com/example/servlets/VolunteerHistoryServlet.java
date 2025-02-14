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

@WebServlet(name = "VolunteerHistoryServlet", urlPatterns = {"/volunteer/history"})
public class VolunteerHistoryServlet extends HttpServlet {
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
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int volunteerId = rs.getInt("volunteer_id");
                System.out.println("Volunteer ID: " + volunteerId);

                // Fetch the history of incidents where the volunteer has participated
                String sql =
                        "SELECT i.incident_id, i.description, i.start_datetime, i.end_datetime, i.status " +
                                "FROM incidents i " +
                                "JOIN volunteer_requests vr ON i.incident_id = vr.incident_id " +
                                "WHERE vr.volunteer_id = ? AND vr.status = 'approved'";

                PreparedStatement historyStmt = connection.prepareStatement(sql);
                historyStmt.setInt(1, volunteerId);

                ResultSet historyRs = historyStmt.executeQuery();
                List<JsonObject> history = new ArrayList<>();

                while (historyRs.next()) {
                    JsonObject incident = new JsonObject();
                    incident.addProperty("incident_id", historyRs.getInt("incident_id"));
                    incident.addProperty("description", historyRs.getString("description"));
                    incident.addProperty("start_datetime", historyRs.getString("start_datetime"));
                    incident.addProperty("end_datetime", historyRs.getString("end_datetime"));
                    incident.addProperty("status", historyRs.getString("status"));
                    history.add(incident);
                }

                Gson gson = new Gson();
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(history));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"success\": false, \"message\": \"Volunteer not found\"}");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"success\": false, \"message\": \"Database error\"}");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}