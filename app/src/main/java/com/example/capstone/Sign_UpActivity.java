package com.example.capstone;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;

public class Sign_UpActivity extends AppCompatActivity {
Button btnContinue;
EditText ET_FName, ET_LName, ET_MI, ET_Sex, ET_Email, ET_Pass, ET_ConPass , ET_Address, ET_Municipality,ET_Postal, ET_Contact, ET_Nationality;
FirebaseFirestore db;
Toolbar toolbar;
FirebaseAuth mAuth;
DatabaseReference reference;
private DatePickerDialog datePickerDialog;
private Button dateButton;
    @Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.sign_up);
    GlobalVariables gv =(GlobalVariables) getApplicationContext ();
        initDatePicker();
        dateButton = findViewById(R.id.btn_bday);
        dateButton.setText(getTodaysDate());



        btnContinue = (Button) findViewById(R.id.signup);
    ET_FName=(EditText) findViewById(R.id.Fname);
    ET_LName=(EditText)findViewById(R.id.Lname);
    ET_MI= (EditText)findViewById(R.id.Mi);
    ET_Sex= (EditText) findViewById(R.id.gender);
    ET_Contact=(EditText) findViewById(R.id.contact2);
    ET_Nationality=(EditText) findViewById(R.id.nationality);
    ET_Email=(EditText) findViewById(R.id.email);
    ET_Pass= (EditText) findViewById(R.id.password);
    ET_ConPass=(EditText) findViewById(R.id.confirm_pass);
    ET_Address=(EditText) findViewById(R.id.address);
    ET_Postal=(EditText) findViewById(R.id.postalcode);
    ET_Municipality=(EditText) findViewById(R.id.municipality);
    db= FirebaseFirestore.getInstance();



    btnContinue.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {


            gv.setFname(String.valueOf(ET_FName.getText()));
            gv.setLname(String.valueOf(ET_LName.getText()));
            gv.setMname(String.valueOf(ET_MI.getText()));
            gv.setContact(String.valueOf(ET_Contact.getText()));
            gv.setSex(String.valueOf(ET_Sex.getText()));
            gv.setNationality(String.valueOf(ET_Nationality.getText()));
            gv.setEmail(String.valueOf(ET_Email.getText()));
            gv.setPassword(String.valueOf(ET_Pass.getText()));
            gv.setAddress(String.valueOf(ET_Address.getText()));
            gv.setMunicipality(String.valueOf(ET_Municipality.getText()));
            gv.setPostal(String.valueOf(ET_Postal.getText()));

            Intent intent = new Intent(Sign_UpActivity.this,Medical_RecordActivity.class);

            startActivity(intent);







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
                dateButton.setText(date);
            }

        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int style = AlertDialog.THEME_HOLO_LIGHT;

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
    public void openDatePickerView (View view){
        datePickerDialog.show();
    }

    private void registerUser(final String Fname, String Pass, final String email) {

        mAuth.createUserWithEmailAndPassword(email, Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    FirebaseUser user = mAuth.getCurrentUser();

                    reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());


                    if (user!=null) {

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("username", Fname);
                        hashMap.put("email", email);
                        hashMap.put("id", user.getUid());
                        hashMap.put("imageURL", "default");
                        hashMap.put("status", "offline");


                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {


                                if (task.isSuccessful()) {

                                    Toast.makeText(Sign_UpActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(Sign_UpActivity.this,
                                            Sign_UpActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK ));


                                }


                            }
                        });






                    }


                }



            }
        });

    }
}


