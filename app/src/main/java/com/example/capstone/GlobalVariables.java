package com.example.capstone;

import android.app.Application;

import java.util.Date;

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
    //select Time
    private String Time;

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    private int counter;
    public String getSDWorkStart() {
        return SDWorkStart;
    }

    public void setSDWorkStart(String SDWorkStart) {
        this.SDWorkStart = SDWorkStart;
    }

    public String getSDWorkEnd() {
        return SDWorkEnd;
    }

    public void setSDWorkEnd(String SDWorkEnd) {
        this.SDWorkEnd = SDWorkEnd;
    }

    public String getSDLunchStart() {
        return SDLunchStart;
    }

    public void setSDLunchStart(String SDLunchStart) {
        this.SDLunchStart = SDLunchStart;
    }

    public String getSDLunchEnd() {
        return SDLunchEnd;
    }

    public void setSDLunchEnd(String SDLunchEnd) {
        this.SDLunchEnd = SDLunchEnd;
    }

    private  String SDWorkStart;
    private  String SDWorkEnd;
    private  String SDLunchStart;
    private  String SDLunchEnd;

    public String getBday() {
        return Bday;
    }

    public void setBday(String bday) {
        Bday = bday;
    }

    private String Bday;

    public String getMainuserID() {
        return MainuserID;
    }

    public void setMainuserID(String mainuserID) {
        MainuserID = mainuserID;
    }

    String MainuserID;

    public String getSDClinic() {
        return SDClinic;
    }

    public void setSDClinic(String SDClinic) {
        this.SDClinic = SDClinic;
    }

    public String getSDDocemail() {
        return SDDocemail;
    }

    public void setSDDocemail(String SDDocemail) {
        this.SDDocemail = SDDocemail;
    }

    public String getSDDate() {
        return SDDate;
    }

    public void setSDDate(String SDDate) {
        this.SDDate = SDDate;
    }

    String SDClinic;
    String SDDocemail;
    String SDDate;

    public Date getDDate() {
        return DDate;
    }

    public void setDDate(Date DDate) {
        this.DDate = DDate;
    }

    Date DDate;
    String SDtimestart;
    String SDPatUId;
    String SDtimestop;
    String SDDocUid;
    String SDnote;
    String SDid;
    String SDDocLastName;
    public String getSDDocLastName() {
        return SDDocLastName;
    }

    public void setSDDocLastName(String SDDocLastName) {
        this.SDDocLastName = SDDocLastName;
    }



    public String getSDid() {
        return SDid;
    }

    public void setSDid(String SDid) {
        this.SDid = SDid;
    }



    public String getSDtimestart() {
        return SDtimestart;
    }

    public void setSDtimestart(String SDtimestart) {
        this.SDtimestart = SDtimestart;
    }

    public String getSDPatUId() {
        return SDPatUId;
    }

    public void setSDPatUId(String SDPatUId) {
        this.SDPatUId = SDPatUId;
    }

    public String getSDtimestop() {
        return SDtimestop;
    }

    public void setSDtimestop(String SDtimestop) {
        this.SDtimestop = SDtimestop;
    }

    public String getSDDocUid() {
        return SDDocUid;
    }

    public void setSDDocUid(String SDDocUid) {
        this.SDDocUid = SDDocUid;
    }

    public String getSDnote() {
        return SDnote;
    }

    public void setSDnote(String SDnote) {
        this.SDnote = SDnote;
    }



    public String getMainUser() {
        return MainUser;
    }

    public void setMainUser(String mainUser) {
        MainUser = mainUser;
    }

    String MainUser;



    public String getFname() {
        return Fname;
    }

    public void setFname(String fname) { Fname = fname; }

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

    String startTime;
    String endTime;
    int post;
    String dateconsult;
    Date dateandtime;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getPost() {
        return post;
    }

    public void setPost(int position) {
        this.post= position;
    }

    public String getDateconsult() {
        return dateconsult;
    }

    public void setDateconsult(String dateconsult) {
        this.dateconsult = dateconsult;
    }

    public Date getDateandtime() {
        return dateandtime;
    }

    public void setDateandtime(Date dateandtime) {
        this.dateandtime = dateandtime;
    }

    public String getPending_patname() {
        return Pending_patname;
    }

    public void setPending_patname(String pending_patname) {
        Pending_patname = pending_patname;
    }

    public String getPending_sched() {
        return Pending_sched;
    }

    public void setPending_sched(String pending_sched) {
        Pending_sched = pending_sched;
    }

    public String getPending_hmo() {
        return Pending_hmo;
    }

    public void setPending_hmo(String pending_hmo) {
        Pending_hmo = pending_hmo;
    }

    public String getPending_docname() {
        return Pending_docname;
    }

    public void setPending_docname(String pending_docname) {
        Pending_docname = pending_docname;
    }

    String Pending_patname;
    String Pending_sched;
    String Pending_hmo;
    String Pending_docname;

    public String getPending_docUid() {
        return Pending_docUid;
    }

    public void setPending_docUid(String pending_docUid) {
        Pending_docUid = pending_docUid;
    }

    String Pending_docUid;

    public String getPending_patUid() {
        return Pending_patUid;
    }

    public void setPending_patUid(String pending_patUid) {
        Pending_patUid = pending_patUid;
    }

    String Pending_patUid;

    public String getPending_cardNumber() {
        return Pending_cardNumber;
    }

    public void setPending_cardNumber(String pending_cardNumber) {
        Pending_cardNumber = pending_cardNumber;
    }

    String Pending_cardNumber;

    public String getEditHMO_cardNumber() {
        return EditHMO_cardNumber;
    }

    public void setEditHMO_cardNumber(String editHMO_cardNumber) {
        EditHMO_cardNumber = editHMO_cardNumber;
    }

    String EditHMO_cardNumber;

    public String getEditHMO_hmoName() {
        return EditHMO_hmoName;
    }

    public void setEditHMO_hmoName(String editHMO_hmoName) {
        EditHMO_hmoName = editHMO_hmoName;
    }

    String EditHMO_hmoName;

}