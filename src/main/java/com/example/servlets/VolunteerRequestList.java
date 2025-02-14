package com.example.servlets;

import java.io.IOException;
import java.io.BufferedReader;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.example.database.tables.EditVolunteerPositionsTable;
import com.example.database.tables.EditVolunteerRequestsTable;
import com.google.gson.Gson;
import com.example.mainClasses.VolunteerPosition;


@WebServlet("/admin/volunteerRequests")
public class VolunteerRequestList extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            EditVolunteerRequestsTable editRequests = new EditVolunteerRequestsTable();

            // Fetch requests as a JSON array
            String requestsJson = editRequests.getAllVolunteerRequestsAsJson();

            // Debug log for validation
            System.out.println("Generated JSON: " + requestsJson);

            // Write only the final JSON array once
            resp.getWriter().write(requestsJson);

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"success\":false, \"message\":\"Server error.\"}");
        }
    }
}

