package com.example.capstone;

import android.app.Application;

public class GlobalVariables extends Application {
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

    public GlobalVariables(String fname, String lname, String mname, String sex, String email, String address, String postal, String municipality) {
        Fname = fname;
        Lname = lname;
        Mname = mname;
        Sex = sex;
        Email = email;
        Address = address;
        Postal = postal;
        Municipality = municipality;
    }
    public GlobalVariables() {

    }

    private String Fname, Lname, Mname, Sex, Email , Address, Postal, Municipality;


}
