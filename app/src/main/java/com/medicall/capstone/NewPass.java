package com.medicall.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.medicall.capstone.R;

import java.util.HashMap;
import java.util.Map;

public class NewPass extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    Button continueforgot;
    String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newpass_forgot);
        continueforgot = (Button) findViewById(R.id.btn_continueforgotpass);
        GlobalVariables gv =(GlobalVariables) getApplicationContext ();
        continueforgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = gv.getEmail();
                Newpass();
            }


        });





    }

    private void Newpass(){
        GlobalVariables gv = (GlobalVariables)getApplicationContext();
        String Pass = gv.getPassword();

        mAuth.confirmPasswordReset(email,Pass)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Map<String,Object> Patients = new HashMap<>();
                        Patients.put("Pass",Pass);

                        db.collection("Patents").document(Uid)
                                .set(Patients)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        new AlertDialog.Builder(NewPass.this)
                                                .setTitle("New Password Saved")
                                                .setMessage("Login to New Password")
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent(NewPass.this,Login.class);
                                                        startActivity(intent);
                                                    }
                                                });
                                    }
                                });

                    }
                });


    }
}