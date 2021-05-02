package com.example.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class doctor_homepage extends AppCompatActivity {
    Button btn_chat;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_home);

        btn_chat = findViewById(R.id.btn_chat);

        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(doctor_homepage.this, MainActivity_chat.class);
                startActivity(intent);
            }
        });




    }
}
