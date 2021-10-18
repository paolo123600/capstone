package com.medicall.capstone;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.medicall.capstone.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Patient_HMOEdit extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    String userID;
    GlobalVariables gv;
    EditText Cnumber;
    EditText HMOContact;
    Button Cancel, Confirm;
    String hmoname;
    EditText expiration;
    private DatePickerDialog datePickerDialog;

    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_hmo_edit);
        Cancel = (Button) findViewById(R.id.edithmo_cancel);
        Confirm = (Button) findViewById(R.id.edithmo_confirm);
        Cnumber = (EditText) findViewById(R.id.inpCardNumber);
        HMOContact = (EditText) findViewById(R.id.hmocontactnumber);
        initDatePicker();
        expiration = (EditText) findViewById(R.id.expireDate);

        gv = (GlobalVariables) getApplicationContext();
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        Cnumber.setText(gv.getEditHMO_cardNumber());
        expiration.setText(gv.getEditHMO_expirydate());
        HMOContact.setText(gv.getEditHMO_contact());
        hmoname = gv.getEditHMO_hmoName();

        back = findViewById(R.id.backspace);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Patient_HMOList.class);
                startActivity(intent);
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Patient_HMOEdit.this, Patient_HMOList.class);
                startActivity(intent);
            }
        });

        Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Patient_HMOEdit.this);
                builder.setCancelable(true);
                builder.setTitle("Edit Health Insurance");
                builder.setMessage("Do you want to save changes?");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Map<String, Object> updateHMO = new HashMap<>();
                                updateHMO.put("CardNumber", String.valueOf(Cnumber.getText()));
                                updateHMO.put("ExpiryDate", String.valueOf(expiration.getText()));
                                updateHMO.put("HMOCNumber", String.valueOf(HMOContact.getText()));

                                db.collection("Patients").document(userID).collection("HMO").document(hmoname).update(updateHMO)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(Patient_HMOEdit.this, "HMO has been updated", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(Patient_HMOEdit.this, Patient_HMOList.class);
                                                startActivity(intent);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Patient_HMOEdit.this, "Failed to update", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
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
                expiration.setText(date);
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
        return getMonthFormat(month) + " " + day + " " + year;

    }

    private String getMonthFormat(int month) {
        if(month == 1)
            return  "JAN";
        if(month == 2)
            return  "FEB";
        if(month == 3)
            return  "MAR";
        if(month == 4)
            return  "APR";
        if(month == 5)
            return  "MAY";
        if(month == 6)
            return  "JUN";
        if(month == 7)
            return  "JUL";
        if(month == 8)
            return  "AUG";
        if(month == 9)
            return  "SEP";
        if(month == 10)
            return  "OCT";
        if(month == 11)
            return  "NOV";
        if(month == 12)
            return  "DEC";

        return "JAN";
    }
    public void openDate (View view){
        datePickerDialog.show();
    }
}
