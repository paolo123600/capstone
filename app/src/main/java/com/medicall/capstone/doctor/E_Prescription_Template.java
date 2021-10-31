package com.medicall.capstone.doctor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.medicall.capstone.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class E_Prescription_Template extends AppCompatActivity {

    TextView doctorname, doctornamebelow, doctorspecialization, clinicname, clinicaddress, contactnumber, email;
    TextView patientname, patient_age, patient_sex, patients_address;

    FirebaseAuth fAuth;
    FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e__prescription__template);

        doctorname = findViewById(R.id.eprescrip_doctor_name);
        doctornamebelow = findViewById(R.id.eprescrip_docotor_name_below);
        doctorspecialization = findViewById(R.id.eprescrip_specialization);
        clinicname = findViewById(R.id.eprescrip_clinic_name);
        clinicaddress = findViewById(R.id.eprescrip_address);
        contactnumber = findViewById(R.id.eprescrip_contact);
        email = findViewById(R.id.eprescrip_email);

        patientname = findViewById(R.id.eprescrip_patient_name);
        patient_age = findViewById(R.id.eprescrip_patient_age);
        patient_sex = findViewById(R.id.eprescrip_patient_sex);
        patients_address = findViewById(R.id.eprescrip_patient_address);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        String patid = intent.getStringExtra("patid");
        String docid = intent.getStringExtra("docid");
        String clname = intent.getStringExtra("clname");

        String clinicnameID = clinicname.toString();

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
                patientname.setText(documentSnapshot.getString("LastName") + ", " + documentSnapshot.getString("FirstName") + " " + documentSnapshot.getString("MiddleInitial"));
                patient_sex.setText(documentSnapshot.getString("Sex"));
                patients_address.setText(documentSnapshot.getString("Address"));


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
                    patient_age.setText(age+"");
                } catch (ParseException ed) {
                    ed.printStackTrace();
                }

            }
        });
    }
}