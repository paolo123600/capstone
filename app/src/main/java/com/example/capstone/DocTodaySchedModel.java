package com.example.capstone;

import java.util.Date;

public class DocTodaySchedModel {
    public DocTodaySchedModel(String doctorUId, String patientUId, String startTime, String endTime, int position, String date, String status, String clinicname, Date dnt) {
        DoctorUId = doctorUId;
        PatientUId = patientUId;
        StartTime = startTime;
        EndTime = endTime;
        Position = position;
        Date = date;
        Status = status;
        Clinicname = clinicname;
        Dnt = dnt;
    }

    public String getDoctorUId() {
        return DoctorUId;
    }

    public void setDoctorUId(String doctorUId) {
        DoctorUId = doctorUId;
    }

    public String getPatientUId() {
        return PatientUId;
    }

    public void setPatientUId(String patientUId) {
        PatientUId = patientUId;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public int getPosition() {
        return Position;
    }

    public void setPosition(int position) {
        Position = position;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public DocTodaySchedModel() {
    }

    String DoctorUId;
    String PatientUId;
    String StartTime;
    String EndTime;
    String Date;
    String Status;
    String Clinicname;
    Date Dnt;
    int Position;

    public java.util.Date getDnt() {
        return Dnt;
    }

    public void setDnt(java.sql.Date dnt) {
        Dnt = dnt;
    }



    public String getClinicname() {
        return Clinicname;
    }

    public void setClinicname(String clinicname) {
        Clinicname = clinicname;
    }


}
