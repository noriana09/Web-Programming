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

@WebServlet(name = "VolunteerMessagesServlet", urlPatterns = {"/volunteer/messages"})
public class VolunteerMessagesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"success\": false, \"message\": \"User not logged in\"}");
            return;
        }

        String username = (String) session.getAttribute("username");
        if (username == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"success\": false, \"message\": \"Username not found in session\"}");
            return;
        }

        try (Connection connection = DB_Connection.getConnection()) {
            // Fetch volunteer ID
            String volunteerQuery = "SELECT volunteer_id FROM volunteers WHERE username = ?";
            PreparedStatement volunteerStmt = connection.prepareStatement(volunteerQuery);
            volunteerStmt.setString(1, username);
            ResultSet volunteerRs = volunteerStmt.executeQuery();

            if (volunteerRs.next()) {
                int volunteerId = volunteerRs.getInt("volunteer_id");

                // Fetch messages for the incidents the volunteer is involved in
                String messagesQuery =
                        "SELECT m.incident_id, m.sender, m.recipient, m.message, m.date_time " +
                                "FROM messages m " +
                                "JOIN volunteer_requests vr ON m.incident_id = vr.incident_id " +
                                "WHERE vr.volunteer_id = ? AND vr.status = 'approved' " +
                                "AND (m.recipient = 'public' OR m.recipient = 'volunteers')";

                PreparedStatement messagesStmt = connection.prepareStatement(messagesQuery);
                messagesStmt.setInt(1, volunteerId);

                ResultSet messagesRs = messagesStmt.executeQuery();
                List<JsonObject> messages = new ArrayList<>();

                while (messagesRs.next()) {
                    JsonObject message = new JsonObject();
                    message.addProperty("incident_id", messagesRs.getInt("incident_id"));
                    message.addProperty("sender", messagesRs.getString("sender"));
                    message.addProperty("recipient", messagesRs.getString("recipient"));
                    message.addProperty("content", messagesRs.getString("message"));
                    message.addProperty("timestamp", messagesRs.getString("date_time"));
                    messages.add(message);
                }

                Gson gson = new Gson();
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(messages));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"success\": false, \"message\": \"Volunteer not found\"}");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"success\": false, \"message\": \"Database error\"}");
        }
    }
}