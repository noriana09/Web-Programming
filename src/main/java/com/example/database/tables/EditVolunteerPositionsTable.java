package com.example.database.tables;

import com.example.database.DB_Connection;
import com.example.mainClasses.VolunteerPosition;
import com.example.mainClasses.VolunteerRequest;
import com.google.gson.Gson;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EditVolunteerPositionsTable {

    /**
     * Add a volunteer position to the database from a JSON object.
     */
    public void addVolunteerPositionFromJSON(String json) throws ClassNotFoundException {
        VolunteerPosition position = jsonToVolunteerPosition(json);
        addNewVolunteerPosition(position);
    }

    /**
     * Convert JSON to VolunteerPosition object.
     */
    public VolunteerPosition jsonToVolunteerPosition(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, VolunteerPosition.class);
    }

    /**
     * Convert VolunteerPosition object to JSON.
     */
    public String volunteerPositionToJSON(VolunteerPosition position) {
        Gson gson = new Gson();
        return gson.toJson(position, VolunteerPosition.class);
    }

    /**
     * Add a new volunteer position to the database.
     */
    public void addNewVolunteerPosition(VolunteerPosition position) throws ClassNotFoundException {
        try (Connection con = DB_Connection.getConnection();
             Statement stmt = con.createStatement()) {

            String insertQuery = "INSERT INTO volunteer_positions (incident_id, position_type, slots_open, slots_filled) " +
                    "VALUES (" +
                    position.getIncidentId() + ", " +
                    "'" + position.getPositionType() + "', " +
                    position.getSlotsOpen() + ", " +
                    position.getSlotsFilled() + ")";

            stmt.executeUpdate(insertQuery);
            System.out.println("# Volunteer position successfully added to the database.");

        } catch (SQLException ex) {
            Logger.getLogger(EditVolunteerPositionsTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Retrieve all positions for a given incident ID.
     */
    public List<VolunteerPosition> getVolunteerPositionsByIncident(int incidentId) throws ClassNotFoundException {
        List<VolunteerPosition> positions = new ArrayList<>();

        try (Connection con = DB_Connection.getConnection();
             Statement stmt = con.createStatement()) {

            String query = "SELECT * FROM volunteer_positions WHERE incident_id = " + incidentId;
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                VolunteerPosition position = new VolunteerPosition();
                position.setId(rs.getInt("id"));
                position.setIncidentId(rs.getInt("incident_id"));
                position.setPositionType(rs.getString("position_type"));
                position.setSlotsOpen(rs.getInt("slots_open"));
                position.setSlotsFilled(rs.getInt("slots_filled"));
                positions.add(position);
            }

        } catch (SQLException ex) {
            Logger.getLogger(EditVolunteerPositionsTable.class.getName()).log(Level.SEVERE, null, ex);
        }

        return positions;
    }

    /**
     * Add a new volunteer request.
     */
    public void addVolunteerRequest(VolunteerRequest request) throws ClassNotFoundException {
        try (Connection con = DB_Connection.getConnection();
             Statement stmt = con.createStatement()) {

            String insertQuery = "INSERT INTO volunteer_requests (incident_id, volunteer_id, position_type, status) " +
                    "VALUES (" +
                    request.getIncidentId() + ", " +
                    request.getVolunteerId() + ", " +
                    "'" + request.getPositionType() + "', " +
                    "'" + request.getStatus() + "')";

            stmt.executeUpdate(insertQuery);
            System.out.println("# Volunteer request successfully added to the database.");

        } catch (SQLException ex) {
            Logger.getLogger(EditVolunteerPositionsTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Retrieve all volunteer requests for a given incident ID.
     */
    public List<VolunteerRequest> getVolunteerRequestsByIncident(int incidentId) throws ClassNotFoundException {
        List<VolunteerRequest> requests = new ArrayList<>();

        try (Connection con = DB_Connection.getConnection();
             Statement stmt = con.createStatement()) {

            String query = "SELECT * FROM volunteer_requests WHERE incident_id = " + incidentId;
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                VolunteerRequest request = new VolunteerRequest();
                request.setId(rs.getInt("id"));
                request.setIncidentId(rs.getInt("incident_id"));
                request.setVolunteerId(rs.getInt("volunteer_id"));
                request.setPositionType(rs.getString("position_type"));
                request.setStatus(rs.getString("status"));
                requests.add(request);
            }

        } catch (SQLException ex) {
            Logger.getLogger(EditVolunteerPositionsTable.class.getName()).log(Level.SEVERE, null, ex);
        }

        return requests;
    }

    /**
     * Create the `volunteer_positions` table.
     */
    public void createVolunteerPositionsTable() throws ClassNotFoundException {
        try (Connection con = DB_Connection.getConnection();
             Statement stmt = con.createStatement()) {

            String query = "CREATE TABLE volunteer_positions (" +
                    "id INT NOT NULL AUTO_INCREMENT, " +
                    "incident_id INT NOT NULL, " +
                    "position_type VARCHAR(50) NOT NULL, " +
                    "slots_open INT NOT NULL, " +
                    "slots_filled INT NOT NULL, " +
                    "PRIMARY KEY (id))";

            stmt.execute(query);
            System.out.println("# The volunteer_positions table was successfully created.");

        } catch (SQLException ex) {
            Logger.getLogger(EditVolunteerPositionsTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Create the `volunteer_requests` table.
     */
    public void createVolunteerRequestsTable() throws ClassNotFoundException {
        try (Connection con = DB_Connection.getConnection();
             Statement stmt = con.createStatement()) {

            String query = "CREATE TABLE volunteer_requests (" +
                    "id INT NOT NULL AUTO_INCREMENT, " +
                    "incident_id INT NOT NULL, " +
                    "volunteer_id INT NOT NULL, " +
                    "position_type VARCHAR(50) NOT NULL, " +
                    "status VARCHAR(20) NOT NULL DEFAULT 'pending', " +
                    "request_date DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "PRIMARY KEY (id))";

            stmt.execute(query);
            System.out.println("# The volunteer_requests table was successfully created.");

        } catch (SQLException ex) {
            Logger.getLogger(EditVolunteerPositionsTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addVolunteerPosition(VolunteerPosition position) throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();

        String query = "INSERT INTO volunteer_positions (incident_id, position_type, slots_open, slots_filled) VALUES (" +
                position.getIncidentId() + ", '" +
                position.getPositionType() + "', " +
                position.getSlotsOpen() + ", 0)";

        stmt.executeUpdate(query);
        stmt.close();
        con.close();
    }

    public VolunteerPosition getVolunteerPosition(int incidentId, String positionType) throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String query = "SELECT * FROM volunteer_positions WHERE incident_id = ? AND position_type = ?";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, incidentId);
            stmt.setString(2, positionType);

            rs = stmt.executeQuery();
            if (rs.next()) {
                VolunteerPosition position = new VolunteerPosition();
                position.setId(rs.getInt("id"));
                position.setIncidentId(rs.getInt("incident_id"));
                position.setPositionType(rs.getString("position_type"));
                position.setSlotsOpen(rs.getInt("slots_open"));
                position.setSlotsFilled(rs.getInt("slots_filled"));
                return position;
            }
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (con != null) con.close();
        }
        return null; // Return null if no position found
    }

    public void incrementSlotsFilled(int incidentId, String positionType) throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        PreparedStatement stmt = null;

        try {
            String query = "UPDATE volunteer_positions SET slots_filled = slots_filled + 1 WHERE incident_id = ? AND position_type = ?";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, incidentId);
            stmt.setString(2, positionType);

            stmt.executeUpdate();
        } finally {
            if (stmt != null) stmt.close();
            if (con != null) con.close();
        }
    }


    public boolean isPositionAvailable(int incidentId, String positionType) {
        try (Connection con = DB_Connection.getConnection()) {
            String query = "SELECT slots_open, slots_filled FROM volunteer_positions WHERE incident_id = ? AND position_type = ?";
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setInt(1, incidentId);
                pst.setString(2, positionType);

                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        int slotsOpen = rs.getInt("slots_open");
                        int slotsFilled = rs.getInt("slots_filled");

                        // Check if there are still available slots
                        return slotsFilled < slotsOpen;
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false; // Return false if the position doesn't exist or if an error occurs
    }

    public boolean hasAvailablePositions(int incidentId, String positionType) throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String query = "SELECT * FROM volunteer_positions WHERE incident_id = ? AND position_type = ? AND slots_filled < slots_open";
            stmt = con.prepareStatement(query);
            stmt.setInt(1, incidentId);
            stmt.setString(2, positionType);

            rs = stmt.executeQuery();
            return rs.next(); // If a row exists, then a position is available
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (con != null) con.close();
        }
    }



}