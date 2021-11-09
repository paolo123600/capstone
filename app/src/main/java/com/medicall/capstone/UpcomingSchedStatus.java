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
    TextView timer;
    TextView payment;
    Button note;
    Button prescriptionbtn;

    //hmo
    ImageView hmo_sched;
    TextView hmo_num;
    TextView hmo_add;
    TextView hmo_expiry;
    TextView hmo_name;

    TextView view1;
    View view2;
    TextView view3;
    View view4;
    TextView view5;
    View view6;
    TextView view7;
    View view8;



    String userId;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseAuth mAuth;

    ImageView changeDP;
    Bitmap getPic;
    StorageReference ref;
    String image;
    ImageView dpicture;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    FirebaseFirestore db;
    String documentid;
    private View notebtn;


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
        timer = findViewById(R.id.time_status);
        payment = findViewById(R.id.payment_status);
        notebtn = (Button) findViewById(R.id.view_note);
        prescriptionbtn = (Button) findViewById(R.id.view_prescription);
        note = (Button) findViewById(R.id.view_note);
        prescriptionbtn = (Button) findViewById(R.id.view_prescription);
        hmo_name = findViewById(R.id.hmo_name);
        hmo_expiry = findViewById(R.id.hmo_expiry);
        hmo_num = findViewById(R.id.hmo_num);
        hmo_add = findViewById(R.id.HMOaddress);
        hmo_sched = findViewById(R.id.hmo_sched);

        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);
        view4 = findViewById(R.id.view4);
        view5 = findViewById(R.id.view5);
        view6 = findViewById(R.id.view6);
        view7 = findViewById(R.id.view7);
        view8 = findViewById(R.id.view8);



        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();

        db.collection("Schedules").document(documentid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh: mm aa");
                clinicname.setText(documentSnapshot.getString("ClinicName"));
                payment.setText(documentSnapshot.getString("Price"));
                date.setText(documentSnapshot.getDate("Date").toString());

                String docUid = documentSnapshot.getString("DoctorUId");
                String PatUid = documentSnapshot.getString("PatientUId");


                if(payment.getText().equals("HMO")){
                    Toast.makeText(UpcomingSchedStatus.this, PatUid, Toast.LENGTH_SHORT).show();
                    String Hmoname = documentSnapshot.getString("HMOName");

                    DocumentReference documentReference = db.collection("HMO").document(Hmoname).collection("Patients").document(PatUid);
                    documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {


                            hmo_name.setText(Hmoname);
                           hmo_add.setText(documentSnapshot.getString("HMOAddress"));
                            hmo_expiry.setText(documentSnapshot.getString("ExpiryDate"));
                            hmo_num.setText(documentSnapshot.getString("HMOCNumber"));
                            hmo_sched.setVisibility(View.VISIBLE);

                            hmo_name.setVisibility(View.VISIBLE);
                            hmo_add.setVisibility(View.VISIBLE);
                            hmo_expiry.setVisibility(View.VISIBLE);
                            hmo_num.setVisibility(View.VISIBLE);
                            view1.setVisibility(View.VISIBLE);
                            view2.setVisibility(View.VISIBLE);
                            view3.setVisibility(View.VISIBLE);
                            view4.setVisibility(View.VISIBLE);
                            view5.setVisibility(View.VISIBLE);
                            view6.setVisibility(View.VISIBLE);
                            view7.setVisibility(View.VISIBLE);

                        }
                    });
                    String storageid = documentSnapshot.getString("StorageId");
                    storageReference = FirebaseStorage.getInstance().getReference("PatientHMO/" + storageid);
                    try{
                        File localfile = File.createTempFile("myHMO", ".jpg");
                        storageReference.getFile(localfile)
                                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        getPic = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                                        hmo_sched.setImageBitmap(getPic);
                                    }
                                });
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }

                }






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












    }



}