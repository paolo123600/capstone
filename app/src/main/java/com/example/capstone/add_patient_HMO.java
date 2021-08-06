package com.example.capstone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class add_patient_HMO extends AppCompatActivity{
    public int numberOfLines = 1;


    FirebaseAuth fAuth;
    FirebaseFirestore db;
    String userId;
    String listallHMO;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient__h_m_o);


        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getAnswer();


            }
        });

        final Button Add_button = (Button) findViewById(R.id.add_button);
        Add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Add_Line();

            }
        });
    }


    public void Add_Line() {
        LinearLayout ll = (LinearLayout)findViewById(R.id.linearLayoutDecisions);
        EditText et = new EditText(this);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        numberOfLines++;
        et.setLayoutParams(p);
        et.setId(numberOfLines + 1);
        ll.addView(et);
        listallHMO = et.getText().toString();

    }


    public void getAnswer() {

        int number = (int)(Math.random() * 3);
        String answer = listallHMO;

        TextView answerBox = (TextView)findViewById(R.id.textView7);
        answerBox.setText(answer);

    }
}

