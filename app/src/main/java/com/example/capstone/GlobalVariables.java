package com.example.capstone;

import android.app.Application;

public class GlobalVariables extends Application {

    private String Contact;
    private String Nationality;
    private String Fname;
    private String Lname;
    private String Mname;
    private String Sex;
    private String Email;
    private String Password;
    private String Address;
    private String Postal;
    private String Municipality;
    private  String EContactP;
    private  String EContactN;
    private  String Height;
    private  String Weight;
    private  String BloodP;
    private  String BloodType;
    private  String Allergies;
    private  String Illness;
    private  String Channel_Name;




    public String getFname() {
        return Fname;
    }

    public void setFname(String fname) {
        Fname = fname;
    }

    public String getLname() {
        return Lname;
    }

    public void setLname(String lname) {
        Lname = lname;
    }

    public String getMname() {
        return Mname;
    }

    public void setMname(String mname) {
        Mname = mname;
    }

    public String getSex() {
        return Sex;
    }

    public void setSex(String sex) {
        Sex = sex;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPostal() {
        return Postal;
    }

    public void setPostal(String postal) {
        Postal = postal;
    }

    public String getMunicipality() {
        return Municipality;
    }

    public void setMunicipality(String municipality) {
        Municipality = municipality;
    }


    public GlobalVariables() {

    }




    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public String getNationality() {
        return Nationality;
    }

    public void setNationality(String nationality) {
        Nationality = nationality;
    }



    public String getEContactP() {
        return EContactP;
    }

    public void setEContactP(String EContactP) {
        this.EContactP = EContactP;
    }

    public String getEContactN() {
        return EContactN;
    }

    public void setEContactN(String EContactN) {
        this.EContactN = EContactN;
    }

    public String getHeight() {
        return Height;
    }

    public void setHeight(String height) {
        Height = height;
    }

    public String getWeight() {
        return Weight;
    }

    public void setWeight(String weight) {
        Weight = weight;
    }

    public String getBloodP() {
        return BloodP;
    }

    public void setBloodP(String bloodP) {
        BloodP = bloodP;
    }

    public String getBloodType() {
        return BloodType;
    }

    public void setBloodType(String bloodType) {
        BloodType = bloodType;
    }

    public String getAllergies() {
        return Allergies;
    }

    public void setAllergies(String allergies) {
        Allergies = allergies;
    }

    public String getIllness() {
        return Illness;
    }

    public void setIllness(String illness) {
        Illness = illness;
    }


    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getChannel_Name() {
        return Channel_Name;
    }

    public void setChannel_Name(String channel_Name) {
        Channel_Name = channel_Name;
    }



}
