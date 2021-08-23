package com.example.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Patient_HMOList extends AppCompatActivity {

    Button Edit, Delete, Add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_hmo_list);

        Add = (Button) findViewById(R.id.hmo_add);

        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addhmo = new Intent(Patient_HMOList.this, Patient_HMOAdd.class);
                startActivity(addhmo);
            }
        });
    }
}
