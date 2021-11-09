package com.medicall.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class addvitals extends AppCompatActivity {

    EditText bpupper;
    EditText bplower;
    EditText temp;
    EditText prate;
    EditText res;
    Button submit;



    FirebaseAuth fAuth;
    FirebaseFirestore db;
    String userId;
    String collectionBpDate, edittxtupper, edittxtlower, temps, pulse, respira;
    RecyclerView mFirestoreList;
    ImageView back;
    TextView none;
    private FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addvitals);

        mFirestoreList = findViewById(R.id.recview_bp);
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        bpupper = findViewById(R.id.bp_upper);
        bplower = findViewById(R.id.bp_lower);
        temp = findViewById(R.id.btemp);
        prate = findViewById(R.id.prate);
        res = findViewById(R.id.respiration);
        submit = findViewById(R.id.vitalbtn);



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            new AlertDialog.Builder(addvitals.this).setTitle("Add Vitals").setMessage("Are you sure about your vitals").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    addvitals();
                    Intent intent = new Intent(getApplicationContext(), pat_blood_pressure.class);
                    startActivity(intent);

                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();


            }
        });



    }

    private void addvitals(){
        String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Date currentTime = Calendar.getInstance().getTime();

        edittxtupper = bpupper.getText().toString();
        edittxtlower = bplower.getText().toString();
        temps = temp.getText().toString();
        pulse = prate.getText().toString();
        respira = res.getText().toString();

        Map<String,Object> vital = new HashMap<>();
        vital.put("Upper", edittxtupper);
        vital.put("Lower", edittxtlower);
        vital.put("Temperature",temps);
        vital.put("Pulse",pulse);
        vital.put("Respiratory", respira);
        vital.put("Dnt", currentTime);

        String ctime;
        ctime = currentTime.toString();
        db.collection("Patients").document(Uid).collection("BP").document(ctime).set(vital);



    }
    private String makeDateString(int day, int month, int year){
        return  day + "-" +  month + "-" + year;
    }



}