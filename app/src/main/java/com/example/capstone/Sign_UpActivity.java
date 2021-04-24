package com.example.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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



            Map<String,Object> Verification = new HashMap<>();
            Verification.put("Email",ET_Email.getText().toString());
            Verification.put("VCode","TestTest");

            db.collection("Verification")
                    .add(Verification)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                            Intent intent = new Intent(Sign_UpActivity.this,Sign_Up_VerifyActivity.class);
                            intent.putExtra("FirstName",String.valueOf(ET_FName.getText()));
                            intent.putExtra("LastName",String.valueOf(ET_LName.getText()));
                            intent.putExtra("Middle",String.valueOf(ET_MI.getText()));
                            intent.putExtra("Sex",String.valueOf(ET_Sex.getText()));
                            intent.putExtra("Contact",String.valueOf(ET_Contact.getText()));
                            intent.putExtra("Nationality",String.valueOf(ET_Nationality.getText()));
                            intent.putExtra("Email",String.valueOf(ET_Email.getText()));
                            intent.putExtra("Pass",String.valueOf(ET_Pass.getText()));
                            intent.putExtra("Address",String.valueOf(ET_Address.getText()));
                            intent.putExtra("Postal",String.valueOf(ET_Postal.getText()));
                            intent.putExtra("Municipality",String.valueOf(ET_Municipality.getText()));
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Sign_UpActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                }
            });


        }
    });

}}


