package com.example.capstone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class secretary_homepage extends AppCompatActivity {
    private RelativeLayout notif_button;

    private RelativeLayout patrec;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secretary_homepage);

        patrec = findViewById(R.id.patient_record);

        ///////////
        notif_button = findViewById(R.id.notification_button);


        notif_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(secretary_homepage.this, RecentChatSecretary.class);
                startActivity(intent);
            }
        });

        patrec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), patient_record_clinic.class);
                startActivity(intent);
            }
        });
    }
}
