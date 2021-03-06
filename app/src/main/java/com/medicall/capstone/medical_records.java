package com.medicall.capstone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.medicall.capstone.R;

public class medical_records extends AppCompatActivity {

    TextView contactperson;
    TextView contactnum;
    TextView medicheight;
    TextView medicBP;
    TextView medicBT;
    TextView medicweight;
    TextView medicallergies;
    TextView medicillness;

    ImageView back;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;

    Button editbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_records);

        contactperson = findViewById(R.id.Econtact_person);
        contactnum = findViewById(R.id.Econtact_number);
        medicheight = findViewById(R.id.medicalrec_height);
        medicweight = findViewById(R.id.medicalrec_weight);
        medicBT = findViewById(R.id.medicalrec_bloodtype);
        medicallergies = findViewById(R.id.medicalrec_allergies);
        medicillness = findViewById(R.id.medicalrec_illness);

        back = findViewById(R.id.backspace);

        editbtn = findViewById(R.id.medicalrec_edit_btn);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();

        contactperson.setKeyListener(null);
        contactnum.setKeyListener(null);
        medicheight.setKeyListener(null);
        medicweight.setKeyListener(null);
        medicBT.setKeyListener(null);
        medicallergies.setKeyListener(null);
        medicillness.setKeyListener(null);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(medical_records.this, MainActivity.class);
                startActivity(intent);
            }
        });

        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(medical_records.this, medicalrec_update.class);
                startActivity(intent);
            }
        });

        DocumentReference documentReference = fStore.collection("Patients").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                contactperson.setText(documentSnapshot.getString("EContactPerson"));
                contactnum.setText(documentSnapshot.getString("EContactNumber"));
                medicheight.setText(documentSnapshot.getString("Height") + "cm");
                medicBT.setText(documentSnapshot.getString("BloodType"));
                medicweight.setText(documentSnapshot.getString("Weight") + "kg");
               medicallergies.setText(documentSnapshot.getString("Allergies"));
                medicillness.setText(documentSnapshot.getString("Illness"));
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}