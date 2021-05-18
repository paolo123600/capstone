package com.example.capstone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class ProfileFragment extends AppCompatActivity {

    TextView firstname;
    TextView gender;
    TextView address;
    TextView birthday;
    TextView municipality;
    TextView number;
    TextView email;
    TextView postal;
    ImageView back;

    Button editbutton;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_fragment);

        firstname = findViewById(R.id.first_name_profile);
        gender = findViewById(R.id.gender_profile);
        address = findViewById(R.id.address_profile);
        birthday = findViewById(R.id.birthday_profile);
        municipality = findViewById(R.id.municipality_profile);
        number = findViewById(R.id.number_profile);
        email = findViewById(R.id.email_profile);
        postal = findViewById(R.id.postal_profile);
        back = findViewById(R.id.backspace);

        editbutton = findViewById(R.id.editbtn);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();

        firstname.setKeyListener(null);
        gender.setKeyListener(null);
        address.setKeyListener(null);
        birthday.setKeyListener(null);
        municipality.setKeyListener(null);
        number.setKeyListener(null);
        email.setKeyListener(null);
        postal.setKeyListener(null);


        DocumentReference documentReference = fStore.collection("Patients").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                firstname.setText(documentSnapshot.getString("LastName") + ", " + documentSnapshot.getString("FirstName") + " " + documentSnapshot.getString("MiddleInitial"));
                gender.setText(documentSnapshot.getString("Sex"));
                address.setText(documentSnapshot.getString("Address"));
                municipality.setText(documentSnapshot.getString("Municipality"));
                number.setText(documentSnapshot.getString("Contact"));
                email.setText(documentSnapshot.getString("Email"));
                postal.setText(documentSnapshot.getString("Postal"));
                birthday.setText(documentSnapshot.getString("Birthday"));
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        editbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), profile_edit.class);
                startActivity(intent);
            }
        });
    }
}