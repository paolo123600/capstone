package com.medicall.capstone.Model;

public class DocRC {
    public DocRC() {
    }



    String FirstName;
    String LastName;
    String UserId;
    String ClinicName;


    public DocRC(String firstName, String lastName, String userId, String clinicName) {
        FirstName = firstName;
        LastName = lastName;
        UserId = userId;
        ClinicName = clinicName;

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

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getClinicName() {
        return ClinicName;
    }

    public void setClinicName(String clinicName) {
        ClinicName = clinicName;
    }


}
