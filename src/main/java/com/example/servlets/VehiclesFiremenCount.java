package com.example.servlets;

import com.example.database.DB_Connection;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/statistics/vehiclesFiremenCount")
public class VehiclesFiremenCount extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try (Connection con = DB_Connection.getConnection()) {
            String query = "SELECT SUM(vehicles) AS total_vehicles, SUM(firemen) AS total_firemen FROM incidents";
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            JsonObject json = new JsonObject();
            if (rs.next()) {
                json.addProperty("total_vehicles", rs.getInt("total_vehicles"));
                json.addProperty("total_firemen", rs.getInt("total_firemen"));
            }

            resp.getWriter().write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"success\":false, \"message\":\"Server error.\"}");
        }
    }
}
