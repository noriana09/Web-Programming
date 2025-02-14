package com.example.servlets;


import com.google.gson.JsonArray;

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

@WebServlet("/statistics/usersVolunteersCount")
public class UsersVolunteersCount extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        try (Connection con = DB_Connection.getConnection()) {
            JsonObject jsonResponse = new JsonObject();

            // Count total users
            String usersQuery = "SELECT COUNT(*) AS count FROM users";
            PreparedStatement usersStmt = con.prepareStatement(usersQuery);
            ResultSet usersRs = usersStmt.executeQuery();
            if (usersRs.next()) {
                jsonResponse.addProperty("total_users", usersRs.getInt("count"));
            }

            // Count total volunteers
            String volunteersQuery = "SELECT COUNT(*) AS count FROM volunteers";
            PreparedStatement volunteersStmt = con.prepareStatement(volunteersQuery);
            ResultSet volunteersRs = volunteersStmt.executeQuery();
            if (volunteersRs.next()) {
                jsonResponse.addProperty("total_volunteers", volunteersRs.getInt("count"));
            }

            // Write the combined JSON response
            resp.getWriter().write(jsonResponse.toString());

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"success\":false, \"message\":\"Server error.\"}");
        }
    }
}

