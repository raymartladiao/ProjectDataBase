package com.mycompany.barangayrecordsmanagementsystem;

import java.time.LocalDate;

public class BlotterRecord {
    private int blotterId;
    private int complainantId;
    private String complainantName;   // filled by the JOIN query only
    private String respondentName;
    private LocalDate incidentDate;
    private String incidentPlace;
    private String incidentDetails;
    private LocalDate dateFiled;
    private String status;

    public int getBlotterId() { return blotterId; }
    public void setBlotterId(int blotterId) { this.blotterId = blotterId; }

    public int getComplainantId() { return complainantId; }
    public void setComplainantId(int complainantId) { this.complainantId = complainantId; }

    public String getComplainantName() { return complainantName; }
    public void setComplainantName(String complainantName) { this.complainantName = complainantName; }

    public String getRespondentName() { return respondentName; }
    public void setRespondentName(String respondentName) { this.respondentName = respondentName; }

    public LocalDate getIncidentDate() { return incidentDate; }
    public void setIncidentDate(LocalDate incidentDate) { this.incidentDate = incidentDate; }

    public String getIncidentPlace() { return incidentPlace; }
    public void setIncidentPlace(String incidentPlace) { this.incidentPlace = incidentPlace; }

    public String getIncidentDetails() { return incidentDetails; }
    public void setIncidentDetails(String incidentDetails) { this.incidentDetails = incidentDetails; }

    public LocalDate getDateFiled() { return dateFiled; }
    public void setDateFiled(LocalDate dateFiled) { this.dateFiled = dateFiled; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
