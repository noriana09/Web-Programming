package com.example.servlets;


import java.io.IOException;
import java.io.BufferedReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.database.DB_Connection;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@WebServlet(name = "UserMessageSendServlet", urlPatterns = {"/user/messages/send"})
public class UserMessageSendServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        try {
            // Parse JSON request
            BufferedReader reader = req.getReader();
            JsonObject jsonRequest = gson.fromJson(reader, JsonObject.class);

            int incident_id = jsonRequest.get("incident_id").getAsInt();
            String recipient = jsonRequest.get("recipient").getAsString();
            String message = jsonRequest.get("message").getAsString();
            String sender = jsonRequest.get("sender").getAsString();

            // Validate input
            if (incident_id <= 0 || recipient.isEmpty() || message.isEmpty() || sender.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"success\": false, \"message\": \"Invalid input data\"}");
                return;
            }

            // Save the message to the database
            try (Connection connection = DB_Connection.getConnection()) {
                String sql = "INSERT INTO messages (incident_id, message, sender, recipient, date_time) VALUES (?, ?, ?, ?, NOW())";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setInt(1, incident_id);
                stmt.setString(2, message);
                stmt.setString(3, sender);
                stmt.setString(4, recipient);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    resp.getWriter().write("{\"success\": true, \"message\": \"Message sent successfully\"}");
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().write("{\"success\": false, \"message\": \"Failed to send message\"}");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"success\": false, \"message\": \"Database error\"}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"success\": false, \"message\": \"An unexpected error occurred\"}");
        }
    }
}