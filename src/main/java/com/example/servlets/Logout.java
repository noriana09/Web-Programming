package com.example.servlets;

import javax.servlet.http.*;
import java.io.IOException;

public class Logout extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request , HttpServletResponse response){
        HttpSession session = request.getSession();
        if(session.getAttribute("loggedIn") != null){
            session.invalidate();
            request.getServletContext().setAttribute("activeUsers" , null);
            response.setStatus(200);
        }else{
            response.setStatus(403);
        }
    }
}
