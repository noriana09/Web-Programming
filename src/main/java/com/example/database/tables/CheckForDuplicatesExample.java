/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.database.tables;

import com.example.database.DB_Connection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author micha
 */
public class CheckForDuplicatesExample {
    
    public boolean isUserNameAvailable(String username) throws SQLException, ClassNotFoundException{
         Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();

        ResultSet rs;
        try {
            rs = stmt.executeQuery("SELECT COUNT(username) AS total FROM users WHERE username = '" + username + "'");
            rs.next(); 
            if(rs.getInt("total")==0){
                 rs = stmt.executeQuery("SELECT COUNT(username) AS total2 FROM volunteers WHERE username = '" + username + "'");
                 rs.next();
                 if(rs.getInt("total2")==0){
                     return true;
                 }
             }
             return false;
          
        } catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        }
    return false;
    }

    
    
}
