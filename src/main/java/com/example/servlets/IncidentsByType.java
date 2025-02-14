package com.example.servlets;

import javax.servlet.annotation.WebServlet;
import com.example.DBConnection;
import com.example.database.DB_Connection;
import com.example.mainClasses.Incident;
import com.google.gson.Gson;
import com.example.database.tables.*;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/statistics/incidentsByType")
public class IncidentsByType extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try (Connection con = DB_Connection.getConnection();
             Statement stmt = con.createStatement()) {

            String query = "SELECT incident_type, COUNT(*) AS count FROM incidents GROUP BY incident_type";
            ResultSet rs = stmt.executeQuery(query);

            List<JsonObject> result = new ArrayList<>();
            while (rs.next()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("incident_type", rs.getString("incident_type"));
                obj.addProperty("count", rs.getInt("count"));
                result.add(obj);
            }

            resp.getWriter().write(new Gson().toJson(result));
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Server error\"}");
        }
    }
}
