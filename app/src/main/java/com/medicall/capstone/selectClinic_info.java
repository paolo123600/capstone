package com.medicall.capstone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class selectClinic_info extends AppCompatActivity {

    TextView clname, clcontactnum, clemail, claddress, clmunicipality;
    FirebaseFirestore db;
    GlobalVariables gv;
    String clinicnamegv;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_clinic_info);

        clname = findViewById(R.id.cl_name_info);
        clcontactnum = findViewById(R.id.cl_contact_info);
        clemail = findViewById(R.id.cl_email_info);
        claddress = findViewById(R.id.cl_address_info);
        clmunicipality = findViewById(R.id.cl_municipality_info);

        gv = (GlobalVariables) getApplicationContext();
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        clinicnamegv = intent.getStringExtra("ClinicUid");

        back = findViewById(R.id.backspace);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), selectClinic.class);
                startActivity(intent);
            }
        });

        DocumentReference documentReference = db.collection("Clinics").document(clinicnamegv);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                clname.setText(documentSnapshot.getString("ClinicName"));
                clcontactnum.setText(documentSnapshot.getString("ContactNumber"));
                clemail.setText(documentSnapshot.getString("Email"));
                claddress.setText(documentSnapshot.getString("Address"));
                clmunicipality.setText(documentSnapshot.getString("Municipality"));
            }
        });
    }
}