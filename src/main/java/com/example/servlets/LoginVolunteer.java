package com.example.servlets;

import com.example.database.tables.EditVolunteersTable;
import com.example.mainClasses.Volunteer;
import com.example.database.init.JSON_Converter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginVolunteer extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false); // Get existing session, don't create a new one
        try {
            if (session != null && "volunteer".equals(session.getAttribute("userType"))) {
                response.setStatus(200);
                EditVolunteersTable evt = new EditVolunteersTable();
                Volunteer v = evt.databaseToVolunteer(session.getAttribute("loggedIn").toString());
                response.getWriter().write(v.getFirstname());
            } else {
                response.setStatus(403); // Forbidden if not logged in as a volunteer
            }
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(LoginVolunteer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Read form data directly from request parameters
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        EditVolunteersTable evt = new EditVolunteersTable();

        try {
            // Authenticate the volunteer
            Volunteer v = evt.databaseToVolunteers(username, password);
            if (v != null) {
                // Login successful
                HttpSession session = request.getSession();
                Integer activeUsers = (Integer) request.getServletContext().getAttribute("activeUsers");
                if (activeUsers == null) {
                    activeUsers = 0;
                }
                session.setAttribute("loggedIn", username);
                session.setAttribute("userType", "volunteer");
                session.setAttribute("username", username);
                request.getServletContext().setAttribute("activeUsers", activeUsers + 1);

                response.setStatus(200);
                response.getWriter().write("Login successful.");
            } else if (evt.databaseToVolunteer(username) != null) {
                // Wrong password
                response.setStatus(403);
                response.getWriter().write("Incorrect password.");
            } else {
                // User not found
                response.setStatus(404);
                response.getWriter().write("Volunteer not found.");
            }
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(LoginVolunteer.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(500); // Internal Server Error
            response.getWriter().write("An error occurred during login.");
        }
    }

}
