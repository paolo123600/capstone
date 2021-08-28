package com.medicall.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.medicall.capstone.R;

public class LoginActivity extends AppCompatActivity {


    TextView et_email, et_password;
    Button loginBtn;
    String email, password;
    FirebaseAuth mAuth;//


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        et_email = findViewById(R.id.Username);
        et_password = findViewById(R.id.Password);
        loginBtn = findViewById(R.id.logbtn);


        mAuth = FirebaseAuth.getInstance();


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = et_email.getText().toString();
                password = et_password.getText().toString();


                if (TextUtils.isEmpty(email)) {

                    et_email.setError("Required");

                } else if (TextUtils.isEmpty(password)) {

                    et_password.setError("Required");
                } else {
                    LoginMeIn(email, password);
                }



            }
        });








    }

    private void LoginMeIn(String email, String password) {


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {



                if (task.isSuccessful()) {


                  Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                  startActivity(intent);
                  finish();
                    Toast.makeText(LoginActivity.this, "Logged In Successfully", Toast.LENGTH_SHORT).show();

                }

            }
        });



    }
}