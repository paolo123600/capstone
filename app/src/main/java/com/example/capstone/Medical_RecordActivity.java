package com.example.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
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

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class Medical_RecordActivity extends AppCompatActivity {
    EditText ET_ContactP,ET_ContactN,ET_Height,ET_Weight,ET_BloodP,ET_BloodType,ET_Allergies,ET_Illness;
    Button btn_Continue;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medical_record);
        GlobalVariables gv =(GlobalVariables) getApplicationContext ();

        db= FirebaseFirestore.getInstance();
        ET_ContactP=(EditText) findViewById(R.id.EcontanctP);
        ET_ContactN=(EditText) findViewById(R.id.EcontanctN);
        ET_Height=(EditText) findViewById(R.id.Height);
        ET_Weight=(EditText) findViewById(R.id.Weight);
        ET_BloodP=(EditText) findViewById(R.id.Bloodp);
        ET_BloodType=(EditText) findViewById(R.id.Bloodtype);
        ET_Allergies=(EditText) findViewById(R.id.Allergies);
        ET_Illness=(EditText) findViewById(R.id.illness);
        btn_Continue = (Button) findViewById(R.id.btn_continue);

    btn_Continue.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

        gv.setEContactP(ET_ContactP.getText().toString());
        gv.setEContactN(ET_ContactN.getText().toString());
        gv.setHeight(ET_Height.getText().toString());
        gv.setWeight(ET_Weight.getText().toString());
        gv.setBloodP(ET_BloodP.getText().toString());
        gv.setBloodType(ET_BloodType.getText().toString());
        gv.setAllergies(ET_Allergies.getText().toString());
        gv.setIllness(ET_Illness.getText().toString());

        String email = gv.getEmail();;

        SecureRandom random = new SecureRandom();
        String randomCode = new BigInteger(30, random).toString(32).toUpperCase();


        Map<String,Object> Verification = new HashMap<>();
        Verification.put("Email",email);
        Verification.put("VCode",randomCode);

        db.collection("Verification")
                .add(Verification)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        try{
                            SendEmail SE = new SendEmail();
                            SE.EmailSend(randomCode, email);

                            Intent intent = new Intent(Medical_RecordActivity.this,Sign_Up_VerifyActivity.class);

                            startActivity(intent);
                        }
                        catch (Exception e){
                            Toast.makeText(getApplicationContext(), "Verification Code Error!", Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Medical_RecordActivity.this,"Failed",Toast.LENGTH_SHORT).show();
            }
        });

        ;
    }
});
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
}
