package com.example.capstone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class View_doctor_info extends AppCompatActivity {

    private TextView docname;
    private TextView clinicname;
    private TextView PRC;
    private TextView PTR;
    private TextView docgender;
    private TextView docbday;
    private TextView docschoolgrad;
    private TextView docyeargrad;
    private TextView docspecialty;
    GlobalVariables gv;
    String docid;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_doctor_info);

        docname = findViewById(R.id.View_docname);
        clinicname = findViewById(R.id.View_docclinic_name);
        PRC = findViewById(R.id.View_PRC);
        PTR = findViewById(R.id.View_PTR);
        docgender = findViewById(R.id.View_docgender);
        docbday = findViewById(R.id.View_docbday);
        docschoolgrad = findViewById(R.id.View_docschool);
        docyeargrad = findViewById(R.id.View_docyeargrad);
        docspecialty = findViewById(R.id.View_docspecialty);

        gv = (GlobalVariables) getApplicationContext();
        db = FirebaseFirestore.getInstance();
        docid = gv.getSDDocUid();


        DocumentReference documentReference = db.collection("Doctors").document(docid);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                docname.setText("Doctor Name: " + documentSnapshot.getString("FirstName") + " " + documentSnapshot.getString("LastName"));
                clinicname.setText("Clinic Name: " + documentSnapshot.getString("ClinicName"));
                PRC.setText("PRC: " + documentSnapshot.getString("PRC"));
                PTR.setText("PTR: " + documentSnapshot.getString("PTR"));
                docgender.setText("Gender: " + documentSnapshot.getString("Gender"));
                docbday.setText("Birthday: " + documentSnapshot.getString("Birthday"));
                docschoolgrad.setText("Graduate School: " + documentSnapshot.getString("SchoolGrad"));
                docyeargrad.setText("Year Graduated: " + documentSnapshot.getString("YearGrad"));
                docspecialty.setText("Specialty: " + documentSnapshot.getString("DocType"));

            }
        });
    }
}