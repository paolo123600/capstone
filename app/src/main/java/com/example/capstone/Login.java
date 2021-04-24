package com.example.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class Login extends AppCompatActivity {

    Button login, signup;
    // Change EditText to TextInputLayout (TextInputLayout is for Firebase to read and insert data on that specific tool)
    EditText user, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        user = findViewById(R.id.Username);
        pass = findViewById(R.id.Password);
        login = findViewById(R.id.logbtn);
        signup = findViewById(R.id.signup);

        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
               /*
               try{

               if(){
                    FOR PATIENT
                }
                else if(){
                    FOR NURSE
                }
                else if(){
                    Intent doctor = new Intent (this, doctor_homepage.class);
                    startActivity(doctor);
                }
                else{
                    ERROR PREVIEW
                }

               }

               catch (Exception ex){

               }
                */
            }
        });
    }

    public void sign_up(View view) {
        Intent signUp = new Intent (this, Sign_UpActivity.class);
        startActivity(signUp);
    }

    public void forgotPassword(View view) {
        Intent forgotPW = new Intent (this, ForgotPassword.class);
        startActivity(forgotPW);
    }
}
