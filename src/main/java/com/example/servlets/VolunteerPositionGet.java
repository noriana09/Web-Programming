package com.example.servlets;
import com.example.database.DB_Connection;
import com.example.mainClasses.VolunteerPosition;
import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/volunteer/positions")
public class VolunteerPositionGet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try (Connection conn = DB_Connection.getConnection()) {
            // Get volunteer type from the query parameter
            String volunteerType = req.getParameter("volunteer_type");
            if (volunteerType == null || volunteerType.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"success\":false, \"message\":\"Volunteer type is required.\"}");
                return;
            }

            String query = "SELECT * FROM volunteer_positions " +
                    "WHERE position_type = ? AND slots_open > slots_filled";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, volunteerType);

                try (ResultSet rs = stmt.executeQuery()) {
                    List<VolunteerPosition> positions = new ArrayList<>();
                    while (rs.next()) {
                        VolunteerPosition position = new VolunteerPosition();
                        position.setId(rs.getInt("id"));
                        position.setIncidentId(rs.getInt("incident_id"));
                        position.setPositionType(rs.getString("position_type"));
                        position.setSlotsOpen(rs.getInt("slots_open"));
                        position.setSlotsFilled(rs.getInt("slots_filled"));
                        positions.add(position);
                    }

                    // Convert to JSON and send response
                    Gson gson = new Gson();
                    String jsonResponse = gson.toJson(positions);
                    out.write(jsonResponse);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"success\":false, \"message\":\"Database error.\"}");
        }
    }
}


