package com.example.servlets;

import com.example.database.init.JSON_Converter;
import com.example.database.tables.*;
import com.example.mainClasses.Admin;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginAdmin extends HttpServlet {
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
            HttpSession session = request.getSession();
            session.setAttribute("loggedIn", true);
            session.setAttribute("userType", "admin");
            session.setAttribute("username", username);
            response.setStatus(200);
            response.getWriter().write("admin");
        } else {
            response.setStatus(403); // Forbidden
            response.getWriter().write("Invalid admin credentials.");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if(session.getAttribute("userType") == "admin"){
            response.setStatus(200);
            response.getWriter().write("admin");
        }else{
            response.setStatus(403);
        }
    }
}
