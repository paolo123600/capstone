package com.example.capstone.Model;

public class PatRC {
    public PatRC() {
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public PatRC(String firstName, String lastName, String userId) {
        FirstName = firstName;
        LastName = lastName;
        UserId = userId;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    String FirstName, LastName, UserId;

}
