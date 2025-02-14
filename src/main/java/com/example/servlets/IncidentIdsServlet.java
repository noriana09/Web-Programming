package com.example.servlets;

import com.example.DBConnection;
import com.example.mainClasses.Incident;
import com.google.gson.Gson;
import com.example.database.tables.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/incidents/ids")
public class IncidentIdsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            EditIncidentsTable editIncidentsTable = new EditIncidentsTable();
            ArrayList<Integer> incidentIds = editIncidentsTable.getAllIncidentIds();

            Gson gson = new Gson();
            String json = gson.toJson(incidentIds);

            resp.getWriter().write(json);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"success\":false, \"message\":\"Server error.\"}");
        }
    }
}