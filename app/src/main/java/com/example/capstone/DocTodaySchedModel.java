package com.example.capstone;

public class DocTodaySchedModel {
    public DocTodaySchedModel(String doctorUId, String patientUId, String startTime, String endTime, int position, String date, String status) {
        DoctorUId = doctorUId;
        PatientUId = patientUId;
        StartTime = startTime;
        EndTime = endTime;
        Position = position;
        Date = date;
        Status = status;
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

    String DoctorUId, PatientUId, StartTime, EndTime,  Date, Status;
    int Position;
}
