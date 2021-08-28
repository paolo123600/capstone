package com.medicall.capstone;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.medicall.capstone.R;

import com.medicall.capstone.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class Pending_Confirmation extends AppCompatActivity {

    private TextView PatientName;
    private TextView DoctorName;
    private TextView Schedule;
    private TextView HMO_Name;
    private Button Accept;
    private Button Decline;
    private ImageView HMO_Image;
    private TextView CardNumber;
    GlobalVariables gv;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    private PreferenceManager preferenceManager;
    String storageid;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    Bitmap getpic;
    String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_confirmation);

        preferenceManager = new PreferenceManager(getApplicationContext());
        PatientName = (TextView) findViewById(R.id.conf_patientname);
        DoctorName = (TextView) findViewById(R.id.conf_doctorname);
        Schedule = (TextView) findViewById(R.id.conf_schedule);
        HMO_Name = (TextView) findViewById(R.id.conf_hmoname);
        HMO_Image = (ImageView) findViewById(R.id.conf_hmoimage);
        Accept = (Button) findViewById(R.id.conf_btnaccept);
        Decline = (Button) findViewById(R.id.conf_btndecline);
        CardNumber = (TextView) findViewById(R.id.conf_hmocardnum);
        gv = (GlobalVariables) getApplicationContext();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        retrieveHMO();

        db.collection("Doctors").document(gv.getPending_docUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doctor = task.getResult();
                    DoctorName.setText("Doctor: " + doctor.getString("FirstName") + " " + doctor.getString("LastName"));
                    db.collection("Patients").document(gv.getPending_patUid())
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot patient = task.getResult();
                                PatientName.setText("Patient: " + patient.getString("FirstName") + " " + patient.getString("LastName"));
                            }
                        }
                    });
                }
            }
        });

        Schedule.setText("Schedule: " + gv.getPending_sched());
        HMO_Name.setText("HMO: " + gv.getPending_hmo());
        CardNumber.setText("Card Number: " + gv.getPending_cardNumber());

        Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Pending_Confirmation.this);
                builder.setCancelable(true);
                builder.setTitle("Confirmation");
                builder.setMessage("Approve this appointment?");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.collection("Schedules").whereEqualTo("PatientUId", gv.getPending_patUid())
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()){
                                            QuerySnapshot querySnapshot = task.getResult();
                                            if(!querySnapshot.isEmpty()){
                                                for(QueryDocumentSnapshot scheds : task.getResult()){
                                                    db.collection("Schedules").document(scheds.getString("SchedId"))
                                                            .update("Status", "Paid");
                                                    AlertDialog.Builder status = new AlertDialog.Builder(Pending_Confirmation.this);
                                                    status.setTitle("Schedule approved");
                                                    status.setMessage("Appointment successfully approved!");
                                                    status.setCancelable(true);
                                                    status.setNeutralButton(android.R.string.ok,
                                                            new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    Intent intent = new Intent(Pending_Confirmation.this, Pending_Appointments.class);
                                                                    startActivity(intent);
                                                                }
                                                            });
                                                    AlertDialog success = status.create();
                                                    success.show();
                                                }

                                            }
                                        }
                                    }
                                });
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        Decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Pending_Confirmation.this);
                builder.setCancelable(true);
                builder.setTitle("Confirmation");
                builder.setMessage("Cancel this appointment?");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.collection("Schedules").whereEqualTo("PatientUId", gv.getPending_patUid())
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()){
                                            QuerySnapshot querySnapshot = task.getResult();
                                            if(!querySnapshot.isEmpty()){
                                                for(QueryDocumentSnapshot scheds : task.getResult()){
                                                    db.collection("Schedules").document(scheds.getString("SchedId"))
                                                            .update("Status", "Cancelled");
                                                    AlertDialog.Builder status1 = new AlertDialog.Builder(Pending_Confirmation.this);
                                                    status1.setTitle("Schedule cancelled");
                                                    status1.setMessage("Appointment successfully declined!");
                                                    status1.setCancelable(true);
                                                    status1.setNeutralButton(android.R.string.ok,
                                                            new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    Intent intent = new Intent(Pending_Confirmation.this, Pending_Appointments.class);
                                                                    startActivity(intent);
                                                                }
                                                            });
                                                    AlertDialog decline = status1.create();
                                                    decline.show();
                                                }

                                            }
                                        }
                                    }
                                });
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        HMO_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pending_Confirmation.this, ImageFullscreen.class);
                startActivity(intent);
            }
        });
    }

    private void retrieveHMO(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Retrieving image..");
        progressDialog.show();

        db.collection("Schedules").whereEqualTo("ClinicName", preferenceManager.getString("ClinicName")).whereEqualTo("PatientUId", gv.getPending_patUid())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot querySnapshot = task.getResult();
                    if(!querySnapshot.isEmpty()){
                        for(QueryDocumentSnapshot sched : task.getResult()){
                            storageid = sched.getString("StorageId");
                            storageReference = FirebaseStorage.getInstance().getReference("PatientHMO/" + storageid);

                            if(storageid==""){
                                if(progressDialog.isShowing()){
                                    final int sdk = Build.VERSION.SDK_INT;
                                    if(sdk < Build.VERSION_CODES.R){
                                        HMO_Image.setBackgroundResource(R.drawable.nopreview);
                                    }
                                    else{
                                        HMO_Image.setBackgroundResource(R.drawable.nopreview);
                                    }
                                    progressDialog.dismiss();
                                }
                            }
                            else{
                                try{
                                    File localfile = File.createTempFile("myHMO", ".jpg");
                                    storageReference.getFile(localfile)
                                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                    if(progressDialog.isShowing()){
                                                        getpic = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                                                        HMO_Image.setImageBitmap(getpic);
                                                        progressDialog.dismiss();
                                                    }
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