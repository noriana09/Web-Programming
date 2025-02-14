package com.example;

import org.json.JSONObject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/getVolunteerDetails")
public class GetVolunteerDetails extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        HttpSession session = request.getSession(false);
        PrintWriter out = response.getWriter();

        if (session == null) {
            System.out.println("No active session.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.write("{\"success\":false,\"message\":\"No active session.\"}");
            return;
        }

        String username = (String) session.getAttribute("username");
        if (username == null) {
            System.out.println("Username is not set in the session.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.write("{\"success\":false,\"message\":\"User is not logged in.\"}");
            return;
        }

        //System.out.println("Retrieved username: " + username);

        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM volunteers WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                System.out.println("Executing query: SELECT * FROM volunteers WHERE username = '" + username + "'");

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        JSONObject volunteerDetails = new JSONObject();
                        volunteerDetails.put("success", true);
                        volunteerDetails.put("volunteer_id", rs.getString("volunteer_id"));
                        volunteerDetails.put("username", rs.getString("username"));
                        volunteerDetails.put("email", rs.getString("email"));
                        volunteerDetails.put("firstname", rs.getString("firstname"));
                        volunteerDetails.put("lastname", rs.getString("lastname"));
                        volunteerDetails.put("birthdate", rs.getString("birthdate"));
                        volunteerDetails.put("gender", rs.getString("gender"));
                        volunteerDetails.put("afm", rs.getString("afm"));
                        volunteerDetails.put("country", rs.getString("country"));
                        volunteerDetails.put("address", rs.getString("address"));
                        volunteerDetails.put("municipality", rs.getString("municipality"));
                        volunteerDetails.put("prefecture", rs.getString("prefecture"));
                        volunteerDetails.put("job", rs.getString("job"));
                        volunteerDetails.put("telephone", rs.getString("telephone"));
                        volunteerDetails.put("latitude", rs.getString("lat"));
                        volunteerDetails.put("longitude", rs.getString("lon"));
                        volunteerDetails.put("volunteer_type", rs.getString("volunteer_type"));
                        volunteerDetails.put("height", rs.getString("height"));
                        volunteerDetails.put("weight", rs.getString("weight"));


                        out.write(volunteerDetails.toString());
                    } else {
                        System.out.println("No volunteer found with username: " + username);
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.write("{\"success\":false,\"message\":\"User not found.\"}");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"success\":false,\"message\":\"Database error occurred.\"}");
        }
    }
}