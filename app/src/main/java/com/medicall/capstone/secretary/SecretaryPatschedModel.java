package com.medicall.capstone.secretary;

import java.util.Date;

public class SecretaryPatschedModel {

    private String ClinicName, PatientUId;

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    String EndTime , StartTime;
    Date Date;

    private SecretaryPatschedModel() {}

    private SecretaryPatschedModel(Date Date, String ClinicName, String PatientUId) {
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
