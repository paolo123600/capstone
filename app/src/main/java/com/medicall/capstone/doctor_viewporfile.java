package com.medicall.capstone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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
import com.medicall.capstone.R;

import java.io.File;
import java.io.IOException;

public class doctor_viewporfile extends AppCompatActivity {

    TextView firstname;
    TextView gender;
    TextView address;
    TextView birthday;
    TextView municipality;
    TextView number;
    TextView email;
    TextView postal;
    TextView ptr;
    TextView prc;
    TextView doctype;
    ImageView back;
    //change pass
    Dialog dialog3;
    Dialog dialog4;
    Button changepass;
    Button editbutton;

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

    ImageView changeDP;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(),Login.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_viewporfile);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        dpicture = findViewById(R.id.doctor_dp);
        db = FirebaseFirestore.getInstance();
        changeDP = findViewById(R.id.editImage);

        firstname = findViewById(R.id.first_name_profile);
        gender = findViewById(R.id.gender_profile);
        address = findViewById(R.id.address_profile);
        birthday = findViewById(R.id.birthday_profile);
        municipality = findViewById(R.id.municipality_profile);
        number = findViewById(R.id.number_profile);
        email = findViewById(R.id.email_profile);
        postal = findViewById(R.id.postal_profile);
        ptr = findViewById(R.id.number_ptr);
        prc = findViewById(R.id.number_prc);
        doctype = findViewById(R.id.doctor_type);
        back = findViewById(R.id.backspace);

        editbutton = findViewById(R.id.editbtn);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();

        firstname.setKeyListener(null);
        gender.setKeyListener(null);
        address.setKeyListener(null);
        birthday.setKeyListener(null);
        municipality.setKeyListener(null);
        number.setKeyListener(null);
        email.setKeyListener(null);
        postal.setKeyListener(null);
        doctype.setKeyListener(null);
        ptr.setKeyListener(null);
        prc.setKeyListener(null);
        changepass = (Button) findViewById(R.id.changepass);
        editbutton = findViewById(R.id.editbtn);
        mAuth = FirebaseAuth.getInstance();


        //Change Picture
        changeDP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(doctor_viewporfile.this, doc_changepic.class);
                startActivity(intent);
            }
        });

        //Get Profile Picture
        db.collection("Doctors").whereEqualTo("UserId", userId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot querySnapshot = task.getResult();
                    if(!querySnapshot.isEmpty()){
                        for(QueryDocumentSnapshot doctor : task.getResult()){
                            image = doctor.getString("StorageId");
                            if(image.equals("None")){
                                dpicture.setBackgroundResource(R.drawable.circlebackground);
                            }
                            else{
                                storageReference = FirebaseStorage.getInstance().getReference("DoctorPicture/" + image);
                                try{
                                    File local = File.createTempFile("myDP","");
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

        //changepass
        changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog3 = new Dialog(doctor_viewporfile.this);
                dialog3.setContentView(R.layout.changepasspopdoc);
                dialog3.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog3.show();

                Button cancel = dialog3.findViewById(R.id.cancel);
                Button conf = dialog3.findViewById(R.id.confchangepass);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        onBackPressed();
                    }
                });
                ////
                mAuth.sendPasswordResetEmail(String.valueOf(email.getText())).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete( Task<Void> task) {
                        conf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog3.dismiss();
                                dialog4 = new Dialog(doctor_viewporfile.this);
                                dialog4.setContentView(R.layout.confirmpopdoc);
                                dialog4.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                dialog4.show();
                                Button conf1 = dialog4.findViewById(R.id.conf);
                                conf1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        dialog4.dismiss();
                                        onBackPressed();
                                    }
                                });
                            }
                        });
                    }
                });

            }
        });


        DocumentReference documentReference = fStore.collection("Doctors").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                firstname.setText(documentSnapshot.getString("LastName") + ", " + documentSnapshot.getString("FirstName") + " " + documentSnapshot.getString("MiddleInitial"));
                gender.setText(documentSnapshot.getString("Gender"));
                address.setText(documentSnapshot.getString("Address"));
                municipality.setText(documentSnapshot.getString("Municipality"));
                number.setText(documentSnapshot.getString("ContactNumber"));
                email.setText(documentSnapshot.getString("Email"));
                postal.setText(documentSnapshot.getString("PostalCode"));
                birthday.setText(documentSnapshot.getString("Birthday"));
                doctype.setText(documentSnapshot.getString("DocType"));
                ptr.setText(documentSnapshot.getString("PTR"));
                prc.setText(documentSnapshot.getString("PRC"));
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
}
