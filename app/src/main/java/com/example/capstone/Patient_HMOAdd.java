package com.example.capstone;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.capstone.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Patient_HMOAdd extends AppCompatActivity {

    FirebaseFirestore db;
    LinearLayout lltextbox, llspinner , llbtnadd , llbtnminus ;
    int count =1;
    int tvcount = 101;
    int btnaddcount =201;
    int btnminuscount =301;
    Button btnaccept ;
    PreferenceManager preferenceManager;
    String patuid;


    EditText ET_VCode;
    Button btn_Continue;
    Button emailreg;
    String VEmail= "";
    String Vcode="";
    String email ="";
    FirebaseAuth mAuth;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_hmo_add);
        GlobalVariables gv =(GlobalVariables) getApplicationContext ();
        mAuth = FirebaseAuth.getInstance();



        db = FirebaseFirestore.getInstance();

        preferenceManager = new PreferenceManager(getApplicationContext());
//        patuid =  preferenceManager.getString(Constants.KEY_USER_ID);
        patuid = "5ceSztZP39QQ7sCUJSKwaNmM7NC3";
        llspinner= (LinearLayout) findViewById(R.id.LLspinner);
        lltextbox= (LinearLayout) findViewById(R.id.LLtextbox);
        llbtnadd= (LinearLayout) findViewById(R.id.LLbtnadd);
        llbtnminus= (LinearLayout) findViewById(R.id.LLbtnminus);
        btnaccept = (Button) findViewById(R.id.btnhmocontinue);

        CollectionReference clinicsRef = db.collection("HMO");




        Spinner spinnertag = new Spinner(this);
        spinnertag.setId(count);

        List<String> hmo = new ArrayList<>();
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getApplicationContext(), R.layout.custom_spinner, hmo);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnertag.setAdapter(adapter1);
        clinicsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String subject = document.getString("HMOName");

                        hmo.add(subject);
                    }
                    hmo.add("Others");
                    adapter1.notifyDataSetChanged();
                }
            }
        });
        spinnertag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int number = spinnertag.getId();
                number = number+100;
                if (spinnertag.getSelectedItem().toString().equals("Others")){

                    EditText editText= (EditText)findViewById(number);
                    editText.setVisibility(View.VISIBLE);
                }
                else {
                    EditText editText= (EditText)findViewById(number);
                    editText.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(5, 30, 0, 0);
        spinnertag.setLayoutParams(params);





        llspinner.addView(spinnertag);

        EditText textViewtag = new EditText(this);
        textViewtag.setId(tvcount);
        textViewtag.setVisibility(View.INVISIBLE);
        textViewtag.setHeight(200);
        textViewtag.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
        textViewtag.setBackgroundResource(R.drawable.edittext_bg);
        textViewtag.setHint("Enter HMO");
        LinearLayout.LayoutParams edittxtparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 120);
        edittxtparams.setMargins(0, 10, 0, 0);
        textViewtag.setLayoutParams(edittxtparams);
        textViewtag.setPadding(10, 0, 10, 0);
        lltextbox.addView(textViewtag);



        Button btnaddtag = new Button(this);
        btnaddtag.setId(btnaddcount);
        LinearLayout.LayoutParams btnaddparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 120);
        btnaddparams.setMargins(9, 10, 0, 0);
        btnaddtag.setLayoutParams(btnaddparams);
        btnaddtag.setBackgroundResource(R.drawable.ic_add);



        btnaddtag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add
                btnaddtag.setVisibility(View.INVISIBLE);
                findViewById(btnminuscount).setVisibility(View.INVISIBLE);

            }
        });


        Button btnminustag = new Button(this);
        btnminustag.setId(btnminuscount);
        LinearLayout.LayoutParams btnminusparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 120);
        btnminusparams.setMargins(9, 10, 0, 0);
        btnminustag.setLayoutParams(btnminusparams);
        btnminustag.setBackgroundResource(R.drawable.ic_remove);
        btnminustag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });


        btnaccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = gv.getEmail();
                addhmo();


            }
        });
    }

    private  void addhmo(){
        String Uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String,Object> Patients = new HashMap<>();
        int gcount = count;
        int gtvcount = tvcount;

        String hmo = "";
        String spinnervalue = "";


        EditText editText= (EditText)findViewById(gtvcount);
        Spinner spinner = (Spinner)findViewById(gcount);

        spinnervalue = spinner.getSelectedItem().toString();

        if (spinnervalue.equals("Others")){
            hmo = editText.getText().toString();
        }
        else {
            hmo = spinnervalue;
        }

        Map<String,Object> hmodb = new HashMap<>();
        hmodb.put ("HMOName",hmo);
        db.collection("HMO").document(hmo).set(hmodb);
        ///
        Map<String,Object> uid = new HashMap<>();
        uid.put ("PatientUId",Uid);
        db.collection("HMO").document(hmo).collection("Patients").document(Uid).set(uid);

        Map<String,Object> pathmo = new HashMap<>();
        pathmo.put ("HMOName",hmo);
        db.collection("Patients").document(Uid).collection("HMO").document(hmo).set(pathmo);


        Toast.makeText(Patient_HMOAdd.this, hmo, Toast.LENGTH_SHORT).show();

    }
}
