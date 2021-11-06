package com.medicall.capstone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
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
    Button note;
    Button prescription;


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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_sched_status);


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
        note = (Button) findViewById(R.id.view_note);
        prescription = (Button) findViewById(R.id.view_prescription);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();


        db.collection("Schedules").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){

                        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
                        Date times = new Date();



                        clinicname.setText(document.getString("ClinicName"));
                        payment.setText(document.getString("Price"));
                        date.setText(document.getDate("Date").toString());

                        String docUid = document.getString("DoctorUId");
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

                        String Patname = document.getString("PatientUId");
                        db.collection("Patients").whereEqualTo("UserId", Patname).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

                        String Pic = document.getString("PatientUId");
                        db.collection("Patients").whereEqualTo("UserId", Pic).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if(!querySnapshot.isEmpty()){
                                        for(QueryDocumentSnapshot profile : task.getResult()){
                                            image = profile.getString("StorageId");
                                            if(image.equals("None")){
                                                dpicture.setBackgroundResource(R.drawable.circlebackground);
                                            }
                                            else{
                                                storageReference = FirebaseStorage.getInstance().getReference("PatientPicture/" + image);
                                                try{
                                                    File local = File.createTempFile("myProfilePicture",".jpg");
                                                    storageReference.getFile(local)
                                                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                                @Override
                                                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                                    profilepic = BitmapFactory.decodeFile(local.getAbsolutePath());
                                                                    dpicture.setImageBitmap(profilepic);
                                                                }
                                                            });
                                                }
                                                catch (IOException e){
                                                    e.printStackTrace();
                                                }
                                            }

                                        }
                                    }
                                }
                            }
                        });






                    }
                }
            }
        });













    }



}