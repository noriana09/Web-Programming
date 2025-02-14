package com.example.servlets;

import java.io.IOException;
import java.io.BufferedReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.database.DB_Connection;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@WebServlet("/messages/send")
public class MessageSendAdmin extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            // Parse the incoming JSON
            BufferedReader reader = req.getReader();
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(reader, JsonObject.class);

            int incidentId = json.get("incident_id").getAsInt();
            String sender = json.get("sender").getAsString();
            String recipient = json.get("recipient").getAsString();
            String message = json.get("message").getAsString();

            // Insert message into the database
            Connection con = DB_Connection.getConnection();
            String query = "INSERT INTO messages (incident_id, message, sender, recipient, date_time) VALUES (?, ?, ?, ?, NOW())";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, incidentId);
            stmt.setString(2, message);
            stmt.setString(3, sender);
            stmt.setString(4, recipient);
            stmt.executeUpdate();

            resp.getWriter().write("{\"success\":true}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"success\":false, \"message\":\"Server error.\"}");
        }
    }
}
