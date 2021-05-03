package com.example.capstone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class RecentChats extends AppCompatActivity {

    Button newchat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_chats);

        newchat.findViewById(R.id.btn_recentchat);

        Intent intent = new Intent(getApplicationContext(), doctor_homepage.class);
        startActivity(intent);

    }
}