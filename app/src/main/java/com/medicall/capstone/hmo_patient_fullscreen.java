package com.medicall.capstone;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.medicall.capstone.utilities.PreferenceManager;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class hmo_patient_fullscreen extends AppCompatActivity {

    SubsamplingScaleImageView fullScreen;
    FirebaseFirestore db;
    GlobalVariables gv;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    StorageReference ref;
    String image;
    Bitmap getpic;
    ProgressBar prog;
    ImageView download, back;
    OutputStream outputStream;
    Bitmap downloadhmo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hmo_patient_fullscreen);

        db = FirebaseFirestore.getInstance();
        gv = (GlobalVariables) getApplicationContext();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        prog = (ProgressBar) findViewById(R.id.loading);
        download = (ImageView) findViewById(R.id.btndownload);
        back = findViewById(R.id.backspace);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        fullScreen = (SubsamplingScaleImageView) findViewById(R.id.fullScreenImage);


        db.collection("Schedules").whereEqualTo("ClinicName", gv.getPatient_HMO_ClinicName()).whereEqualTo("PatientUId", gv.getPatient_HMO_PatientUId())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot querySnapshot = task.getResult();
                    if(!querySnapshot.isEmpty()){
                        for(QueryDocumentSnapshot pic : task.getResult()){
                            image = pic.getString("StorageId");
                            storageReference = FirebaseStorage.getInstance().getReference("PatientHMO/" + image);
                            try{
                                File local = File.createTempFile("myHMO", ".jpg");
                                storageReference.getFile(local)
                                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                prog.setVisibility(View.GONE);
                                                download.setVisibility(View.VISIBLE);
                                                getpic = BitmapFactory.decodeFile(local.getAbsolutePath());
                                                fullScreen.setImage(ImageSource.bitmap(getpic));
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
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(hmo_patient_fullscreen.this);
                builder.setTitle("Download file");
                builder.setMessage("Do you want to download this file?");
                builder.setCancelable(true);
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                downloadHMO();
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

    private void downloadHMO(){
        db.collection("Schedules").whereEqualTo("ClinicName", gv.getPatient_HMO_ClinicName()).whereEqualTo("PatientUId", gv.getPatient_HMO_PatientUId())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot querySnapshot = task.getResult();
                    if(!querySnapshot.isEmpty()){
                        for(QueryDocumentSnapshot download : task.getResult()){
                            storageReference = FirebaseStorage.getInstance().getReference();
                            ref = storageReference.child("PatientHMO/" + download.getString("StorageId"));
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = uri.toString();
                                    downloadFile(hmo_patient_fullscreen.this, "PatientHMO" + download.getString("StorageId"), ".jpg", DIRECTORY_DOWNLOADS, url);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        }
                    }
                }
            }
        });

    }

    public void downloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String url){
        DownloadManager downloadManager = (DownloadManager) context.
                getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);

        downloadManager.enqueue(request);
    }

}
