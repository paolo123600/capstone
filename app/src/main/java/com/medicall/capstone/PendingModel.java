package com.medicall.capstone;

import java.util.Date;

public class PendingModel {

    private String ClinicName;
    private java.util.Date Date;
    private String DoctorUId;
    private String PatientUId;
    private String HMOName;
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

    public String getHMOName() {
        return HMOName;
    }

    public void setHMOName(String hmoName) {
        HMOName = hmoName;
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

    public PendingModel(String clinicName, Date date, String doctorUId, String patientUId, String hmoName, String startTime, String endTime, String cardNumber){
        ClinicName = clinicName;
        Date = date;
        DoctorUId = doctorUId;
        PatientUId = patientUId;
        HMOName = hmoName;
        StartTime = startTime;
        EndTime = endTime;
        CardNumber = cardNumber;
    }
}
