package com.example.capstone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class profile_edit extends AppCompatActivity {

    TextView fullnameProfile;
    TextView genderProfile;
    TextView bdayProfile;
    EditText municiplatyProfile;
    EditText numberProfile;
    TextView emailProfile;
    EditText addressProfile;
    EditText postalPorfile;

    Button update;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;

    ImageView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        fullnameProfile = findViewById(R.id.first_name_profile);
        genderProfile = findViewById(R.id.gender_profile);
        bdayProfile = findViewById(R.id.birthday_profile);
        municiplatyProfile = findViewById(R.id.municipality_profile);
        numberProfile = findViewById(R.id.number_profile);
        emailProfile = findViewById(R.id.email_profile);
        addressProfile = findViewById(R.id.address_profile);
        postalPorfile = findViewById(R.id.postal_profile);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();

        update = findViewById(R.id.update_profile);

        back = findViewById(R.id.backspace);

        fullnameProfile.setKeyListener(null);
        genderProfile.setKeyListener(null);
        bdayProfile.setKeyListener(null);
        emailProfile.setKeyListener(null);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        DocumentReference documentReference = fStore.collection("Patients").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                fullnameProfile.setText(documentSnapshot.getString("LastName") + ", " + documentSnapshot.getString("FirstName") + " " + documentSnapshot.getString("MiddleInitial"));
                genderProfile.setText(documentSnapshot.getString("Sex"));
                bdayProfile.setText(documentSnapshot.getString("Birthday"));
                municiplatyProfile.setText(documentSnapshot.getString("Municipality"));
                numberProfile.setText(documentSnapshot.getString("Contact"));
                emailProfile.setText(documentSnapshot.getString("Email"));
                addressProfile.setText(documentSnapshot.getString("Address"));
                postalPorfile.setText(documentSnapshot.getString("Postal"));
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String municipality = municiplatyProfile.getText().toString();
                String number = numberProfile.getText().toString();
                String email = emailProfile.getText().toString();
                String address = addressProfile.getText().toString();
                String postal = postalPorfile.getText().toString();

                Map<String, Object> Patients = new HashMap<>();
                Patients.put("Municipality", municipality);
                Patients.put("Contact", number);
                Patients.put("Email", email);
                Patients.put("Address", address);
                Patients.put("Postal", postal);


                documentReference.update(Patients);
                Toast.makeText(profile_edit.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);

            }
        });

    }
}