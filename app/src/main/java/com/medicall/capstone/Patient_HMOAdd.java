package com.medicall.capstone;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.medicall.capstone.R;

import com.medicall.capstone.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
    ImageView back;
    String hmospinner;
    EditText HMOnumber;


    EditText ET_VCode;
    Button btn_Continue;
    Button emailreg;
    String VEmail= "";
    String Vcode="";
    String email ="";
    FirebaseAuth mAuth;
    DatabaseReference reference;

    String searchablespinner;

    String subjecthmo;

    String addhmonum;

    AutoCompleteTextView autospinner;
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
        autospinner = findViewById(R.id.autocomplete_hmo);
        llspinner= (LinearLayout) findViewById(R.id.LLspinner);
        lltextbox= (LinearLayout) findViewById(R.id.LLtextbox);
        llbtnadd= (LinearLayout) findViewById(R.id.LLbtnadd);
        llbtnminus= (LinearLayout) findViewById(R.id.LLbtnminus);
        btnaccept = (Button) findViewById(R.id.btnhmocontinue);
        HMOnumber = findViewById(R.id.hmocardnumber);

        CollectionReference clinicsRef = db.collection("HMO");






        Spinner spinnertag = new Spinner(this);
        spinnertag.setId(count);

        back = findViewById(R.id.backspace);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Patient_HMOList.class);
                startActivity(intent);
            }
        });

        List<String> hmo = new ArrayList<>();
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getApplicationContext(), R.layout.custom_spinner, hmo);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autospinner.setAdapter(adapter1);
        clinicsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String subject = document.getString("HMOName");

                        hmo.add(subject);


                    }
                }
            }
        });




        btnaccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addhmonum = HMOnumber.getText().toString();
                if (addhmonum.isEmpty()) {
                    Toast.makeText(Patient_HMOAdd.this, "Please Enter a Card Number", Toast.LENGTH_SHORT).show();
                }
                else if (hmo.contains(autospinner.getText().toString())) {
                        new AlertDialog.Builder(Patient_HMOAdd.this).setTitle("Add HMO").setMessage("Are you sure you want to add this HMO?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addhmo();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();

                    } else {
                        Toast.makeText(Patient_HMOAdd.this, "Please select an HMO in the list", Toast.LENGTH_SHORT).show();
                    }



            }
        });
    }


    private  void addhmo(){
        String Uid=FirebaseAuth.getInstance().getCurrentUser().getUid();

       hmospinner = autospinner.getText().toString();
       addhmonum = HMOnumber.getText().toString();




           Map<String,Object> hmodb = new HashMap<>();
           hmodb.put ("HMOName",hmospinner);
           db.collection("HMO").document(hmospinner).set(hmodb);
           ///
           Map<String,Object> uid = new HashMap<>();
           uid.put ("PatientUId",Uid);
           db.collection("HMO").document(hmospinner).collection("Patients").document(Uid).set(uid);

           Map<String,Object> pathmo = new HashMap<>();
           pathmo.put ("HMOName",hmospinner);
           pathmo.put("CardNumber", addhmonum);
           db.collection("Patients").document(Uid).collection("HMO").document(hmospinner).set(pathmo);

           Toast.makeText(Patient_HMOAdd.this, hmospinner, Toast.LENGTH_SHORT).show();


    }
}
