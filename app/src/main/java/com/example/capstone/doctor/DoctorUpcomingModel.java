package com.example.capstone.doctor;

import java.util.Date;

public class DoctorUpcomingModel {

    private String ClinicName, PatientUId;
    java.util.Date Date;

    private DoctorUpcomingModel() {}

    private DoctorUpcomingModel(Date Date, String ClinicName, String PatientUId) {
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

    public Date getDate() {
        return Date;
    }

    public void setDate(Date date) {
        Date = date;
    }
}
