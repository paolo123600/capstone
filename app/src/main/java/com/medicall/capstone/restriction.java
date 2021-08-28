package com.medicall.capstone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.medicall.capstone.R;

public class restriction extends AppCompatActivity {


    EditText password;

    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restriction);

        password = findViewById(R.id.restriction_password);
        submit = findViewById(R.id.restriction_submit);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getText().toString().equals("Sampogs24")) {
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(restriction.this, "Password Incorrect", Toast.LENGTH_SHORT).show();
                }

            }
        });




    }
}