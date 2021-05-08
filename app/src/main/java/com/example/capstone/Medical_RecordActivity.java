package com.example.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

//
public class Medical_RecordActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    EditText ET_ContactP,ET_ContactN,ET_Height,ET_Weight,ET_BloodP,ET_Allergies,ET_Illness;
    Button btn_Continue;
    Spinner ET_BloodType;
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
        ET_BloodType=(Spinner) findViewById(R.id.Bloodtype);
        ET_Allergies=(EditText) findViewById(R.id.Allergies);
        ET_Illness=(EditText) findViewById(R.id.illness);
        btn_Continue = (Button) findViewById(R.id.btn_continue);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.blood, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ET_BloodType.setAdapter(adapter);

        ET_BloodType.setOnItemSelectedListener(this);

        btn_Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gv.setEContactP(ET_ContactP.getText().toString());
                gv.setEContactN(ET_ContactN.getText().toString());
                gv.setHeight(ET_Height.getText().toString());
                gv.setWeight(ET_Weight.getText().toString());
                gv.setBloodP(ET_BloodP.getText().toString());
                gv.setBloodType(ET_BloodType.getSelectedItem().toString());
                gv.setAllergies(ET_Allergies.getText().toString());
                gv.setIllness(ET_Illness.getText().toString());

                String email = gv.getEmail();;

                SecureRandom random = new SecureRandom();
                String randomCode = new BigInteger(30, random).toString(32).toUpperCase();

                SendEmail SE = new SendEmail();
                SE.EmailSend(randomCode, email);

                Map<String,Object> Verification = new HashMap<>();
                Verification.put("Email",email);
                Verification.put("VCode",randomCode);

                db.collection("Verification")
                        .add(Verification)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {

//                                try{
//
//
//                                }
//                                catch (Exception e){
//                                    Toast.makeText(getApplicationContext(), "Verification Code Error!", Toast.LENGTH_LONG).show();
//                                }


                                Intent intent = new Intent(Medical_RecordActivity.this,Sign_Up_VerifyActivity.class);

                                startActivity(intent);
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}