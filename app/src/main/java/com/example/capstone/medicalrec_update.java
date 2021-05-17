package com.example.capstone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class medicalrec_update extends AppCompatActivity {

    EditText contactperson;
    EditText contactnum;
    EditText medicheight;
    EditText medicBP;
    EditText medicBT;
    EditText medicweight;
    EditText medicallergies;
    EditText medicillness;

    Button update;
    ImageView back;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicalrec_update);

        contactperson = findViewById(R.id.Econtact_person);
        contactnum = findViewById(R.id.Econtact_number);
        medicheight = findViewById(R.id.medicalrec_height);
        medicweight = findViewById(R.id.medicalrec_weight);
        medicBP = findViewById(R.id.medicalrec_bloodpressure);
        medicBT = findViewById(R.id.medicalrec_bloodtype);
        medicallergies = findViewById(R.id.medicalrec_allergies);
        medicillness = findViewById(R.id.medicalrec_illness);
        medicBT.setKeyListener(null);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        back = findViewById(R.id.backspace);
        userId = fAuth.getCurrentUser().getUid();

        update = findViewById(R.id.btn_updatemedical);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(medicalrec_update.this, MainActivity.class);
                startActivity(intent);
            }
        });

        DocumentReference documentReference = fStore.collection("Patients").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                contactperson.setText(documentSnapshot.getString("EContactPerson"));
                contactnum.setText(documentSnapshot.getString("EContactNumber"));
                medicheight.setText(documentSnapshot.getString("Height"));
                medicBP.setText(documentSnapshot.getString("BloodP"));
                medicBT.setText(documentSnapshot.getString("BloodType"));
                medicweight.setText(documentSnapshot.getString("Weight"));
                medicallergies.setText(documentSnapshot.getString("Allergies"));
                medicillness.setText(documentSnapshot.getString("Illness"));
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String conperson = contactperson.getText().toString();
                String connum = contactnum.getText().toString();
                String mheight = medicheight.getText().toString();
                String mBP = medicBP.getText().toString();
                String mBT = medicBT.getText().toString();
                String mWeight = medicweight.getText().toString();
                String mAllergies = medicallergies.getText().toString();
                String mIllness = medicillness.getText().toString();

                Map<String, Object> Patients = new HashMap<>();
                Patients.put("EContactPerson", conperson);
                Patients.put("EContactNumber", connum);
                Patients.put("Height", mheight);
                Patients.put("BloodP", mBP);
                Patients.put("BloodType", mBT);
                Patients.put("Weight", mWeight);
                Patients.put("Allergies", mAllergies);
                Patients.put("Illness", mIllness);


                documentReference.update(Patients);
                Toast.makeText(medicalrec_update.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);

            }
        });

    }
}