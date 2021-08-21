package com.example.capstone.secretary;

public class SecretaryPatschedModel {

    private String Date, ClinicName, PatientUId;

    private SecretaryPatschedModel() {}

    private SecretaryPatschedModel(String Date, String ClinicName, String PatientUId) {
        this.ClinicName = ClinicName;
        this.PatientUId = PatientUId;
        this.Date = Date;

    }


    public String getClinicName() {
        return ClinicName;
    }

    public void setClinicName(String clinicName) {
        this.ClinicName = clinicName;
    }


    public String getPatientUId() {
        return PatientUId;
    }

    public void setPatientUId(String patientUId) {
        PatientUId = patientUId;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
