package com.medicall.capstone.Model;

public class SecRC {
    String ClinicName , UserId;

    public SecRC(String clinicName, String userId) {
        ClinicName = clinicName;
        UserId = userId;

    }

    public String getClinicName() {
        return ClinicName;
    }

    public void setClinicName(String clinicName) {
        ClinicName = clinicName;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }



    public SecRC() {
    }
}
