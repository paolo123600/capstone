package com.example.capstone;

import java.util.Date;

public class PendingModel {

    private String ClinicName;
    private java.util.Date Date;
    private String DoctorUId;
    private String PatientUId;
    private String Hmo;
    private String StartTime;
    private String EndTime;
    private String CardNumber;

    public PendingModel(){

    }

    public String getClinicName() {
        return ClinicName;
    }

    public void setClinicName(String clinicName) {
        ClinicName = clinicName;
    }

    public Date getDate() {
        return Date;
    }

    public void setDate(Date date) {
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

    public String getCardNumber() {
        return CardNumber;
    }

    public void setCardNumber(String cardNumber) {
        CardNumber = cardNumber;
    }

    public PendingModel(String clinicName, Date date, String doctorUId, String patientUId, String hmo, String startTime, String endTime, String cardNumber){
        ClinicName = clinicName;
        Date = date;
        DoctorUId = doctorUId;
        PatientUId = patientUId;
        Hmo = hmo;
        StartTime = startTime;
        EndTime = endTime;
        CardNumber = cardNumber;
    }
}
