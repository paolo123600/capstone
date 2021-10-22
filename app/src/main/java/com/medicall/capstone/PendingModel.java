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
    private String ExpiryDate;

    public String getHMOAddress() {
        return HMOAddress;
    }

    public void setHMOAddress(String HMOAddress) {
        this.HMOAddress = HMOAddress;
    }

    private String HMOAddress;

    public String getHMOCNumber() {
        return HMOCNumber;
    }

    public void setHMOCNumber(String HMOCNumber) {
        this.HMOCNumber = HMOCNumber;
    }

    private String HMOCNumber;

    public String getExpiryDate() {
        return ExpiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        ExpiryDate = expiryDate;
    }

    public Integer getPosition() {
        return Position;
    }

    public void setPosition(Integer position) {
        Position = position;
    }

    private Integer Position;

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

    public PendingModel(String clinicName, Date date, String doctorUId, String patientUId, String hmoName, String startTime, String endTime, String cardNumber, String expiryDate, String hmoCnumber, String hmoAddress){
        ClinicName = clinicName;
        Date = date;
        DoctorUId = doctorUId;
        PatientUId = patientUId;
        HMOName = hmoName;
        StartTime = startTime;
        EndTime = endTime;
        CardNumber = cardNumber;
        ExpiryDate = expiryDate;
        HMOCNumber = hmoCnumber;
        HMOAddress = hmoAddress;
    }
}
