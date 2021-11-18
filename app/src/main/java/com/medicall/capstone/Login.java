package com.medicall.capstone;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.medicall.capstone.R;

import com.medicall.capstone.utilities.Constants;
import com.medicall.capstone.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    TextView terms;
    TextView forgot , signup;
    Button login;
    // Change EditText to TextInputLayout (TextInputLayout is for Firebase to read and insert data on that specific tool)
    EditText user, pass;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    private PreferenceManager preferenceManager;
    private ProgressBar signInProgressBar;
    RelativeLayout progressbg;
    ConstraintLayout bg_remove;
    GlobalVariables gv;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);





        gv = (GlobalVariables) getApplicationContext();

        preferenceManager = new PreferenceManager(getApplicationContext());
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            if (preferenceManager.getString(Constants.USERTYPE).equals("Patient")){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
            else if (preferenceManager.getString(Constants.USERTYPE).equals("Doctor")){
                Intent intent = new Intent(getApplicationContext(), doctor_homepage.class);
                startActivity(intent);
                finish();

            }
            else if (preferenceManager.getString(Constants.USERTYPE).equals("Secretary")){
                Intent intent = new Intent(getApplicationContext(), secretary_homepage.class);
                startActivity(intent);
                finish();

            }

        }

        setContentView(R.layout.login_page);
        db= FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();

        login = findViewById(R.id.logbtn);
        signup = findViewById(R.id.btntvSignup);
        user = findViewById(R.id.Username);
        pass = findViewById(R.id.Password);
        signInProgressBar = findViewById(R.id.signInProgressBar);
        progressbg = findViewById(R.id.progress_bg);
        bg_remove = findViewById(R.id.bgremove);
        terms = findViewById(R.id.terms);
        forgot = findViewById(R.id.forgotpass);

        terms.setMovementMethod(LinkMovementMethod.getInstance());

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgotPW = new Intent (Login.this, ForgotPassword.class);
                startActivity(forgotPW);
            }
        });


        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (user.getText().toString().trim().isEmpty()){
                    Toast.makeText(Login.this, "Enter Email",Toast.LENGTH_SHORT).show();
                }else if (pass.getText().toString().trim().isEmpty()){
                    Toast.makeText(Login.this, "Enter Password",Toast.LENGTH_SHORT).show();
                }else{

                    progressbg.setVisibility(View.VISIBLE);
                    signInProgressBar.setVisibility(View.VISIBLE);
                    bg_remove.setVisibility(View.INVISIBLE);
                    userlogin();
                }





            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forgotPW = new Intent (Login.this, DataAct.class);
                startActivity(forgotPW);
            }
        });
    }

    private void userlogin() {
        String email = user.getText().toString();
        String password = pass.getText().toString();
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    String Uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                    String authemail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    GlobalVariables gv= (GlobalVariables) getApplicationContext();
                    gv.setMainUser(email);
                    gv.setMainuserID(Uid);

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user.isEmailVerified()) {
                        DocumentReference docIdref = db.collection("Patients").document(Uid);
                        docIdref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    DocumentSnapshot document = task.getResult();

                                    if (document.exists()){
                                        if (authemail.equals(document.getString(Constants.KEY_EMAIL))) {
                                            preferenceManager.putString(Constants.KEY_EMAIL, document.getString(Constants.KEY_EMAIL));
                                        } else {
                                            preferenceManager.putString(Constants.KEY_EMAIL, authemail);
                                            db.collection("Patients").document(document.getId()).update("Email", authemail);
                                        }

                                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                                        preferenceManager.putString(Constants.USERTYPE, "Patient");
                                        preferenceManager.putString(Constants.KEY_USER_ID, Uid);
                                        preferenceManager.putString(Constants.KEY_FIRST_NAME, document.getString(Constants.KEY_FIRST_NAME));
                                        preferenceManager.putString(Constants.KEY_LAST_NAME, document.getString(Constants.KEY_LAST_NAME));


                                        Intent intent = new Intent (Login.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }

                                    else {

                                        String Uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        String authemail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                        DocumentReference docIdref = db.collection("Doctors").document(Uid);
                                        docIdref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()){
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()){

                                                        if (authemail.equals(document.getString(Constants.KEY_EMAIL))) {
                                                            preferenceManager.putString(Constants.KEY_EMAIL, document.getString(Constants.KEY_EMAIL));
                                                        } else {
                                                            preferenceManager.putString(Constants.KEY_EMAIL, authemail);
                                                            db.collection("Doctors").document(document.getId()).update("Email", authemail);
                                                        }

                                                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                                                        preferenceManager.putString(Constants.USERTYPE, "Doctor");
                                                        preferenceManager.putString(Constants.KEY_USER_ID, Uid);
                                                        preferenceManager.putString(Constants.KEY_FIRST_NAME, document.getString(Constants.KEY_FIRST_NAME));
                                                        preferenceManager.putString(Constants.KEY_LAST_NAME, document.getString(Constants.KEY_LAST_NAME));
                                                        preferenceManager.putString("ClinicName",document.getString("ClinicName"));

                                                        Intent intent = new Intent (Login.this, Ra_doc.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(intent);
                                                    }
                                                    else {
                                                        String Uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                                                        String authemail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                                        DocumentReference docIdref = db.collection("Secretary").document(Uid);
                                                        docIdref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()){
                                                                    DocumentSnapshot document = task.getResult();
                                                                    if (document.exists()){

                                                                        if (authemail.equals(document.getString(Constants.KEY_EMAIL))) {
                                                                            preferenceManager.putString(Constants.KEY_EMAIL, document.getString(Constants.KEY_EMAIL));
                                                                        } else {
                                                                            preferenceManager.putString(Constants.KEY_EMAIL, authemail);
                                                                            db.collection("Secretary").document(document.getId()).update("Email", authemail);
                                                                        }

                                                                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                                                                        preferenceManager.putString(Constants.USERTYPE, "Secretary");
                                                                        preferenceManager.putString(Constants.KEY_USER_ID, Uid);
                                                                        preferenceManager.putString(Constants.KEY_FIRST_NAME, document.getString(Constants.KEY_FIRST_NAME));
                                                                        preferenceManager.putString(Constants.KEY_LAST_NAME, document.getString(Constants.KEY_LAST_NAME));
                                                                        preferenceManager.putString("ClinicName",document.getString("ClinicName"));

                                                                        Intent intent = new Intent (Login.this, Ra_sec.class);
                                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                        startActivity(intent);
                                                                    }
                                                                    else {
                                                                        progressbg.setVisibility(View.INVISIBLE);
                                                                        signInProgressBar.setVisibility(View.INVISIBLE);
                                                                        bg_remove.setVisibility(View.VISIBLE);
                                                                        Toast.makeText(Login.this, "Invalid login details", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                                else {


                                                                }
                                                            }
                                                        });
                                                    }
//                                                Intent forgotPW = new Intent (Login.this, MainActivity.class);
//                                                startActivity(forgotPW);
                                                }
                                                else {


                                                }
                                            }
                                        });

                                    }
                                }
                                else {


                                }
                            }

                        });

                    }else if (!user.isEmailVerified()){
                        new AlertDialog.Builder(Login.this).setTitle("Email is not Verified").setMessage("Do you want to resend the verification in your Email?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                user.sendEmailVerification();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                        signInProgressBar.setVisibility(View.INVISIBLE);
                        bg_remove.setVisibility(View.VISIBLE);
                    }

                }
                else{
                    progressbg.setVisibility(View.INVISIBLE);
                    signInProgressBar.setVisibility(View.INVISIBLE);
                    bg_remove.setVisibility(View.VISIBLE);
                    Toast.makeText(Login.this, "Invalid Login Details", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }


    public void forgotPassword(View view) {


    }

    public void terms(View view){

    }
}
