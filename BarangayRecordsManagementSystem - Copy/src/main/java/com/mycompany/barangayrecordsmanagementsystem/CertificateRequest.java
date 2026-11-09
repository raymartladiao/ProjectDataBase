package com.mycompany.barangayrecordsmanagementsystem;

import java.time.LocalDate;

public class CertificateRequest {
    private int requestId;
    private int residentId;
    private String residentName;      // filled by the JOIN query only
    private String certificateType;
    private String purpose;
    private LocalDate dateRequested;
    private LocalDate dateIssued;     // may be null (not issued yet)
    private String barangayVoter;    // "Yes" or "No"
    private String electorate;        // "Yes" or "No"
    private String status;

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public int getResidentId() { return residentId; }
    public void setResidentId(int residentId) { this.residentId = residentId; }

    public String getResidentName() { return residentName; }
    public void setResidentName(String residentName) { this.residentName = residentName; }

    public String getCertificateType() { return certificateType; }
    public void setCertificateType(String certificateType) { this.certificateType = certificateType; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public LocalDate getDateRequested() { return dateRequested; }
    public void setDateRequested(LocalDate dateRequested) { this.dateRequested = dateRequested; }

    public LocalDate getDateIssued() { return dateIssued; }
    public void setDateIssued(LocalDate dateIssued) { this.dateIssued = dateIssued; }

    public String getBarangayVoter() { return barangayVoter; }
    public void setBarangayVoter(String barangayVoter) { this.barangayVoter = barangayVoter; }

    public String getElectorate() { return electorate; }
    public void setElectorate(String electorate) { this.electorate = electorate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
