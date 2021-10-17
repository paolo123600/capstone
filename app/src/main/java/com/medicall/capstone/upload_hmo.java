package com.medicall.capstone;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.medicall.capstone.R;

import com.medicall.capstone.utilities.Constants;
import com.medicall.capstone.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class upload_hmo extends AppCompatActivity {

    ImageView upload;
    public Uri imageUri;
    Button uploadbtn;
    ImageView back;
    Switch switchhmo;
    TextView hmoinfo;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    FirebaseAuth fAuth;
    FirebaseFirestore db;
    String userId, patientid, storageid, patientName;
    TextView cardnumber;
    TextView expiration;
    GlobalVariables gv;
    String patuid;
    private PreferenceManager preferenceManager;

    ProgressDialog progressDialog;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_hmo);

        upload = findViewById(R.id.imgUpload);
        hmoinfo = findViewById(R.id.txtview_hmopreview);
        uploadbtn = findViewById(R.id.btn_upload);
        cardnumber = (TextView) findViewById(R.id.cardNumber);
        expiration = (TextView) findViewById(R.id.expired);
        switchhmo = findViewById(R.id.switch_hmo);
        preferenceManager = new PreferenceManager(getApplicationContext());
        back = findViewById(R.id.backspace);

        gv = (GlobalVariables) getApplicationContext();
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        upload.setVisibility(ImageView.GONE);
        hmoinfo.setVisibility(TextView.GONE);

        patuid = preferenceManager.getString(Constants.KEY_USER_ID);

        DocumentReference documentReference = db.collection("Patients").document(patuid).collection("HMO").document(gv.getHMOName());
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                cardnumber.setText("Card Number: " + documentSnapshot.getString("CardNumber"));
                expiration.setText("Expiry Date: " + documentSnapshot.getString("ExpiryDate"));
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                        String [] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else{
                        selectImage();
                    }
                }
                else{
                    selectImage();
                }
            }
        });

        switchhmo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switchhmo.isChecked()){
                    upload.setVisibility(ImageView.VISIBLE);
                    hmoinfo.setVisibility(TextView.VISIBLE);
                }
                else{
                    upload.setVisibility(ImageView.GONE);
                    hmoinfo.setVisibility(TextView.GONE);
                }
            }
        });

        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(upload_hmo.this);
                builder.setCancelable(true);
                builder.setTitle("Finalization");
                builder.setMessage("Are you sure about your HMO?");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(switchhmo.isChecked()){
                                    uploadHMO();
                                }
                                else{
                                    uploadHMONoPic();
                                }
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


    public void selectImage(){
        Intent intent= new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case PERMISSION_CODE:{
                if(grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    selectImage();
                }
                else{
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            upload.setImageURI(imageUri);
        }
    }


    public void uploadHMO(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading file..");
        progressDialog.show();

        SimpleDateFormat formatt = new SimpleDateFormat("yyyy_dd", Locale.TAIWAN);
        Date now = new Date();
        String fileName = patuid + "_" + formatt.format(now);

        db.collection("Patients").document(patuid).collection("HMO").document(gv.getHMOName()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String cardnum = documentSnapshot.getString("CardNumber");
                String expiry = documentSnapshot.getString("ExpiryDate");



                db.collection("Doctors").whereEqualTo("UserId", gv.getSDDocUid())
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            if(!querySnapshot.isEmpty()){
                                for(QueryDocumentSnapshot doctor: task.getResult()){
                                    storageReference = FirebaseStorage.getInstance().getReference("PatientHMO/" +fileName);
                                    storageReference.putFile(imageUri)
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                    String schedid = db.collection("Schedules").document().getId();

                                                    Map<String, Object> patienthmo = new HashMap<>();
                                                    patienthmo.put("ClinicName", doctor.getString("ClinicName"));
                                                    patienthmo.put("PatientUId", patuid);
                                                    patienthmo.put("StorageId", fileName);
                                                    patienthmo.put("CardNumber", cardnum);
                                                    patienthmo.put("ExpiryDate", expiry);
                                                    patienthmo.put("StartTime", gv.getStartTime());
                                                    patienthmo.put("EndTime", gv.getEndTime());
                                                    patienthmo.put("Date", gv.getDateconsult());
                                                    patienthmo.put("Dnt", gv.getDateandtime());
                                                    patienthmo.put("HMOName", gv.getHMOName());
                                                    patienthmo.put("Position", gv.getPost()+1);
                                                    patienthmo.put("Status", "Pending Approval");
                                                    patienthmo.put("DoctorUId", gv.getSDDocUid());
                                                    patienthmo.put("Price", "HMO");
                                                    patienthmo.put("SchedId", schedid);

                                                    db.collection("Schedules").document(schedid).set(patienthmo)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                    if(progressDialog.isShowing()){
                                                                        progressDialog.dismiss();
                                                                    }
                                                                    Toast.makeText(upload_hmo.this, "You have successfully booked a schedule!", Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(upload_hmo.this, MainActivity.class);
                                                                    startActivity(intent);
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(upload_hmo.this, "Failed to upload!", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                    upload.setImageURI(null);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    if(progressDialog.isShowing()){
                                                        progressDialog.dismiss();
                                                    }
                                                    Toast.makeText(upload_hmo.this, "Failed to Upload!", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                                                }
                                            });
                                }
                            }
                        }
                    }
                });
            }
        });

    }

    public void uploadHMONoPic(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading file..");
        progressDialog.show();

        db.collection("Patients").document(patuid).collection("HMO").document(gv.getHMOName()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String cardnum = documentSnapshot.getString("CardNumber");
                String expiry = documentSnapshot.getString("ExpiryDate");


                db.collection("Doctors").whereEqualTo("UserId", gv.getSDDocUid())
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            if(!querySnapshot.isEmpty()){
                                for(QueryDocumentSnapshot doctor: task.getResult()){

                                    String schedid = db.collection("Schedules").document().getId();

                                    Map<String, Object> patienthmo = new HashMap<>();
                                    patienthmo.put("ClinicName", doctor.getString("ClinicName"));
                                    patienthmo.put("PatientUId", patuid);
                                    patienthmo.put("CardNumber", cardnum);
                                    patienthmo.put("ExpiryDate", expiry);
                                    patienthmo.put("StartTime", gv.getStartTime());
                                    patienthmo.put("EndTime", gv.getEndTime());
                                    patienthmo.put("Date", gv.getDateconsult());
                                    patienthmo.put("Dnt", gv.getDateandtime());
                                    patienthmo.put("HMOName", gv.getHMOName());
                                    patienthmo.put("Position", gv.getPost()+1);
                                    patienthmo.put("Status", "Pending Approval");
                                    patienthmo.put("DoctorUId", gv.getSDDocUid());
                                    patienthmo.put("StorageId","NoPic");
                                    patienthmo.put("Price", "HMO");
                                    patienthmo.put("SchedId", schedid);

                                    db.collection("Schedules").document(schedid).set(patienthmo)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    if(progressDialog.isShowing()){
                                                        progressDialog.dismiss();
                                                    }
                                                    Toast.makeText(upload_hmo.this, "You have successfully booked a schedule!", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(upload_hmo.this, MainActivity.class);
                                                    startActivity(intent);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(upload_hmo.this, "Failed to upload!", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                }
                            }
                        }
                    }
                });

            }
        });


    }
}
