package com.medicall.capstone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.medicall.capstone.R;

public class DataAct extends AppCompatActivity {

    Button accept;
    Button decline;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_privacy);

        accept = (Button) findViewById(R.id.btn_accept1);
        decline = (Button) findViewById(R.id.btn_decline);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DataAct.this, Sign_UpActivity.class);
                startActivity(intent);
            }
        });

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DataAct.this,Login.class);
                startActivity(intent);
            }
        });

    }
}