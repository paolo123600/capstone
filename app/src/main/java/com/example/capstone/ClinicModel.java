package com.example.capstone;

public class ClinicModel {



    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userID) {
        this.UserId = userID;
    }

    public ClinicModel(String clinicName, String userId) {
        ClinicName = clinicName;
        UserId = userId;
    }

    private String ClinicName;
    private String UserId;

    private ClinicModel() {}



    public String getClinicName() {
        return ClinicName;
    }

    public void setClinicName(String clinicName) {
        ClinicName = clinicName;
    }
}
