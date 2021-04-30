package com.example.capstone;

import android.app.Application;

import java.sql.Time;
import java.util.Date;

public class ScheduleVariables extends Application {
    public String getClinic() {
        return Clinic;
    }

    public void setClinic(String clinic) {
        Clinic = clinic;
    }

    public String getDocemail() {
        return Docemail;
    }

    public void setDocemail(String doctor) {
        Docemail = doctor;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    String Clinic;
    String Docemail;
     String Date;


}
