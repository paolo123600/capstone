package com.medicall.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.medicall.capstone.R;

public class ForgotPassword extends AppCompatActivity {

    Button forgotpass;
    EditText tb_forgotpass;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_page);

        mAuth = FirebaseAuth.getInstance();
        forgotpass = (Button) findViewById(R.id.btn_continueforgotpass);
        tb_forgotpass = (EditText) findViewById(R.id.forgotpassEmail);

        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userEmail = tb_forgotpass.getText().toString();
                if(TextUtils.isEmpty(userEmail)){
                    Toast.makeText(ForgotPassword.this, "Please input your Email", Toast.LENGTH_SHORT).show();
                }else{
                    mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ForgotPassword.this, "Check your Email", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                startActivity(intent);
                            }else {
                                String message = task.getException().getMessage();
                                Toast.makeText(ForgotPassword.this, "An Error Occured "+ message ,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }


            }
        });


    }
}
