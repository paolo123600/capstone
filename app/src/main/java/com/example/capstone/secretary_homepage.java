package com.example.capstone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

public class secretary_homepage extends AppCompatActivity {
    private RelativeLayout notif_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secretary_homepage);

        ///////
        notif_button = findViewById(R.id.notification_button);

        notif_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(secretary_homepage.this, RecentChatSecretary.class);
                startActivity(intent);
            }
        });
    }
}
