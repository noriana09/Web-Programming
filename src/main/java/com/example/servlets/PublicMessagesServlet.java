package com.example.servlets;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.database.DB_Connection;
import com.example.mainClasses.Message;
import com.google.gson.Gson;


@WebServlet(name = "PublicMessagesServlet", urlPatterns = {"/user/messages/public"})
public class PublicMessagesServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json");

        try (PrintWriter out = resp.getWriter()) {
            List<Message> messages = new ArrayList<>();
            try (Connection connection = DB_Connection.getConnection()) {
                // Query to fetch public messages for active incidents
                String sql = "SELECT m.message_id, m.incident_id, m.message, m.sender, m.recipient, m.date_time " +
                        "FROM messages m " +
                        "JOIN incidents i ON m.incident_id = i.incident_id " +
                        "WHERE m.recipient = 'public' AND i.status = 'running' " +
                        "ORDER BY m.date_time ASC";
                PreparedStatement stmt = connection.prepareStatement(sql);

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Message msg = new Message();
                    msg.setMessage_id(rs.getInt("message_id"));
                    msg.setIncident_id(rs.getInt("incident_id"));
                    msg.setMessage(rs.getString("message"));
                    msg.setSender(rs.getString("sender"));
                    msg.setRecipient(rs.getString("recipient"));
                    msg.setDate_time();
                    messages.add(msg);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            // Return JSON response
            out.write(gson.toJson(messages));

        }
    }
}
