package com.example.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentMethod extends AppCompatActivity {

    Button hmo, googlepay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_method);

        hmo = findViewById(R.id.use_hmo);
        googlepay = findViewById(R.id.use_money);

        hmo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentMethod.this, selectDoc_hmo.class);
                startActivity(intent);
            }
        });

        googlepay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentMethod.this, selectDoc.class);
                startActivity(intent);
            }
        });
    }
}
