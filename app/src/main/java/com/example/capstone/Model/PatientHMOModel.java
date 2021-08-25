package com.example.capstone.Model;

public class PatientHMOModel {
    String HMOName;
    String CardNumber;

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

    public PatientHMOModel(String hmoName, String cardNumber){
        this.CardNumber = cardNumber;
        this.HMOName = hmoName;
    }
}
