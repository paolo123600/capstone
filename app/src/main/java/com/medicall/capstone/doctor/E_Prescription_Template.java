package com.medicall.capstone.doctor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.medicall.capstone.GlobalVariables;
import com.medicall.capstone.R;
import com.medicall.capstone.doctor_homepage;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class E_Prescription_Template extends AppCompatActivity {

    TextView doctorname, doctornamebelow, doctorspecialization, clinicname, clinicaddress, contactnumber, email, date;
    TextView patientname, patient_age, patient_sex, patients_address;

    FirebaseAuth fAuth;
    FirebaseFirestore db;

    ImageView docsignature;

    private StorageReference storageReference;
    private FirebaseStorage storage;
    String image;
    Bitmap getpic;
    EditText docprescript;
    Button submit;

    ImageView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e__prescription__template);

        doctorname = findViewById(R.id.eprescript_doctor_name);
        doctornamebelow = findViewById(R.id.eprescript_docotor_name_below);
        doctorspecialization = findViewById(R.id.eprescript_specialization);
        clinicname = findViewById(R.id.eprescript_clinic_name);
        clinicaddress = findViewById(R.id.eprescript_address);
        contactnumber = findViewById(R.id.eprescript_contact);
        email = findViewById(R.id.eprescript_email);
        date = findViewById(R.id.eprescript_patient_date);
        docprescript = findViewById(R.id.eprescript_doctorprescription);
        submit = findViewById(R.id.eprescription_submit);
        back = findViewById(R.id.backspace);

        patientname = findViewById(R.id.eprescript_patient_name);
        patient_age = findViewById(R.id.eprescript_patient_age);
        patient_sex = findViewById(R.id.eprescript_patient_sex);
        patients_address = findViewById(R.id.eprescript_patient_address);
        docsignature = findViewById(R.id.doctor_signature);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        GlobalVariables gv = (GlobalVariables) getApplicationContext();


        Intent intent = getIntent();
        String patid = intent.getStringExtra("patid");
        String docid = intent.getStringExtra("docid");
        String clname = intent.getStringExtra("clname");
        String ddate = intent.getStringExtra("ddate");

        String clinicnameID = clinicname.toString();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), doctor_homepage.class);
                startActivity(intent);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(E_Prescription_Template.this).setTitle("Prescription").setMessage("Are you sure you want to send this prescription?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String doctor_prescription = docprescript.getText().toString();

                        Date datenow = Calendar.getInstance().getTime();
                        Map<String, Object> eprescript = new HashMap<>();
                        eprescript.put("Prescription", doctor_prescription);
                        eprescript.put("Dnt", datenow);

                        db.collection("Schedules").document(gv.getSDid()).collection("Prescription").document("Doctor_Prescription").set(eprescript);

                        Intent intent = new Intent(getApplicationContext(), doctor_homepage.class);
                        startActivity(intent);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

            }
        });

        DocumentReference documentReferenceSchedule = db.collection("Schedules").document(gv.getSDid()).collection("Prescription").document("Doctor_Prescription");
        documentReferenceSchedule.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                docprescript.setText(documentSnapshot.getString("Prescription"));


            }
        });


        DocumentReference documentReferenceDOC = db.collection("Doctors").document(docid);
        documentReferenceDOC.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                doctorname.setText(documentSnapshot.getString("LastName") + ", " + documentSnapshot.getString("FirstName") + " " + documentSnapshot.getString("MiddleInitial"));
                doctornamebelow.setText(documentSnapshot.getString("LastName") + ", " + documentSnapshot.getString("FirstName") + " " + documentSnapshot.getString("MiddleInitial"));
                doctorspecialization.setText(documentSnapshot.getString("DocType"));
                clinicname.setText(documentSnapshot.getString("ClinicName"));
                patients_address.setText(documentSnapshot.getString("Address"));
                email.setText(documentSnapshot.getString("Email"));
                String clinicnamestring = documentSnapshot.getString("ClinicName");
                db.collection("Clinics").whereEqualTo("ClinicName", clinicnamestring).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                clinicaddress.setText(queryDocumentSnapshot.getString("Address"));
                                contactnumber.setText(queryDocumentSnapshot.getString("ContactNumber"));
                            }
                        }
                    }
                });



            }
        });

        DocumentReference documentReferenceCLINIC = db.collection("Clinics").document(clname);
        documentReferenceCLINIC.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                clinicaddress.setText(documentSnapshot.getString("Address"));
                contactnumber.setText(documentSnapshot.getString("ContactNumber"));
            }
        });




        DocumentReference documentReference = db.collection("Patients").document(patid);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                patientname.setText("Patient: " + documentSnapshot.getString("LastName") + ", " + documentSnapshot.getString("FirstName") + " " + documentSnapshot.getString("MiddleInitial"));
                patient_sex.setText("Sex: " + documentSnapshot.getString("Sex"));
                patients_address.setText("Address: " + documentSnapshot.getString("Address"));




                int age = 0;
                try {

                    SimpleDateFormat format = new SimpleDateFormat("MMMM d, yyyy");
                    Date date1 = format.parse(documentSnapshot.getString("Birthday"));
                    Calendar now = Calendar.getInstance();
                    Calendar dob = Calendar.getInstance();
                    dob.setTime(date1);
                    if (dob.after(now)) {
                        throw new IllegalArgumentException("Can't be born in the future");
                    }
                    int year1 = now.get(Calendar.YEAR);
                    int year2 = dob.get(Calendar.YEAR);
                    age = year1 - year2;
                    int month1 = now.get(Calendar.MONTH);
                    int month2 = dob.get(Calendar.MONTH);
                    if (month2 > month1) {
                        age--;
                    } else if (month1 == month2) {
                        int day1 = now.get(Calendar.DAY_OF_MONTH);
                        int day2 = dob.get(Calendar.DAY_OF_MONTH);
                        if (day2 > day1) {
                            age--;
                        }
                    }
                    patient_age.setText("Age: " + age+"");
                } catch (ParseException ed) {
                    ed.printStackTrace();
                }

                Date datenow = Calendar.getInstance().getTime();
                SimpleDateFormat format1 = new SimpleDateFormat("MM/dd/yy");
                String datenowstring = format1.format(datenow);

                date.setText("Date: " + datenowstring);


                db.collection("Doctors").whereEqualTo("SignatureId", docid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                for (QueryDocumentSnapshot profile : task.getResult()) {
                                    image = profile.getString("SignatureId");
                                    storageReference = FirebaseStorage.getInstance().getReference("DoctorSignatures/" + image);
                                    try {
                                        File local = File.createTempFile("mySignature",".png");
                                        storageReference.getFile(local).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                getpic = BitmapFactory.decodeFile(local.getAbsolutePath());
                                                docsignature.setImageBitmap(getpic);
                                            }
                                        });
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                });



               


            }
        });
    }
}