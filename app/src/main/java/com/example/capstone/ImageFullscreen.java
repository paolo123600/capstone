package com.example.capstone;

import android.app.DownloadManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.capstone.utilities.PreferenceManager;
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

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class ImageFullscreen extends AppCompatActivity {

    SubsamplingScaleImageView fullScreen;
    FirebaseFirestore db;
    private PreferenceManager preferenceManager;
    GlobalVariables gv;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    StorageReference ref;
    String image;
    Bitmap getpic;
    ProgressBar prog;
    ImageView download;
    OutputStream outputStream;
    Bitmap downloadhmo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_fullscreen);

        preferenceManager = new PreferenceManager(getApplicationContext());
        db = FirebaseFirestore.getInstance();
        gv = (GlobalVariables) getApplicationContext();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        prog = (ProgressBar) findViewById(R.id.loading);
        download = (ImageView) findViewById(R.id.btndownload);

        fullScreen = (SubsamplingScaleImageView) findViewById(R.id.fullScreenImage);


        db.collection("Schedules").whereEqualTo("ClinicName", preferenceManager.getString("ClinicName")).whereEqualTo("PatientUId", gv.getPending_patUid())
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
                AlertDialog.Builder builder = new AlertDialog.Builder(ImageFullscreen.this);
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
        db.collection("Schedules").whereEqualTo("ClinicName", preferenceManager.getString("ClinicName")).whereEqualTo("PatientUId", gv.getPending_patUid())
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
                                    downloadFile(ImageFullscreen.this, "PatientHMO" + download.getString("StorageId"), ".jpg", DIRECTORY_DOWNLOADS, url);
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
