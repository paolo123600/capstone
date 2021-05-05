package com.example.capstone.Model;

public class SecRC {
    String ClinicName , UserId, Type;

    public SecRC(String clinicName, String userId, String type) {
        ClinicName = clinicName;
        UserId = userId;
        Type = type;
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

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public SecRC() {
    }
}
