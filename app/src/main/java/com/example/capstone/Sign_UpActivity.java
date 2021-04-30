package com.example.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.capstone.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class Sign_UpActivity extends AppCompatActivity {
Button btnContinue;
EditText ET_FName, ET_LName, ET_MI, ET_Sex, ET_Email, ET_Pass, ET_ConPass , ET_Address, ET_Municipality,ET_Postal, ET_Contact, ET_Nationality;
FirebaseFirestore db;
    @Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.sign_up);
    GlobalVariables gv =(GlobalVariables) getApplicationContext ();


        btnContinue = (Button) findViewById(R.id.signup);
    ET_FName=(EditText) findViewById(R.id.Fname);
    ET_LName=(EditText)findViewById(R.id.Lname);
    ET_MI= (EditText)findViewById(R.id.Mi);
    ET_Sex= (EditText) findViewById(R.id.gender);
    ET_Contact=(EditText) findViewById(R.id.contact2);
    ET_Nationality=(EditText) findViewById(R.id.nationality);
    ET_Email=(EditText) findViewById(R.id.email);
    ET_Pass= (EditText) findViewById(R.id.password);
    ET_ConPass=(EditText) findViewById(R.id.confirm_pass);
    ET_Address=(EditText) findViewById(R.id.address);
    ET_Postal=(EditText) findViewById(R.id.postalcode);
    ET_Municipality=(EditText) findViewById(R.id.municipality);
    db= FirebaseFirestore.getInstance();



    btnContinue.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {


            gv.setFname(String.valueOf(ET_FName.getText()));
            gv.setLname(String.valueOf(ET_LName.getText()));
            gv.setMname(String.valueOf(ET_MI.getText()));
            gv.setContact(String.valueOf(ET_Contact.getText()));
            gv.setSex(String.valueOf(ET_Sex.getText()));
            gv.setNationality(String.valueOf(ET_Nationality.getText()));
            gv.setEmail(String.valueOf(ET_Email.getText()));
            gv.setPassword(String.valueOf(ET_Pass.getText()));
            gv.setAddress(String.valueOf(ET_Address.getText()));
            gv.setMunicipality(String.valueOf(ET_Municipality.getText()));
            gv.setPostal(String.valueOf(ET_Postal.getText()));




            Intent intent = new Intent(Sign_UpActivity.this,Medical_RecordActivity.class);

            startActivity(intent);



        }
    });

}}


