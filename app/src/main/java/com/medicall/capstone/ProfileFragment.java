package com.medicall.capstone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
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
import com.medicall.capstone.R;

import java.io.File;
import java.io.IOException;

public class ProfileFragment extends AppCompatActivity {

    TextView firstname;
    TextView gender;
    TextView address;
    TextView birthday;
    TextView municipality;
    TextView number;
    TextView email;
    TextView postal;
    ImageView back;
    Button changepass;
    Button editbutton;
    Dialog dialog;
    Dialog dialog1;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    FirebaseAuth mAuth;

    // Profile Picture
    Bitmap profilepic;
    StorageReference ref;
    String image;
    ImageView dpicture;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    FirebaseFirestore db;

    Button changeDP;
    ProgressDialog progressDialog;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(),Login.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_fragment);

        mAuth = FirebaseAuth.getInstance();
        firstname = findViewById(R.id.first_name_profile);
        gender = findViewById(R.id.gender_profile);
        address = findViewById(R.id.address_profile);
        birthday = findViewById(R.id.birthday_profile);
        municipality = findViewById(R.id.municipality_profile);
        number = findViewById(R.id.number_profile);
        email = findViewById(R.id.email_profile);
        postal = findViewById(R.id.postal_profile);
        back = findViewById(R.id.backspace);
        changepass = (Button) findViewById(R.id.changepass);
        editbutton = findViewById(R.id.editbtn);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        dpicture = findViewById(R.id.patient_dp);
        db = FirebaseFirestore.getInstance();

        changeDP = findViewById(R.id.changePicture);

        changeDP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileFragment.this, Patient_ChangePicture.class);
                startActivity(intent);
            }
        });

        db.collection("Patients").whereEqualTo("UserId",userId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
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

        firstname.setKeyListener(null);
        gender.setKeyListener(null);
        address.setKeyListener(null);
        birthday.setKeyListener(null);
        municipality.setKeyListener(null);
        number.setKeyListener(null);
        email.setKeyListener(null);
        postal.setKeyListener(null);

        //change pass
        changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(ProfileFragment.this);
                dialog.setContentView(R.layout.changepasspop);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                Button cancel = dialog.findViewById(R.id.cancel);
                Button conf = dialog.findViewById(R.id.confchangepass);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {

                        onBackPressed();
                    }
                });
                mAuth.sendPasswordResetEmail(String.valueOf(email.getText())).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete( Task<Void> task) {
                        conf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                dialog1 = new Dialog(ProfileFragment.this);
                                dialog1.setContentView(R.layout.confirmpop);
                                dialog1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                dialog1.show();
                                Button conf1 = dialog1.findViewById(R.id.conf);
                                conf1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        dialog1.dismiss();
                                        onBackPressed();

                                    }
                                });
                            }
                        });
                    }
                });

            }
        });




        DocumentReference documentReference = fStore.collection("Patients").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                firstname.setText(documentSnapshot.getString("LastName") + ", " + documentSnapshot.getString("FirstName") + " " + documentSnapshot.getString("MiddleInitial"));
                gender.setText(documentSnapshot.getString("Sex"));
                address.setText(documentSnapshot.getString("Address"));
                municipality.setText(documentSnapshot.getString("Municipality"));
                number.setText(documentSnapshot.getString("Contact"));
                email.setText(documentSnapshot.getString("Email"));
                postal.setText(documentSnapshot.getString("Postal"));
                birthday.setText(documentSnapshot.getString("Birthday"));
            }
        });



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
            }
        });

        editbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), profile_edit.class);
                startActivity(intent);
            }
        });
    }

}