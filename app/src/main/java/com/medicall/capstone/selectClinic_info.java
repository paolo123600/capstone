package com.medicall.capstone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.io.IOException;

public class selectClinic_info extends AppCompatActivity {

    TextView clname, clcontactnum, clemail, claddress, clmunicipality;
    FirebaseFirestore db;
    GlobalVariables gv;
    String clinicnamegv;
    String clinicName;
    ImageView back;

    private StorageReference storageReference;
    private FirebaseStorage storage;

    String storageID;
    Bitmap currentPic;
    ImageView newprofile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_clinic_info);

        clname = findViewById(R.id.cl_name_info);
        clcontactnum = findViewById(R.id.cl_contact_info);
        clemail = findViewById(R.id.cl_email_info);
        claddress = findViewById(R.id.cl_address_info);
        clmunicipality = findViewById(R.id.cl_municipality_info);
        newprofile = findViewById(R.id.clinicImageInfo);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        gv = (GlobalVariables) getApplicationContext();
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        clinicnamegv = intent.getStringExtra("ClinicUid");
        clinicName = intent.getStringExtra("ClinicName");

        back = findViewById(R.id.backspace);

        db.collection("Clinics").whereEqualTo("ClinicName",clinicName)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot querySnapshot = task.getResult();
                    if(!querySnapshot.isEmpty()){
                        for(QueryDocumentSnapshot profilepic : task.getResult()){
                            storageID = profilepic.getString("StorageId");
                            if(storageID.equals("None")){
                                newprofile.setBackgroundResource(R.drawable.circlebackground);
                            }
                            else{
                                storageReference = FirebaseStorage.getInstance().getReference("ClinicPicture/" + storageID);
                                try{
                                    File local = File.createTempFile("myProfilePicture",".jpg");
                                    storageReference.getFile(local)
                                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                    currentPic = BitmapFactory.decodeFile(local.getAbsolutePath());
                                                    newprofile.setImageBitmap(currentPic);
                                                }
                                            });
                                }
                                catch(IOException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), selectClinic.class);
                startActivity(intent);
            }
        });

        DocumentReference documentReference = db.collection("Clinics").document(clinicnamegv);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                clname.setText(documentSnapshot.getString("ClinicName"));
                clcontactnum.setText(documentSnapshot.getString("ContactNumber"));
                clemail.setText(documentSnapshot.getString("Email"));
                claddress.setText(documentSnapshot.getString("Address"));
                clmunicipality.setText(documentSnapshot.getString("Municipality"));
            }
        });
    }
}