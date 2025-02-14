package com.example.mainClasses;


import com.google.gson.annotations.SerializedName;

import java.io.BufferedReader;

public class VolunteerRequest {
    private int id; // Primary key

    @SerializedName("incident_id")
    private int incidentId; // Foreign key referencing the incident

    @SerializedName("volunteer_id")
    private int volunteerId; // User ID of the volunteer

    @SerializedName("position_type")
    private String positionType; // The type of position requested

    @SerializedName("status")
    private String status; // e.g., "pending", "approved", "rejected"

    @SerializedName("request_data")
    private String requestDate; // Date of the request

    // Default constructor
    public VolunteerRequest() {}

    // Parameterized constructor
    public VolunteerRequest(int id, int incidentId, int volunteerId, String positionType, String status, String requestDate) {
        this.id = id;
        this.incidentId = incidentId;
        this.volunteerId = volunteerId;
        this.positionType = positionType;
        this.status = status;
        this.requestDate = requestDate;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(int incidentId) {
        this.incidentId = incidentId;
    }

    public int getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(int volunteerId) {
        this.volunteerId = volunteerId;
    }

    public String getPositionType() {
        return positionType;
    }

    public void setPositionType(String positionType) {
        this.positionType = positionType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    @Override
    public String toString() {
        return "VolunteerRequest{" +
                "id=" + id +
                ", incidentId=" + incidentId +
                ", volunteerId=" + volunteerId +
                ", positionType='" + positionType + '\'' +
                ", status='" + status + '\'' +
                ", requestDate='" + requestDate + '\'' +
                '}';
    }

}
