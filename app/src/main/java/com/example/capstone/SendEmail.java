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

        final String username="medicall.capstone@gmail.com";
        final String password="!capstone04";
        String messageToSend = "Thank you for registering in our medical application!" +
                "" +
                "This is the Verification Code:" + vcode +
                "" +
                "Please enter this code to verify your account.";


        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.host","smtp.gmail.com");
        props.put("mail.smtp.port","587");
        Session ses = Session.getInstance(props,
                new javax.mail.Authenticator(){
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username,password);
                    }
                });
        try{
            MimeMessage mess = new MimeMessage(ses);
            mess.setFrom(new InternetAddress(username));
            mess.setRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
            mess.setSubject("Verification");
            mess.setText(messageToSend);
            Transport.send(mess);
            Toast.makeText(getApplicationContext(), "Message Send!", Toast.LENGTH_LONG).show();
        }
        catch (MessagingException e){
            throw new RuntimeException(e);
        }
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

}
