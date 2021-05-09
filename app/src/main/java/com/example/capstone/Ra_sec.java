package com.example.capstone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Ra_sec extends AppCompatActivity {

    Button confsec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ra_sec);
        confsec = (Button) findViewById(R.id.btn_accept_sec);

        confsec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Ra_sec.this, secretary_homepage.class);
                startActivity(intent);
            }
        });
    }
}