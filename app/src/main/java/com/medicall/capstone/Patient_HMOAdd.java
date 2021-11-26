package com.medicall.capstone;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.textfield.TextInputLayout;
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
import java.util.Calendar;
import java.util.Date;
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
    EditText HMOCnumber;
    Switch switchbtn;

private EditText expiry;
private DatePickerDialog datePickerDialog;

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

    String addexpirydate;
    String addhmocontact;

    TextInputLayout addhmoAddress;
    String addhmoAddress2;
    EditText HMOAddress;

    AutoCompleteTextView autospinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
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
        initDatePicker();
        expiry = (EditText) findViewById(R.id.expireDate);
        HMOCnumber = findViewById(R.id.hmocontactnumber);
        switchbtn = findViewById(R.id.switch_hmoaddress);
        addhmoAddress = findViewById(R.id.addHMOAddress);
        HMOAddress = (EditText) findViewById(R.id.HMOaddress);

        CollectionReference clinicsRef = db.collection("HMO");

        Spinner spinnertag = new Spinner(this);
        spinnertag.setId(count);

        back = findViewById(R.id.backspace);
        
        switchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switchbtn.isChecked()){
                    addhmoAddress.setVisibility(EditText.VISIBLE);
                }
                else{
                    addhmoAddress.setVisibility(EditText.GONE);
                }
            }
        });

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
                addexpirydate = expiry.getText().toString();
                addhmocontact = HMOCnumber.getText().toString();
                addhmoAddress2 = HMOAddress.getText().toString();
                if(switchbtn.isChecked()){
                    if (addhmonum.isEmpty()) {
                        Toast.makeText(Patient_HMOAdd.this, "Please Enter your Card Number", Toast.LENGTH_SHORT).show();
                    }
                    else if (addexpirydate.isEmpty()){
                        Toast.makeText(Patient_HMOAdd.this, "Expiry date cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                    else if(addhmocontact.isEmpty()){
                        Toast.makeText(Patient_HMOAdd.this, "Please enter HMO provider's contact number", Toast.LENGTH_SHORT).show();
                    }
                    else if(addhmoAddress2.isEmpty()){
                        Toast.makeText(Patient_HMOAdd.this, "Please enter your provider's address/branch", Toast.LENGTH_SHORT).show();
                    }
                    else if (hmo.contains(autospinner.getText().toString())) {
                        new AlertDialog.Builder(Patient_HMOAdd.this).setTitle("Add HMO").setMessage("Are you sure you want to add this HMO?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addhmowithAddress();
                                Intent intent = new Intent(getApplicationContext(), Patient_HMOList.class);
                                startActivity(intent);
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
                else{
                    if (addhmonum.isEmpty()) {
                        Toast.makeText(Patient_HMOAdd.this, "Please Enter your Card Number", Toast.LENGTH_SHORT).show();
                    }
                    else if (addexpirydate.isEmpty()){
                        Toast.makeText(Patient_HMOAdd.this, "Expiry date cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                    else if(addhmocontact.isEmpty()){
                        Toast.makeText(Patient_HMOAdd.this, "Please enter HMO provider's contact number", Toast.LENGTH_SHORT).show();
                    }
                    else if (hmo.contains(autospinner.getText().toString())) {
                        new AlertDialog.Builder(Patient_HMOAdd.this).setTitle("Add HMO").setMessage("Are you sure you want to add this HMO?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addhmo();
                                Intent intent = new Intent(getApplicationContext(), Patient_HMOList.class);
                                startActivity(intent);
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


            }
        });
    }

    private String getTodaysDate() {

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month +1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day,month,year);
    }

    //date
    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                expiry.setText(date);
            }

        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int style = android.app.AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style,dateSetListener,year,month,day);

    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " ," + year;

    }

    private String getMonthFormat(int month) {
        if(month == 1)
            return  "January";
        if(month == 2)
            return  "Febuary";
        if(month == 3)
            return  "March";
        if(month == 4)
            return  "April";
        if(month == 5)
            return  "May";
        if(month == 6)
            return  "June";
        if(month == 7)
            return  "July";
        if(month == 8)
            return  "August";
        if(month == 9)
            return  "September";
        if(month == 10)
            return  "October";
        if(month == 11)
            return  "November";
        if(month == 12)
            return  "December";

        return "January";
    }
    public void openDate (View view){
        datePickerDialog.show();
    }


    private  void addhmo(){
        String Uid=FirebaseAuth.getInstance().getCurrentUser().getUid();

       hmospinner = autospinner.getText().toString();
       addhmonum = HMOnumber.getText().toString();
       addexpirydate = expiry.getText().toString();
       addhmocontact = HMOCnumber.getText().toString();
       addhmoAddress2 = "None";

           Map<String,Object> hmodb = new HashMap<>();
           hmodb.put ("HMOName",hmospinner);
           hmodb.put("ExpiryDate",addexpirydate);
           hmodb.put("HMOCNumber",addhmocontact);
            hmodb.put("HMOAddress",addhmoAddress2);
           db.collection("HMO").document(hmospinner).set(hmodb);
           ///
           Map<String,Object> uid = new HashMap<>();
           uid.put ("PatientUId",Uid);
           uid.put("ExpiryDate",addexpirydate);
           uid.put("HMOCNumber",addhmocontact);
           uid.put("HMOAddress",addhmoAddress2);
           db.collection("HMO").document(hmospinner).collection("Patients").document(Uid).set(uid);

           Map<String,Object> pathmo = new HashMap<>();
           pathmo.put ("HMOName",hmospinner);
           pathmo.put("CardNumber", addhmonum);
           pathmo.put("ExpiryDate",addexpirydate);
           pathmo.put("HMOCNumber",addhmocontact);
           pathmo.put("HMOAddress",addhmoAddress2);
           db.collection("Patients").document(Uid).collection("HMO").document(hmospinner).set(pathmo);




    }

    private  void addhmowithAddress(){
        String Uid=FirebaseAuth.getInstance().getCurrentUser().getUid();

        hmospinner = autospinner.getText().toString();
        addhmonum = HMOnumber.getText().toString();
        addexpirydate = expiry.getText().toString();
        addhmocontact = HMOCnumber.getText().toString();
        addhmoAddress2 = HMOAddress.getText().toString();

        Map<String,Object> hmodb = new HashMap<>();
        hmodb.put ("HMOName",hmospinner);
        hmodb.put("ExpiryDate",addexpirydate);
        hmodb.put("HMOCNumber",addhmocontact);
        hmodb.put("HMOAddress",addhmoAddress2);
        db.collection("HMO").document(hmospinner).set(hmodb);
        ///
        Map<String,Object> uid = new HashMap<>();
        uid.put ("PatientUId",Uid);
        uid.put("ExpiryDate",addexpirydate);
        uid.put("HMOCNumber",addhmocontact);
        uid.put("HMOAddress",addhmoAddress2);
        db.collection("HMO").document(hmospinner).collection("Patients").document(Uid).set(uid);

        Map<String,Object> pathmo = new HashMap<>();
        pathmo.put ("HMOName",hmospinner);
        pathmo.put("CardNumber", addhmonum);
        pathmo.put("ExpiryDate",addexpirydate);
        pathmo.put("HMOCNumber",addhmocontact);
        pathmo.put("HMOAddress",addhmoAddress2);
        db.collection("Patients").document(Uid).collection("HMO").document(hmospinner).set(pathmo);




    }
}
