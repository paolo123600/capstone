package com.medicall.capstone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class instructions extends AppCompatActivity {

    TextView book, resched, forgotpass, changepass, addhmo;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        book = findViewById(R.id.ins_howtobook);
        resched = findViewById(R.id.ins_howtoresched);
        forgotpass = findViewById(R.id.ins_forgotpass);
        changepass = findViewById(R.id.ins_changepassword);
        addhmo = findViewById(R.id.ins_addhmo);
        back = findViewById(R.id.backspace);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUrl("https://medicall-6effc.web.app/BookInstruction.html");
            }
        });

        resched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUrl("https://medicall-6effc.web.app/reschedInstruction.html");
            }
        });

        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUrl("https://medicall-6effc.web.app/forgotpass.html");
            }
        });

        changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUrl("https://medicall-6effc.web.app/changepass.html");
            }
        });

        addhmo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUrl("https://medicall-6effc.web.app/hmoinstructions.html");
            }
        });

    }

    private void gotoUrl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }
}