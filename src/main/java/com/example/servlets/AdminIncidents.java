package com.example.servlets;

import com.example.DBConnection;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.BufferedReader;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.database.DB_Connection;  // Adjust your package
import com.example.mainClasses.Incident;      // Adjust your package

@WebServlet("/admin/incidents")
public class AdminIncidents extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        List<Incident> incidentList = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {

            String sql = "SELECT " +
                    "incident_id, incident_type, description, user_phone, user_type, " +
                    "address, prefecture, municipality, start_datetime, end_datetime, " +
                    "danger, status, finalResult, lat, lon, vehicles, firemen " +
                    "FROM incidents";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            // We'll use this to format the Timestamps to string (yyyy/MM/dd HH:mm:ss):
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

            while (rs.next()) {
                // Create a new Incident (which has String fields)
                Incident inc = new Incident();

                // Integers
                inc.setIncident_id(rs.getInt("incident_id"));
                inc.setVehicles(rs.getInt("vehicles"));
                inc.setFiremen(rs.getInt("firemen"));

                // Doubles
                inc.setLat(rs.getDouble("lat"));
                inc.setLon(rs.getDouble("lon"));

                // Strings
                inc.setIncident_type(rs.getString("incident_type"));
                inc.setDescription(rs.getString("description"));
                inc.setUser_phone(rs.getString("user_phone"));
                inc.setUser_type(rs.getString("user_type"));
                inc.setAddress(rs.getString("address"));
                inc.setPrefecture(rs.getString("prefecture"));
                inc.setMunicipality(rs.getString("municipality"));
                inc.setDanger(rs.getString("danger"));
                inc.setStatus(rs.getString("status"));
                inc.setFinalResult(rs.getString("finalResult"));

                // Convert start_datetime Timestamp → String
                Timestamp startTs = rs.getTimestamp("start_datetime");
                if (startTs != null) {
                    LocalDateTime startLdt = startTs.toLocalDateTime();
                    inc.start_datetime = dtf.format(startLdt);
                } else {
                    inc.start_datetime = null;
                }

                // Convert end_datetime Timestamp → String
                Timestamp endTs = rs.getTimestamp("end_datetime");
                if (endTs != null) {
                    LocalDateTime endLdt = endTs.toLocalDateTime();
                    inc.setEnd_datetime(dtf.format(endLdt));
                } else {
                    inc.setEnd_datetime(null);
                }

                // Add to list
                incidentList.add(inc);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"success\": false, \"message\": \"Database error.\"}");
            return;
        }

        // Convert the list to JSON
        Gson gson = new Gson();
        String json = gson.toJson(incidentList);
        resp.getWriter().write(json);
    }
}
