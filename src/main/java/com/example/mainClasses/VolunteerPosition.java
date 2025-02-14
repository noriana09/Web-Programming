package com.example.mainClasses;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class VolunteerPosition {
    private int id; // Primary key

    @SerializedName("incident_id")
    private int incidentId; // Foreign key referencing the incident

    @SerializedName("position_type")
    private String positionType; // e.g., "driver", "medic", "general"

    @SerializedName("slots_open")
    private int slotsOpen; // How many slots are available

    @SerializedName("slots_filled")
    private int slotsFilled = 0; // How many slots have been filled (default to 0)

    // Default constructor
    public VolunteerPosition() {}

    // Parameterized constructor
    public VolunteerPosition(int id, int incidentId, String positionType, int slotsOpen, int slotsFilled) {
        this.id = id;
        this.incidentId = incidentId;
        this.positionType = positionType;
        this.slotsOpen = slotsOpen;
        this.slotsFilled = slotsFilled;
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

    public String getPositionType() {
        return positionType;
    }

    public void setPositionType(String positionType) {
        this.positionType = positionType;
    }

    public int getSlotsOpen() {
        return slotsOpen;
    }

    public void setSlotsOpen(int slotsOpen) {
        this.slotsOpen = slotsOpen;
    }

    public int getSlotsFilled() {
        return slotsFilled;
    }

    public void setSlotsFilled(int slotsFilled) {
        this.slotsFilled = slotsFilled;
    }

    public boolean isValid() {
        return incidentId > 0 && positionType != null && !positionType.trim().isEmpty() && slotsOpen > 0;
    }

    @Override
    public String toString() {
        return "VolunteerPosition{" +
                "id=" + id +
                ", incidentId=" + incidentId +
                ", positionType='" + positionType + '\'' +
                ", slotsOpen=" + slotsOpen +
                ", slotsFilled=" + slotsFilled +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VolunteerPosition that = (VolunteerPosition) o;
        return id == that.id && incidentId == that.incidentId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, incidentId);
    }
}
