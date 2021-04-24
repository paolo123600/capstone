package com.example.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Sign_Up_VerifyActivity extends AppCompatActivity {
    TextView et_test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_verfiy);

        Intent intent = getIntent();
        String fname=intent.getStringExtra("FirstName");
        String lname=intent.getStringExtra("FirstName");
        String mname=intent.getStringExtra("FirstName");
        String sex =intent.getStringExtra("FirstName");
        String contact=intent.getStringExtra("FirstName");
        String nationality=intent.getStringExtra("FirstName");
        String email=intent.getStringExtra("FirstName");
        String pass=intent.getStringExtra("FirstName");
        String address=intent.getStringExtra("FirstName");
        String postal=intent.getStringExtra("FirstName");
        String municipality=intent.getStringExtra("FirstName");



}}
