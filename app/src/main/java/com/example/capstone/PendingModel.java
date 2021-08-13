package com.example.capstone;

public class PendingModel {

    private String ClinicName;
    private String Date;
    private String DoctorUId;
    private String PatientUId;
    private String Hmo;
    private String StartTime;
    private String EndTime;

    public PendingModel(){

    }

    public String getClinicName() {
        return ClinicName;
    }

    public void setClinicName(String clinicName) {
        ClinicName = clinicName;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
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

    public String getHmo() {
        return Hmo;
    }

    public void setHmo(String hmo) {
        Hmo = hmo;
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

    public PendingModel(String clinicName, String date, String doctorUId, String patientUId, String hmo, String startTime, String endTime){
        ClinicName = clinicName;
        Date = date;
        DoctorUId = doctorUId;
        PatientUId = patientUId;
        Hmo = hmo;
        StartTime = startTime;
        EndTime = endTime;
    }
}
