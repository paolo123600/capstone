package com.example.capstone;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.capstone.utilities.Constants;
import com.example.capstone.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Sign_Up_VerifyActivity extends AppCompatActivity {
    EditText ET_VCode;
    Button btn_Continue;
    FirebaseFirestore db;
    String VEmail= "";
    String Vcode="";
    String email ="";
    private FirebaseAuth mAuth;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_verfiy);
        GlobalVariables gv =(GlobalVariables) getApplicationContext ();

        preferenceManager = new PreferenceManager(getApplicationContext());

        btn_Continue= (Button) findViewById(R.id.btn_verify);
        ET_VCode=(EditText) findViewById(R.id.verify);
        db= FirebaseFirestore.getInstance();
        btn_Continue.setOnClickListener(new View.OnClickListener() {
            static final String TAG = "Read Data Activity";
            @Override
            public void onClick(View view) {
                String EVCode= ET_VCode.getText().toString();
                //Getting values

                email = gv.getEmail();
                mAuth = FirebaseAuth.getInstance();



                db.collection("Verification")
                        .whereEqualTo("Email",email)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){


                                        //getting the data and checking if it is equal
                                    for(QueryDocumentSnapshot document : task.getResult()) {

                                        Log.d(TAG, document.getId() + "=>" + document.getData());
                                         VEmail = document.get("Email").toString();
                                         Vcode = document.get("VCode").toString();


                                    }
                                        //deleting the email in verification
                                        if(email.equals(VEmail)&&EVCode.equals(Vcode)){

                                            db.collection("Verification")
                                                    .whereEqualTo("Email",email)
                                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful() && !task.getResult().isEmpty()){

                                                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                                        String documentID = documentSnapshot.getId();
                                                        db.collection("Verification")
                                                                .document(documentID)
                                                                .delete()
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {

                                                                    }

                                                                }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(Sign_Up_VerifyActivity.this, "Deleting failed", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });



                                                    }
                                                }
                                            });
                                                //adding the Patient Record
                                                registerUSer();



                                        }

                                        else{
                                            Toast.makeText(Sign_Up_VerifyActivity.this,"Incorrect Verification Code",Toast.LENGTH_SHORT).show();

                                        }



                                }
                                else {

                                    Toast.makeText(Sign_Up_VerifyActivity.this,"Fail",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
}

    private void registerUSer() {
        GlobalVariables gv =(GlobalVariables) getApplicationContext ();
        String Pass =  gv.getPassword();
        String Fname =gv.getFname();
        String Lname =gv.getLname();
        String Mname =gv.getMname();
        String Contact = gv.getContact();
        String Sex =gv.getSex();
        String Address =gv.getAddress();
        String Postal =gv.getPostal();
        String Municipality =gv.getMunicipality();
        String EEContactP =gv.getEContactP();
        String EContactN =gv.getEContactN();
        String Height =gv.getHeight();
        String Weight =gv.getWeight();
        String BloodP =gv.getBloodP();
        String BloodType =gv.getBloodType();
        String Allergies =gv.getAllergies();
        String Illness =gv.getIllness();

        mAuth.createUserWithEmailAndPassword(email,Pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            String Uid=FirebaseAuth.getInstance().getCurrentUser().getUid();

                            Map<String,Object> Patients = new HashMap<>();
                            Patients.put("FirstName",Fname);
                            Patients.put("LastName",Lname);
                            Patients.put("MiddleInitial",Mname);
                            Patients.put("Sex",Sex);
                            Patients.put("Contact",Contact);
                            Patients.put("Address",Address);
                            Patients.put("Email",email);
                            Patients.put("Municipality",Municipality);
                            Patients.put("Postal",Postal);
                            Patients.put("EContactPerson",EEContactP);
                            Patients.put("EContactNumber",EContactN);
                            Patients.put("Height",Weight);
                            Patients.put("Weight",Height);
                            Patients.put("BloodP",BloodP);
                            Patients.put("BloodType",BloodType);
                            Patients.put("Allergies",Allergies);
                            Patients.put("Illness",Illness);


                            db.collection("Patients").document(Uid)
                                    .set(Patients)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            new AlertDialog.Builder(Sign_Up_VerifyActivity.this)
                                                    .setTitle("Account Successfully Created")
                                                    .setMessage("You have successfully created an account!! You can now login in the login page.")
                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            Intent intent = new Intent(Sign_Up_VerifyActivity.this,Login.class);
                                                            startActivity(intent);
                                                        }
                                                    }).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(gv, "Fail addingdata", Toast.LENGTH_SHORT).show();
                                }
                            });



                        }
                    }
                });

    }
}
