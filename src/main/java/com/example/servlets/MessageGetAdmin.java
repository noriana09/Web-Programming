package com.example.servlets;

import java.io.IOException;
import java.io.BufferedReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.database.DB_Connection;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@WebServlet("/messages/get")
public class MessageGetAdmin extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            int incidentId = Integer.parseInt(req.getParameter("incident_id"));

            Connection con = DB_Connection.getConnection();
            String query = "SELECT * FROM messages WHERE incident_id = ? ORDER BY date_time ASC";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, incidentId);

            ResultSet rs = stmt.executeQuery();
            List<JsonObject> messages = new ArrayList<>();
            while (rs.next()) {
                JsonObject message = new JsonObject();
                message.addProperty("message_id", rs.getInt("message_id"));
                message.addProperty("incident_id", rs.getInt("incident_id"));
                message.addProperty("message", rs.getString("message"));
                message.addProperty("sender", rs.getString("sender"));
                message.addProperty("recipient", rs.getString("recipient"));
                message.addProperty("date_time", rs.getString("date_time"));
                messages.add(message);
            }

            resp.getWriter().write(new Gson().toJson(messages));
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"success\":false, \"message\":\"Server error.\"}");
        }
    }
}