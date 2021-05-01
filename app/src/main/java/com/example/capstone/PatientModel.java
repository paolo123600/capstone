package com.example.capstone;

public class PatientModel {
    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getFirstName() {
        return FirstName;
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

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public PatientModel(String email, String firstName, String lastName, String userId, String contact) {
        Email = email;
        FirstName = firstName;
        LastName = lastName;
        UserId = userId;
        Contact = contact;
    }
    public PatientModel() {

    }

    String Email , FirstName, LastName, UserId, Contact;
}
