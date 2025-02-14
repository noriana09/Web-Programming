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

@WebServlet("/getUserDetails")
public class GetUserDetailsServlet extends HttpServlet {
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
            String query = "SELECT * FROM users WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                System.out.println("Executing query: SELECT * FROM users WHERE username = '" + username + "'");

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        JSONObject userDetails = new JSONObject();
                        userDetails.put("success", true);
                        userDetails.put("username", rs.getString("username"));
                        userDetails.put("email", rs.getString("email"));
                        userDetails.put("firstname", rs.getString("firstname"));
                        userDetails.put("lastname", rs.getString("lastname"));
                        userDetails.put("birthdate", rs.getString("birthdate"));
                        userDetails.put("gender", rs.getString("gender"));
                        userDetails.put("afm", rs.getString("afm"));
                        userDetails.put("country", rs.getString("country"));
                        userDetails.put("address", rs.getString("address"));
                        userDetails.put("municipality", rs.getString("municipality"));
                        userDetails.put("prefecture", rs.getString("prefecture"));
                        userDetails.put("job", rs.getString("job"));
                        userDetails.put("telephone", rs.getString("telephone"));
                        userDetails.put("latitude", rs.getString("lat"));
                        userDetails.put("longitude", rs.getString("lon"));

                        out.write(userDetails.toString());
                    } else {
                        System.out.println("No user found with username: " + username);
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