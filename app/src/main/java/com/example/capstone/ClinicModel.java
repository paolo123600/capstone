package com.example.capstone;

public class ClinicModel {

    private String ClinicName;

    private ClinicModel() {}

    private ClinicModel(String clinicName) {
        ClinicName = clinicName;
    }

    public String getClinicName() {
        return ClinicName;
    }

    public void setClinicName(String clinicName) {
        ClinicName = clinicName;
    }
}
