package com.example.capstone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class patientrec_doc extends AppCompatActivity {

    EditText name_patrec;
    EditText bday_patrec;
    EditText gender_patrec;
    EditText number_patrec;
    EditText height_patrec;
    EditText bt_patrec;
    EditText weight_patrec;
    EditText emcontact_patrec;
    EditText preillness_patrec;
    EditText allergies_patrec;
    EditText bloodp_patrec;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patientrec_doc);

        name_patrec = findViewById(R.id.full_name_patientrec);
        bday_patrec = findViewById(R.id.birthday_patientrec);
        gender_patrec = findViewById(R.id.gender_patientrec);
        number_patrec = findViewById(R.id.number_patientrec);
        height_patrec = findViewById(R.id.height_patientrec);
        bt_patrec = findViewById(R.id.bloodtype_patientrec);
        weight_patrec = findViewById(R.id.weight_patientrec);
        emcontact_patrec = findViewById(R.id.emergency_contact_patientrec);
        preillness_patrec = findViewById(R.id.prexisting_patientrec);
        allergies_patrec = findViewById(R.id.allergies_patientrec);
        bloodp_patrec = findViewById(R.id.bloodppatientrec);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();
        back = findViewById(R.id.backspace);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Intent intent = getIntent();
        String patid = intent.getStringExtra("patid");

        DocumentReference documentReference = fStore.collection("Patients").document(patid);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                name_patrec.setText(documentSnapshot.getString("LastName") + ", " + documentSnapshot.getString("FirstName") + " " + documentSnapshot.getString("MiddleInitial"));
                gender_patrec.setText(documentSnapshot.getString("Sex"));
                bday_patrec.setText(documentSnapshot.getString("Birthday"));
                number_patrec.setText(documentSnapshot.getString("Contact"));
                height_patrec.setText(documentSnapshot.getString("Height"));
                bt_patrec.setText(documentSnapshot.getString("BloodType"));
                bloodp_patrec.setText(documentSnapshot.getString("BloodP"));
                weight_patrec.setText(documentSnapshot.getString("Weight"));
                emcontact_patrec.setText(documentSnapshot.getString("EContactNumber"));
                preillness_patrec.setText(documentSnapshot.getString("Illness"));
                allergies_patrec.setText(documentSnapshot.getString("Allergies"));
            }
        });


    }
}