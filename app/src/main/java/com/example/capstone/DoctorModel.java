package com.example.capstone;

public class DoctorModel {
    public String getFirstName() {
        return FirstName;
    }

    public DoctorModel() {
    }

    public DoctorModel(String firstName, String lastName, String email, String clinic) {
        FirstName = firstName;
        LastName = lastName;
        Email = email;
        ClinicName = clinic;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getClinic() {
        return ClinicName;
    }

    public void setClinic(String clinicName) {
        ClinicName = clinicName;
    }

    private String FirstName , LastName , Email , ClinicName ;


}
