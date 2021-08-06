package com.example.capstone;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class pat_blood_pressure extends AppCompatActivity {

    TextView bpdate;
    DatePickerDialog.OnDateSetListener listener;
    EditText bplog;
    Button submitbp;

    FirebaseAuth fAuth;
    FirebaseFirestore db;
    String userId;
    String collectionBpDate, edittxtbp;
    int asd;
    int qwe;
    int hjk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pat_blood_pressure);

        bpdate = findViewById(R.id.bp_date);
        bplog = findViewById(R.id.bp_log);
        submitbp = findViewById(R.id.submit_bp);

        asd = 1;
        qwe = 2;
        hjk = 3;


        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        userId = fAuth.getCurrentUser().getUid();

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        bpdate.setText(getTodaysDate());



        listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = dayOfMonth + "-" + month + "-" + year;
                bpdate.setText(date);


            }
        };

        submitbp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectionBpDate = bpdate.getText().toString();
                edittxtbp = bplog.getText().toString();

                Date currentTime = Calendar.getInstance().getTime();
                Map<String, Object> patbp = new HashMap<>();
                patbp.put("Date", collectionBpDate);
                patbp.put("BP", edittxtbp);
                patbp.put("Dnt", currentTime);

                db.collection("Patients").document(userId).collection("BP").document(collectionBpDate).set(patbp);

            }
        });
    }

    private String makeDateString(int day, int month, int year){
        return  day + "-" +  month + "-" + year;
    }

    private String getTodaysDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month = month + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }
}