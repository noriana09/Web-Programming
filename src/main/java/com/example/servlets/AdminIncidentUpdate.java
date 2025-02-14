package com.example.servlets;

import com.example.DBConnection;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@WebServlet("/admin/incidents/update")
public class AdminIncidentUpdate extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        Gson gson = new Gson();
        JsonObject body = gson.fromJson(sb.toString(), JsonObject.class);

        int incident_id = body.has("incident_id") ? body.get("incident_id").getAsInt() : -1;
        String newStatus = body.has("status") ? body.get("status").getAsString() : null;
        String newDanger = body.has("danger") ? body.get("danger").getAsString() : null;

        if (incident_id == -1) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"success\": false, \"message\": \"Missing incident ID.\"}");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // Build SQL dynamically based on which fields are provided:
            StringBuilder sqlBuilder = new StringBuilder("UPDATE incidents SET ");
            List<Object> params = new ArrayList<>();

            if (newStatus != null) {
                sqlBuilder.append("status = ?, ");
                params.add(newStatus);

                // If status is "running" and we want to set start time automatically:
                if ("running".equalsIgnoreCase(newStatus)) {
                    sqlBuilder.append("start_datetime = NOW(), ");
                }
                // If status is "finished", set end_datetime automatically:
                if ("finished".equalsIgnoreCase(newStatus)) {
                    sqlBuilder.append("end_datetime = NOW(), ");
                }
                // If status is "fake", no need to do anything special except set status
            }

            if (newDanger != null) {
                sqlBuilder.append("danger = ?, ");
                params.add(newDanger);
            }

            // Remove trailing comma
            int lastComma = sqlBuilder.lastIndexOf(",");
            if (lastComma >= 0) {
                sqlBuilder.deleteCharAt(lastComma);
            }

            sqlBuilder.append(" WHERE incident_id  = ?");
            params.add(incident_id);

            String sql = sqlBuilder.toString();

            PreparedStatement ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            int rows = ps.executeUpdate();
            if (rows > 0) {
                resp.getWriter().write("{\"success\": true, \"message\": \"Incident updated.\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"success\": false, \"message\": \"No rows updated.\"}");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"success\": false, \"message\": \"Database error.\"}");
        }
    }
}
