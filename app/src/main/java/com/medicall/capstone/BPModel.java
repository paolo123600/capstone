package com.medicall.capstone;

import java.util.Date;

public class BPModel {

    private String item_id;

    private String LastName;
    private String Date;
    private String Upper;
    private String Lower;
    private Date Dnt;

    public BPModel() {}

    public BPModel(String Date, String Upper, String Lower, String LastName, Date Dnt, String item_id){
        this.LastName = LastName;
        this.Date = Date;
        this.Upper = Upper;
        this.Lower = Lower;
        this.Dnt = Dnt;
        this.item_id = item_id;

    }

    public String getDate() { return Date; }

    public void setDate(String date) { Date = date; }

    public java.util.Date getDnt() { return Dnt; }

    public void setDnt(java.util.Date dnt) { this.Dnt = dnt; }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getUpper() { return Upper; }

    public void setUpper(String upper) { Upper = upper; }

    public String getLower() { return Lower; }

    public void setLower(String lower) { Lower = lower; }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }
}
