package com.medicall.capstone;

import android.app.Application;

public class SendEmail extends Application {

    public void EmailSend(String vcode, String receiver){
/////
        GlobalVariables gv =(GlobalVariables) getApplicationContext ();



        String Pass =  gv.getPassword();
        String Fname =gv.getFname();
        String Lname =gv.getLname();
        String Mname =gv.getMname();
        String Contact = gv.getContact();
        String Sex =gv.getSex();
        String Address =gv.getAddress();
        String Postal =gv.getPostal();
        String Municipality =gv.getMunicipality();
        String EEContactP =gv.getEContactP();
        String EContactN =gv.getEContactN();
        String Height =gv.getHeight();
        String Weight =gv.getWeight();
        String BloodP =gv.getBloodP();
        String BloodType =gv.getBloodType();
        String Allergies =gv.getAllergies();
        String Illness =gv.getIllness();
        String Bday = gv.getBday();

    }

}
