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
import com.medicall.capstone.R;

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
    ImageView back;

    String name;
    String bold;
    String boldname;

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

        back = findViewById(R.id.backspace);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), selectDoc.class);
                startActivity(intent);
            }
        });



        DocumentReference documentReference = db.collection("Doctors").document(docid);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                docname.setText(documentSnapshot.getString("FirstName") + " " + documentSnapshot.getString("LastName"));
                clinicname.setText(documentSnapshot.getString("ClinicName"));
                PRC.setText(documentSnapshot.getString("PRC"));
                PTR.setText(documentSnapshot.getString("PTR"));
                docgender.setText(documentSnapshot.getString("Gender"));
                docbday.setText(documentSnapshot.getString("Birthday"));
                docschoolgrad.setText(documentSnapshot.getString("SchoolGrad"));
                docyeargrad.setText(documentSnapshot.getString("YearGrad"));
                docspecialty.setText(documentSnapshot.getString("DocType"));

            }
        });
    }
}