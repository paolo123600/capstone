package com.example.capstone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class RecentChatDoc extends AppCompatActivity {
    Button recentchat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_chat_doc);
        recentchat.findViewById(R.id.btn_recentchatDoc);


        Intent intent = new Intent(getApplicationContext(), doctor_homepage.class);
        startActivity(intent);
    }
}