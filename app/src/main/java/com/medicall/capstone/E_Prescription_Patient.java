package com.medicall.capstone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class E_Prescription_Patient extends AppCompatActivity {

    TextView doctorname, doctornamebelow, doctorspecialization, clinicname, clinicaddress, contactnumber, email, date;
    TextView patientname, patient_age, patient_sex, patients_address;

    FirebaseAuth fAuth;
    FirebaseFirestore db;

    ImageView docsignature;

    private StorageReference storageReference;
    private FirebaseStorage storage;
    String image;
    Bitmap getpic;
    TextView docprescript;

    String patid,docid,clname;
    Date ddate;

    LinearLayout toolbar;
    Button screenshot;
    Dialog saved;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e__prescription__patient);
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        doctorname = findViewById(R.id.patient_eprescript_doctor_name);
        doctornamebelow = findViewById(R.id.patient_eprescript_docotor_name_below);
        doctorspecialization = findViewById(R.id.patient_eprescript_specialization);
        clinicname = findViewById(R.id.patient_eprescript_clinic_name);
        clinicaddress = findViewById(R.id.patient_eprescript_address);
        contactnumber = findViewById(R.id.patient_eprescript_contact);
        email = findViewById(R.id.patient_eprescript_email);
        date = findViewById(R.id.patient_eprescript_patient_date);
        docprescript = findViewById(R.id.patient_eprescript_doctorprescription);
        back = findViewById(R.id.backspace);

        patientname = findViewById(R.id.patient_eprescript_patient_name);
        patient_age = findViewById(R.id.patient_eprescript_patient_age);
        patient_sex = findViewById(R.id.patient_eprescript_patient_sex);
        patients_address = findViewById(R.id.patient_eprescript_patient_address);
        docsignature = findViewById(R.id.doctor_signature);

        toolbar = findViewById(R.id.toolbar);
        screenshot = findViewById(R.id.btnScreenshot);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        GlobalVariables gv = (GlobalVariables) getApplicationContext();

        saved = new Dialog (E_Prescription_Patient.this);

        Intent intent = getIntent();
        String schedid = intent.getStringExtra("schedid");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        db.collection("Schedules").document(schedid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                 DocumentSnapshot documentSnapshot = task.getResult();
                 patid = documentSnapshot.getString("PatientUId");
                 docid = documentSnapshot.getString("DoctorUId");
                 clname = documentSnapshot.getString("ClinicName");
                 ddate = documentSnapshot.getDate("Date");

                    DocumentReference documentReferenceSchedule = db.collection("Schedules").document(schedid).collection("Prescription").document("Doctor_Prescription");
                    documentReferenceSchedule.addSnapshotListener(E_Prescription_Patient.this, new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            docprescript.setText(documentSnapshot.getString("Prescription"));


                        }


                    });

                    DocumentReference documentReferenceDOC = db.collection("Doctors").document(docid);
                    documentReferenceDOC.addSnapshotListener(E_Prescription_Patient.this, new EventListener<DocumentSnapshot>() {
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

                    DocumentReference documentReference = db.collection("Patients").document(patid);
                    documentReference.addSnapshotListener(E_Prescription_Patient.this, new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            patientname.setText("Patient: " + documentSnapshot.getString("LastName") + ", " + documentSnapshot.getString("FirstName") + " " + documentSnapshot.getString("MiddleInitial"));
                            patient_sex.setText("Sex: " + documentSnapshot.getString("Sex"));
                            patients_address.setText("Address: " + documentSnapshot.getString("Address"));




                            int age = 0;
                            try {
                                SimpleDateFormat format = new SimpleDateFormat("MMMM d ,yyyy");
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
                            String datenowstring = format1.format(ddate);

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
        });


        screenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(E_Prescription_Patient.this);
                builder.setCancelable(true);
                builder.setTitle("Save Image");

                builder.setMessage("Do you want to save your prescription in your folder?");
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                takeSS(v);
                                saved.setContentView(R.layout.screenshot_saved);
                                saved.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                ImageView closebtn = saved.findViewById(R.id.imageClose);
                                Button okay = saved.findViewById(R.id.btnHome);

                                closebtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(E_Prescription_Patient.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                });

                                okay.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(E_Prescription_Patient.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                saved.show();
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

    }
    public void takeSS(View view){
        toolbar.setVisibility(View.GONE);
        screenshot.setVisibility(View.GONE);
        new android.os.Handler().postDelayed(
            new Runnable() {
                public void run() {
                    View view1 = getWindow().getDecorView().getRootView();
                    view1.setDrawingCacheEnabled(true);

                    Bitmap bitmap = Bitmap.createBitmap(view1.getDrawingCache());
                    view1.setDrawingCacheEnabled(false);

                    String filename = Environment.getExternalStorageDirectory() + "/Download/" + Calendar.getInstance().getTime().toString() + ".jpg";
                    File fileScreenshot = new File(filename);

                    FileOutputStream fileOutputStream = null;

                    try{
                        fileOutputStream = new FileOutputStream(fileScreenshot);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }
                    toolbar.setVisibility(View.VISIBLE);
                    screenshot.setVisibility(View.VISIBLE);
                }
            }, 1);

    }
}