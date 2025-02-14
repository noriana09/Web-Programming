package com.example.servlets;

import com.example.DBConnection;
import com.example.database.DB_Connection;
import com.example.mainClasses.VolunteerRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Collectors;


@WebServlet("/volunteer/request")
public class VolunteerRequestAdd extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        JSONObject jsonResponse = new JSONObject();

        try (BufferedReader reader = request.getReader()) {
            Gson gson = new Gson();
            VolunteerRequest volunteerRequest = gson.fromJson(reader, VolunteerRequest.class);

            // Validate the incoming request data
            if (volunteerRequest.getIncidentId() == 0 ||
                    volunteerRequest.getVolunteerId() == 0 ||
                    volunteerRequest.getPositionType() == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Invalid input data.");
                response.getWriter().write(jsonResponse.toString());
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                String query = "INSERT INTO volunteer_requests (incident_id, volunteer_id, position_type, status, request_date) VALUES (?, ?, ?, ?, NOW())";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, volunteerRequest.getIncidentId());
                    stmt.setInt(2, volunteerRequest.getVolunteerId());
                    stmt.setString(3, volunteerRequest.getPositionType());
                    stmt.setString(4, volunteerRequest.getStatus());

                    int rowsAffected = stmt.executeUpdate();

                    if (rowsAffected > 0) {
                        jsonResponse.put("success", true);
                        jsonResponse.put("message", "Request submitted successfully.");
                    } else {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        jsonResponse.put("success", false);
                        jsonResponse.put("message", "Failed to submit request.");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Server error.");
        }

        response.getWriter().write(jsonResponse.toString());
    }
}
