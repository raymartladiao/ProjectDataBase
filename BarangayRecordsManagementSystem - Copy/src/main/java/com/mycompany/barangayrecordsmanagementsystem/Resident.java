package com.mycompany.barangayrecordsmanagementsystem;

import java.time.LocalDate;

public class Resident {
    private int residentId;
    private String firstName;
    private String middleName;     // optional, may be null
    private String lastName;
    private String sex;
    private LocalDate birthDate;
    private String address;
    private String contactNo;
    private String civilStatus;

    public int getResidentId() { return residentId; }
    public void setResidentId(int residentId) { this.residentId = residentId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContactNo() { return contactNo; }
    public void setContactNo(String contactNo) { this.contactNo = contactNo; }

    public String getCivilStatus() { return civilStatus; }
    public void setCivilStatus(String civilStatus) { this.civilStatus = civilStatus; }
}
