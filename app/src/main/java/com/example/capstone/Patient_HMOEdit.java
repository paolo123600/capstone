package com.example.capstone;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Patient_HMOEdit extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    String userID;
    GlobalVariables gv;
    EditText Cnumber;
    Button Cancel, Confirm;
    String hmoname;

    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_patient_hmo_edit);
        Cancel = (Button) findViewById(R.id.edithmo_cancel);
        Confirm = (Button) findViewById(R.id.edithmo_confirm);
        Cnumber = (EditText) findViewById(R.id.inpCardNumber);

        gv = (GlobalVariables) getApplicationContext();
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        Cnumber.setText(gv.getEditHMO_cardNumber());
        hmoname = gv.getEditHMO_hmoName();

        back = findViewById(R.id.backspace);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Patient_HMOList.class);
                startActivity(intent);
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Patient_HMOEdit.this, Patient_HMOList.class);
                startActivity(intent);
            }
        });

        Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Patient_HMOEdit.this);
                builder.setCancelable(true);
                builder.setTitle("Edit Health Insurance");
                builder.setMessage("Do you want to save changes?");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Map<String, Object> updateHMO = new HashMap<>();
                                updateHMO.put("CardNumber", String.valueOf(Cnumber.getText()));

                                db.collection("Patients").document(userID).collection("HMO").document(hmoname).update(updateHMO)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(Patient_HMOEdit.this, "HMO has been updated", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(Patient_HMOEdit.this, Patient_HMOList.class);
                                                startActivity(intent);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Patient_HMOEdit.this, "Failed to update", Toast.LENGTH_SHORT).show();
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
}
