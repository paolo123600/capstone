package com.medicall.capstone.Model;

public class PatientHMOModel {
    String HMOName;
    String CardNumber;

    public String getExpiryDate() {
        return ExpiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        ExpiryDate = expiryDate;
    }

    String ExpiryDate;

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

    public PatientHMOModel(String hmoName, String cardNumber, String expiryDate){
        this.CardNumber = cardNumber;
        this.HMOName = hmoName;
        this.ExpiryDate = expiryDate;
    }
}
