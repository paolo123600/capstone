package com.medicall.capstone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UpcomingSchedStatus extends AppCompatActivity {

    TextView firstname;
    TextView email;
    TextView clinicname;
    TextView doctorname;
    TextView date;
    TextView time;
    TextView payment;
    Button notebtn;
    Button prescriptionbtn;


    String userId;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseAuth mAuth;

    ImageView changeDP;
    Bitmap profilepic;
    StorageReference ref;
    String image;
    ImageView dpicture;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    FirebaseFirestore db;
    String documentid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_sched_status);


        documentid = getIntent().getExtras().getString("documentid");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        db = FirebaseFirestore.getInstance();
        dpicture = findViewById(R.id.patient_dp);


        mAuth = FirebaseAuth.getInstance();
        firstname = findViewById(R.id.first_name_profile);
        email = findViewById(R.id.email_profile);
        clinicname = findViewById(R.id.clinic_name);
        doctorname = findViewById(R.id.doctor_name);
        date = findViewById(R.id.date_status);
        time = findViewById(R.id.time_status);
        payment = findViewById(R.id.payment_status);
        notebtn = (Button) findViewById(R.id.view_note);
        prescriptionbtn = (Button) findViewById(R.id.view_prescription);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();

        db.collection("Schedules").document(documentid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                clinicname.setText(documentSnapshot.getString("ClinicName"));
                payment.setText(documentSnapshot.getString("Price"));
                date.setText(documentSnapshot.getDate("Date").toString());

                String docUid = documentSnapshot.getString("DoctorUId");
                String PatUid = documentSnapshot.getString("PatientUId");
                db.collection("Doctors").whereEqualTo("UserId", docUid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()){
                                for (QueryDocumentSnapshot doctor: task.getResult()){
                                    doctorname.setText(doctor.getString("LastName" ) + " " + doctor.getString("FirstName"));
                                }
                            }
                        }
                    }
                });
                db.collection("Patients").whereEqualTo("UserId", PatUid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()){
                                for (QueryDocumentSnapshot pat: task.getResult()){
                                    firstname.setText(pat.getString("LastName" ) + " " + pat.getString("FirstName"));

                                }
                            }
                        }

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpcomingSchedStatus.this, "Error Getting Schedule Information", Toast.LENGTH_SHORT).show();
            }
        });









notebtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        db.collection("Schedules").document(documentid).collection("Note").document("Doctor_Note").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (!documentSnapshot.exists()){
                    Toast.makeText(UpcomingSchedStatus.this, "No Note", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(UpcomingSchedStatus.this, CheckNote.class);
                    intent.putExtra("documentid",documentid);
                    startActivity(intent);
                }
            }
        });

    }

});
prescriptionbtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        db.collection("Schedules").document(documentid).collection("Prescription").document("Doctor_Prescription").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (!documentSnapshot.exists()){
                    Toast.makeText(UpcomingSchedStatus.this, "No Prescription", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(UpcomingSchedStatus.this, CheckNote.class);
                    intent.putExtra("schedid",documentid);
                    startActivity(intent);
                }
            }
        });
    }
});





    }



}