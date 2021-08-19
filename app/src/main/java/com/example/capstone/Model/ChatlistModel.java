package com.example.capstone.Model;

import java.util.Date;

public class ChatlistModel {
    public ChatlistModel(String userId, String usertype, Date dateAndTime) {
        UserId = userId;
        Usertype = usertype;
        DateAndTime = dateAndTime;
    }

    public ChatlistModel() {
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getUsertype() {
        return Usertype;
    }

    public void setUsertype(String usertype) {
        Usertype = usertype;
    }

    String UserId;
    String Usertype;


    public Date getDateAndTime() {
        return DateAndTime;
    }

    public void setDateAndTime(Date dateAndTime) {
        DateAndTime = dateAndTime;
    }

    Date DateAndTime;


}
