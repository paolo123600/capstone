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
import com.medicall.capstone.R;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class View_doctor_info extends AppCompatActivity {

    private TextView docname;
    private TextView clinicname;
    private TextView PRC;
    private TextView PTR;
    private TextView docgender;
    private TextView docbday;
    private TextView docschoolgrad;
    private TextView docyeargrad;
    private TextView docspecialty;
    GlobalVariables gv;
    String docid;
    FirebaseFirestore db;
    ImageView back;

    String name;
    String bold;
    String boldname;

    private StorageReference storageReference;
    private FirebaseStorage storage;
    String image;
    Bitmap getpic;
    CircleImageView profpicturedoc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_doctor_info);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        profpicturedoc = findViewById(R.id.view_doc_profilepic);

        docname = findViewById(R.id.View_docname);
        clinicname = findViewById(R.id.View_docclinic_name);
        PRC = findViewById(R.id.View_PRC);
        PTR = findViewById(R.id.View_PTR);
        docgender = findViewById(R.id.View_docgender);
        docbday = findViewById(R.id.View_docbday);
        docschoolgrad = findViewById(R.id.View_docschool);
        docyeargrad = findViewById(R.id.View_docyeargrad);
        docspecialty = findViewById(R.id.View_docspecialty);

        gv = (GlobalVariables) getApplicationContext();
        db = FirebaseFirestore.getInstance();
        docid = gv.getSDDocUid();

        back = findViewById(R.id.backspace);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), selectDoc.class);
                startActivity(intent);
            }
        });



        DocumentReference documentReference = db.collection("Doctors").document(docid);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                docname.setText(documentSnapshot.getString("FirstName") + " " + documentSnapshot.getString("LastName"));
                clinicname.setText(documentSnapshot.getString("ClinicName"));
                PRC.setText(documentSnapshot.getString("PRC"));
                PTR.setText(documentSnapshot.getString("PTR"));
                docgender.setText(documentSnapshot.getString("Gender"));
                docbday.setText(documentSnapshot.getString("Birthday"));
                docschoolgrad.setText(documentSnapshot.getString("SchoolGrad"));
                docyeargrad.setText(documentSnapshot.getString("YearGrad"));
                docspecialty.setText(documentSnapshot.getString("DocType"));

                db.collection("Doctors").whereEqualTo("StorageId", docid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                for (QueryDocumentSnapshot profile : task.getResult()) {
                                    image = profile.getString("StorageId");
                                    storageReference = FirebaseStorage.getInstance().getReference("DoctorPicture/" + image);
                                    try {
                                        File local = File.createTempFile("myProfilePicture","");
                                        storageReference.getFile(local).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                getpic = BitmapFactory.decodeFile(local.getAbsolutePath());
                                                profpicturedoc.setImageBitmap(getpic);
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