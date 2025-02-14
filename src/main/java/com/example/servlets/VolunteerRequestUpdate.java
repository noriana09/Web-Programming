package com.example.servlets;

import com.example.database.tables.EditVolunteerPositionsTable;
import com.example.database.tables.EditVolunteerRequestsTable;
import com.example.mainClasses.VolunteerPosition;
import com.example.mainClasses.VolunteerRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/admin/volunteerRequests/update")
public class VolunteerRequestUpdate extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        try {
            // Read and parse JSON payload
            BufferedReader reader = req.getReader();
            Gson gson = new Gson();
            JsonObject requestData = gson.fromJson(reader, JsonObject.class);

            if (!requestData.has("id") || !requestData.has("status")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"success\":false, \"message\":\"Missing required fields: id or status.\"}");
                return;
            }

            int reqId = requestData.get("id").getAsInt();
            String newStatus = requestData.get("status").getAsString();

            // Validate inputs
            System.out.println("Request ID: " + reqId);
            System.out.println("New Status: " + newStatus);

            // Retrieve volunteer request
            EditVolunteerRequestsTable editTable = new EditVolunteerRequestsTable();
            VolunteerRequest request = editTable.getVolunteerRequestById(reqId);

            if (request == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"success\":false, \"message\":\"Request not found.\"}");
                return;
            }

            // Approve logic
            if ("approved".equalsIgnoreCase(newStatus)) {
                EditVolunteerPositionsTable positionTable = new EditVolunteerPositionsTable();

                // Check if positions exist for the incident and type
                boolean isAvailable = positionTable.hasAvailablePositions(request.getIncidentId(), request.getPositionType());

                System.out.println("Incident ID: " + request.getIncidentId());
                System.out.println("Position Type: " + request.getPositionType());
                System.out.println("Are positions available? " + isAvailable);

                if (!isAvailable) {
                    // Automatically reject the request
                    editTable.updateRequestStatus(reqId, "rejected");
                    resp.getWriter().write("{\"success\":false, \"message\":\"No available positions for this incident and position type.\"}");
                    return;
                }

                // Update request and increment slots
                editTable.updateRequestStatus(reqId, "approved");
                positionTable.incrementSlotsFilled(request.getIncidentId(), request.getPositionType());
            } else if ("rejected".equalsIgnoreCase(newStatus)) {
                // Reject the request
                editTable.updateRequestStatus(reqId, "rejected");
            }

            resp.getWriter().write("{\"success\":true}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"success\":false, \"message\":\"Server error.\"}");
        }
    }



}



