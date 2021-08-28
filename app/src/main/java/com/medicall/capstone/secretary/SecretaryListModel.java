package com.medicall.capstone.secretary;

public class SecretaryListModel {

    private String FirstName, LastName, DocType, ClinicName, UserId;

    private SecretaryListModel() {}

    private SecretaryListModel(String FirstName, String LastName, String DocType, String ClinicName, String UserId) {
        this.FirstName = FirstName;
        this.LastName = LastName;
        this.DocType = DocType;
        this.ClinicName = ClinicName;
        this.UserId = UserId;

    }

    public String getFirstName() {
        return FirstName;
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

    public String getDocType() {
        return DocType;
    }

    public void setDocType(String docType) {
        DocType = docType;
    }

    public String getClinicName() {
        return ClinicName;
    }

    public void setClinicName(String clinicName) {
        this.ClinicName = clinicName;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }
}