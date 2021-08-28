package com.medicall.capstone;

public class DocSchedModel {


    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getMaximumBooking() {
        return MaximumBooking;
    }

    public void setMaximumBooking(String maximumBooking) {
        MaximumBooking = maximumBooking;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public Boolean getMonday() {
        return Monday;
    }

    public void setMonday(Boolean monday) {
        Monday = monday;
    }

    public Boolean getTuesday() {
        return Tuesday;
    }

    public void setTuesday(Boolean tuesday) {
        Tuesday = tuesday;
    }

    public Boolean getWednesday() {
        return Wednesday;
    }

    public void setWednesday(Boolean wednesday) {
        Wednesday = wednesday;
    }

    public Boolean getThursday() {
        return Thursday;
    }

    public void setThursday(Boolean thursday) {
        Thursday = thursday;
    }

    public Boolean getFriday() {
        return Friday;
    }

    public void setFriday(Boolean friday) {
        Friday = friday;
    }

    public Boolean getSaturday() {
        return Saturday;
    }

    public void setSaturday(Boolean saturday) {
        Saturday = saturday;
    }

    public Boolean getSunday() {
        return Sunday;
    }

    public void setSunday(Boolean sunday) {
        Sunday = sunday;
    }

    public DocSchedModel( String startTime, String endTime, String maximumBooking, String price, Boolean monday, Boolean tuesday, Boolean wednesday, Boolean thursday, Boolean friday, Boolean saturday, Boolean sunday , Boolean inactive) {
        StartTime = startTime;
        EndTime = endTime;
        MaximumBooking = maximumBooking;
        Price = price;
        Monday = monday;
        Tuesday = tuesday;
        Wednesday = wednesday;
        Thursday = thursday;
        Friday = friday;
        Saturday = saturday;
        Sunday = sunday;
        InActive = inactive;
    }

    String StartTime;
    String EndTime;
    String MaximumBooking;
    String Price;
    Boolean Monday;
    Boolean Tuesday;
    Boolean Wednesday;
    Boolean Thursday;
    Boolean Friday;
    Boolean Saturday;
    Boolean Sunday;
    Boolean InActive;

    public Boolean getInActive() {
        return InActive;
    }

    public void setInActive(Boolean inActive) {
        InActive = inActive;
    }



    public DocSchedModel() {
    }
}
