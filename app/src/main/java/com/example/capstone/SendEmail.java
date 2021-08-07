package com.example.capstone;

import android.app.Application;
import android.os.StrictMode;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

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
