package com.medicall.capstone;

public class DoctorModel {
    public String getFirstName() {
        return FirstName;
    }

    public DoctorModel() {
    }

    public DoctorModel(String firstName, String lastName, String email, String clinic, String userId, String DocType) {
        FirstName = firstName;
        LastName = lastName;
        Email = email;
        ClinicName = clinic;
        UserId =userId;
        DocType = DocType;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getClinic() {
        return ClinicName;
    }

    public void setClinic(String clinicName) {
        ClinicName = clinicName;
    }
    public String getDocType() {
        return DocType;
    }

    public void setDocType(String docType) {
        DocType = docType;
    }

    private String FirstName;
    private String LastName;
    private String Email;
    private String ClinicName;
    private String UserId ;
    private String DocType;
    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }


}
