package com.medicall.capstone.Model;

public class PatientHMOModel {
    String HMOName;
    String CardNumber;

    public String getHMOCNumber() {
        return HMOCNumber;
    }

    public void setHMOCNumber(String HMOCNumber) {
        this.HMOCNumber = HMOCNumber;
    }

    String HMOCNumber;

    public String getExpiryDate() {
        return ExpiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        ExpiryDate = expiryDate;
    }

    String ExpiryDate;

    public String getHMOAddress() {
        return HMOAddress;
    }

    public void setHMOAddress(String HMOAddress) {
        this.HMOAddress = HMOAddress;
    }

    String HMOAddress;

    public PatientHMOModel(){

    }

    public String getHMOName() {
        return HMOName;
    }

    public void setHMOName(String HMOName) {
        this.HMOName = HMOName;
    }

    public String getCardNumber() {
        return CardNumber;
    }

    public void setCardNumber(String cardNumber) {
        CardNumber = cardNumber;
    }

    public PatientHMOModel(String hmoName, String cardNumber, String expiryDate, String hmoAddress){
        this.CardNumber = cardNumber;
        this.HMOName = hmoName;
        this.ExpiryDate = expiryDate;
        this.HMOAddress = hmoAddress;
    }
}
