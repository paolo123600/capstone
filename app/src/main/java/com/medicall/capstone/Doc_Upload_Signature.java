package com.medicall.capstone;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.medicall.capstone.utilities.Constants;
import com.medicall.capstone.utilities.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Doc_Upload_Signature extends AppCompatActivity {
    Button upload, cancel;
    ImageView retrieveSG, back, displaysignature;

    FirebaseAuth fAuth;
    private PreferenceManager preferenceManager;
    String userID;

    private StorageReference storageReference;
    private FirebaseStorage storage;
    FirebaseFirestore db;

    ProgressDialog progressDialog;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    public Uri imageUri;
    FirebaseStorage storage2;
    StorageReference photoref;

    String signatureID;
    Bitmap currentPic;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_signature);
        upload = findViewById(R.id.btnUpload);
        cancel = findViewById(R.id.btnCancel);
        retrieveSG = findViewById(R.id.getSignature);
        back = findViewById(R.id.backspace);
        displaysignature = findViewById(R.id.previewSignature);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        storage2 = FirebaseStorage.getInstance();

        preferenceManager = new PreferenceManager(getApplicationContext());
        userID = preferenceManager.getString(Constants.KEY_USER_ID);

        db.collection("Doctors").whereEqualTo("SignatureId", userID)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot querySnapshot = task.getResult();
                    if(!querySnapshot.isEmpty()){
                        for(QueryDocumentSnapshot sign : task.getResult()){
                            signatureID = sign.getString("SignatureId");
                            if(signatureID.equals("None")){
                                displaysignature.setBackgroundResource(R.drawable.nopreview);
                            }
                            else{
                                storageReference = FirebaseStorage.getInstance().getReference("DoctorSignatures/" + signatureID);
                                try{
                                    File local = File.createTempFile("myProfilePicture", ".jpg");
                                    storageReference.getFile(local)
                                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                    currentPic = BitmapFactory.decodeFile(local.getAbsolutePath());
                                                    displaysignature.setImageBitmap(currentPic);
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
                onBackPressed();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        retrieveSG.setOnClickListener(new View.OnClickListener() {
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

        displaysignature.setOnClickListener(new View.OnClickListener() {
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

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Doc_Upload_Signature.this);
                builder.setCancelable(true);
                builder.setTitle("Finalization");
                builder.setMessage("Upload your signature?");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.collection("Doctors").whereEqualTo("UserId",userID).get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful()){
                                                    QuerySnapshot querySnapshot = task.getResult();
                                                    if(!querySnapshot.isEmpty()){
                                                        for(QueryDocumentSnapshot delete : task.getResult()){
                                                            String signatureID = delete.getString("SignatureId");
                                                            if(signatureID.equals("None")){
                                                                Map<String, Object> profile = new HashMap<>();
                                                                profile.put("SignatureId", userID);
                                                                db.collection("Doctors").document(userID).update(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        updateProfile();
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(Doc_Upload_Signature.this, "Upload image failed!", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            }
                                                            else{
                                                                photoref = storage2.getReferenceFromUrl("gs://medicall-6effc.appspot.com/DoctorSignatures").child(signatureID);
                                                                photoref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        updateProfile();
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
                                            }
                                        });
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

    public void updateProfile(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating image..");
        progressDialog.show();

        db.collection("Doctors").whereEqualTo("UserId",userID)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot querySnapshot = task.getResult();
                    if(!querySnapshot.isEmpty()){
                        for(QueryDocumentSnapshot profile : task.getResult()){
                            String fileName = profile.getString("SignatureId");
                            storageReference = FirebaseStorage.getInstance().getReference("DoctorSignatures/" + fileName);
                            storageReference.child("DoctorSignatures/" + fileName);
                            Bitmap bmp = null;
                            try {
                                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.PNG, 10, baos);
                            byte[] fileinbytes = baos.toByteArray();
                            UploadTask uploadTask = storageReference.putBytes(fileinbytes);

                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    if(progressDialog.isShowing()){
                                        progressDialog.dismiss();
                                    }
                                    Toast.makeText(Doc_Upload_Signature.this, "Signature has been uploaded!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Doc_Upload_Signature.this, doctor_homepage.class);
                                    finish();
                                    startActivity(intent);
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            if(progressDialog.isShowing()){
                                                progressDialog.dismiss();
                                            }
                                            Toast.makeText(Doc_Upload_Signature.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
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
            retrieveSG.setVisibility(View.INVISIBLE);
            displaysignature.setImageURI(imageUri);
            upload.setBackgroundResource(R.drawable.darkround);
            upload.setEnabled(true);

        }
    }
}
