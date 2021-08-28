package com.medicall.capstone.Model;

public class DocHMO {

    String FirstName;
    String LastName;
    String UserId;

    public DocHMO(){

    }

    public DocHMO(String fName, String lName, String UId){
        FirstName = fName;
        LastName = lName;
        UserId = UId;
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
}
