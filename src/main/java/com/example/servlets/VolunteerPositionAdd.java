package com.example.servlets;

import java.io.IOException;
import java.io.BufferedReader;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.example.database.tables.EditVolunteerPositionsTable;
import com.google.gson.Gson;
import com.example.mainClasses.VolunteerPosition;


@WebServlet("/admin/volunteerPositions/new")
public class VolunteerPositionAdd extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        try {
            // Parse the incoming JSON
            BufferedReader reader = req.getReader();
            Gson gson = new Gson();
            VolunteerPosition position = gson.fromJson(reader, VolunteerPosition.class);

            // Validate data
            if (position.getIncidentId() == 0 || position.getPositionType() == null || position.getSlotsOpen() <= 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"success\":false, \"message\":\"Invalid input data.\"}");
                return;
            }

            // Add position to the database
            EditVolunteerPositionsTable editTable = new EditVolunteerPositionsTable();
            editTable.addVolunteerPosition(position);

            resp.getWriter().write("{\"success\":true}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"success\":false, \"message\":\"Server error.\"}");
        }
    }
}