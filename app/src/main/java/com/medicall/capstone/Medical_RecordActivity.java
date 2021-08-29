package com.medicall.capstone;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.medicall.capstone.R;

import com.medicall.capstone.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/////
public class Medical_RecordActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    EditText ET_ContactP,ET_ContactN,ET_Height,ET_Weight,ET_BloodP,ET_Allergies,ET_Illness;
    Button btn_Continue;
    Spinner ET_BloodType;
    FirebaseFirestore db;

    //from Sign_Up_VerifyActivity

    EditText ET_VCode;
    Button emailreg;
    String VEmail= "";
    String Vcode="";
    String email ="";
    FirebaseAuth mAuth;
    PreferenceManager preferenceManager;
    DatabaseReference reference;

    AutoCompleteTextView autobloodtype;

    private ProgressBar signInProgressBar;
    RelativeLayout progressbg;
    ConstraintLayout bg_remove;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medical_record);
        GlobalVariables gv =(GlobalVariables) getApplicationContext ();

        preferenceManager = new PreferenceManager(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();

        db= FirebaseFirestore.getInstance();
        ET_ContactP=(EditText) findViewById(R.id.EcontanctP);
        ET_ContactN=(EditText) findViewById(R.id.EcontanctN);
        ET_Height=(EditText) findViewById(R.id.Height);
        ET_Weight=(EditText) findViewById(R.id.Weight);
        ET_Allergies=(EditText) findViewById(R.id.Allergies);
        ET_Illness=(EditText) findViewById(R.id.illness);
        btn_Continue = (Button) findViewById(R.id.btn_continue);

        signInProgressBar = findViewById(R.id.signInProgressBar);
        progressbg = findViewById(R.id.progress_bg);
        bg_remove = findViewById(R.id.bgremove);

        autobloodtype = findViewById(R.id.autocomplete_bloodtype);



        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.blood, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autobloodtype.setAdapter(adapter);

        autobloodtype.setOnItemSelectedListener(this);

        btn_Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                if(ET_ContactP.getText().toString().trim().isEmpty()){
                    Toast.makeText(Medical_RecordActivity.this, "Enter Contact Person",Toast.LENGTH_SHORT).show();
                } else if (ET_ContactN.getText().toString().trim().isEmpty()){
                    Toast.makeText(Medical_RecordActivity.this, "Enter Contact Number",Toast.LENGTH_SHORT).show();
                }else if (ET_Height.getText().toString().trim().isEmpty()){
                    Toast.makeText(Medical_RecordActivity.this, "Enter Height",Toast.LENGTH_SHORT).show();
                }else if(ET_Weight.getText().toString().trim().isEmpty()){
                    Toast.makeText(Medical_RecordActivity.this, "Enter Weight",Toast.LENGTH_SHORT).show();
                }else if (autobloodtype.getText().toString().trim().isEmpty()){
                    Toast.makeText(Medical_RecordActivity.this, "Enter Blood Pressure",Toast.LENGTH_SHORT).show();
                }else if (ET_Allergies.getText().toString().trim().isEmpty()){
                    Toast.makeText(Medical_RecordActivity.this, "Enter Allergies",Toast.LENGTH_SHORT).show();
                }else if (ET_Illness.getText().toString().trim().isEmpty()){
                    Toast.makeText(Medical_RecordActivity.this, "Enter Allergies",Toast.LENGTH_SHORT).show();
                }else{

                    new AlertDialog.Builder(Medical_RecordActivity.this).setMessage("Finish registration?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            email = gv.getEmail();
                            registerUser();

                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).show();

                }
            }
        });
    }

    private void registerUser() {
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
        String Bday = gv.getBday();


        mAuth.createUserWithEmailAndPassword(email, Pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();

                            String EE_ContactP = ET_ContactP.getText().toString();
                            String EE_ContactN = ET_ContactN.getText().toString();
                            String EE_Height = ET_Height.getText().toString();
                            String EE_Weight = ET_Weight.getText().toString();
                            String EE_BloodType = autobloodtype.getText().toString();
                            String EE_Allergies = ET_Allergies.getText().toString();
                            String EE_Illness = ET_Illness.getText().toString();


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
                            Patients.put("Birthday",Bday);


                            Patients.put("EContactPerson",EE_ContactP);
                            Patients.put("EContactNumber",EE_ContactN);
                            Patients.put("Height",EE_Height);
                            Patients.put("Weight",EE_Weight);
                            Patients.put("BloodType",EE_BloodType);
                            Patients.put("Allergies",EE_Allergies);
                            Patients.put("Illness",EE_Illness);
                            Patients.put("UserId",Uid);


                            db.collection("Patients").document(Uid)
                                    .set(Patients)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            new android.app.AlertDialog.Builder(Medical_RecordActivity.this)
                                                    .setTitle("Account Successfully Created")
                                                    .setMessage("You have successfully created an account!! Please confirm your email before logging in.")
                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                            user.sendEmailVerification();

                                                            Intent intent = new Intent(Medical_RecordActivity.this,Login.class);
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}