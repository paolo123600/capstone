package com.example.capstone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class RecentChatSecretary extends AppCompatActivity {
    Button recentchatsec;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_chat_secretary);

        recentchatsec.findViewById(R.id.btn_recentchatsec);

    }
}