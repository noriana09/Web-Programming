package com.example.servlets;

import com.example.database.tables.EditUsersTable;
import com.example.database.tables.EditVolunteersTable;
import com.example.mainClasses.User;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginUser extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        try {
            if (session != null && "user".equals(session.getAttribute("userType"))) {
                response.setStatus(200);
                EditUsersTable eut = new EditUsersTable();
                User u = eut.databaseToUser(session.getAttribute("loggedIn").toString());
                response.getWriter().write(u.getFirstname());
            } else {
                response.setStatus(403); // Forbidden if not logged in as a volunteer
            }
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(LoginUser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        EditUsersTable eut = new EditUsersTable();
        try {
            User user = eut.databaseToUsers(username, password); // Call the updated method
            if (user != null) {
                HttpSession session = request.getSession();
                session.setAttribute("loggedIn", username);
                session.setAttribute("userType", "user"); // Or another type as needed
                session.setAttribute("username", username);
                response.setStatus(200);
                response.getWriter().write("{\"success\": true, \"message\": \"Login successful.\"}");
            } else {
                response.setStatus(403); // Forbidden if credentials are incorrect
                response.getWriter().write("Invalid credentials.");
            }
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(LoginUser.class.getName()).log(Level.SEVERE, null, ex);
            response.setStatus(500); // Internal Server Error
            response.getWriter().write("An error occurred during login.");
        }
    }


}