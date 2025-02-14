package com.example.database.tables;


import com.google.gson.Gson;
import com.example.database.DB_Connection;
import com.example.mainClasses.VolunteerRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EditVolunteerRequestsTable {

    public String getAllVolunteerRequestsAsJson() throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM volunteer_requests");

        JsonArray jsonArray = new JsonArray();
        ResultSetMetaData metadata = rs.getMetaData();
        int columnCount = metadata.getColumnCount();

        while (rs.next()) {
            JsonObject jsonObject = new JsonObject();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metadata.getColumnName(i);
                String value = rs.getString(i);
                jsonObject.addProperty(columnName, value != null ? value : "");
            }
            jsonArray.add(jsonObject);
        }

        return jsonArray.toString(); // Only a single JSON array
    }


    public void updateRequestStatus(int requestId, String status) throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();

        String update = "UPDATE volunteer_requests SET status='" + status + "' WHERE id=" + requestId;
        stmt.executeUpdate(update);

        stmt.close();
        con.close();
    }

    public VolunteerRequest getVolunteerRequestById(int requestId) throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String query = "SELECT * FROM volunteer_requests WHERE id = ?";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, requestId);

            rs = stmt.executeQuery();
            if (rs.next()) {
                VolunteerRequest request = new VolunteerRequest();
                request.setId(rs.getInt("id"));
                request.setIncidentId(rs.getInt("incident_id"));
                request.setVolunteerId(rs.getInt("volunteer_id"));
                request.setPositionType(rs.getString("position_type"));
                request.setStatus(rs.getString("status"));
                request.setRequestDate(rs.getString("request_date"));
                return request;
            }
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (con != null) con.close();
        }
        return null; // Return null if no request found
    }
}