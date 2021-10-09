package com.medicall.capstone;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.medicall.capstone.R;

import java.util.Calendar;
import java.util.HashMap;

public class Sign_UpActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
Button btnContinue;
EditText ET_FName, ET_LName, ET_MI, ET_Email, ET_Pass, ET_ConPass , ET_Address, ET_Municipality,ET_Postal, ET_Contact, ET_Nationality;
FirebaseFirestore db;
AutoCompleteTextView ET_Sex;
Toolbar toolbar;
boolean check;
FirebaseAuth mAuth;
DatabaseReference reference;
private DatePickerDialog datePickerDialog;
private EditText dateButton;
    @Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.sign_up);
    GlobalVariables gv =(GlobalVariables) getApplicationContext ();
        initDatePicker();
        dateButton = (EditText) findViewById(R.id.btn_bday);
        dateButton.setText(getTodaysDate());
        btnContinue = (Button) findViewById(R.id.signup);
    ET_FName=(EditText) findViewById(R.id.Fname);
    ET_LName=(EditText)findViewById(R.id.Lname);
    ET_MI= (EditText)findViewById(R.id.Mi);
   ET_Sex = (AutoCompleteTextView) findViewById(R.id.gender);
    ET_Contact=(EditText) findViewById(R.id.contact2);
    ET_Nationality=(EditText) findViewById(R.id.nationality);
    ET_Email=(EditText) findViewById(R.id.email);
    ET_Pass= (EditText) findViewById(R.id.password);
    ET_ConPass=(EditText) findViewById(R.id.confirm_pass);
    ET_Address=(EditText) findViewById(R.id.address);
    ET_Postal=(EditText) findViewById(R.id.postalcode);
    ET_Municipality=(EditText) findViewById(R.id.municipality);
    db= FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        ArrayAdapter<CharSequence> adapter =  ArrayAdapter.createFromResource(this,R.array.sex, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ET_Sex.setAdapter(adapter);

        ET_Sex.setOnItemSelectedListener(this);



    btnContinue.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ///bday


            gv.setBday(String.valueOf(dateButton.getText()));

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


            if (ET_FName.getText().toString().trim().isEmpty()){
                Toast.makeText(Sign_UpActivity.this, "Enter First Name",Toast.LENGTH_SHORT).show();
            }else if (ET_LName.getText().toString().trim().isEmpty()){
                Toast.makeText(Sign_UpActivity.this, "Enter Last Name",Toast.LENGTH_SHORT).show();
            }else if (ET_Contact.getText().toString().trim().isEmpty()){
                Toast.makeText(Sign_UpActivity.this, "Enter Contact Number",Toast.LENGTH_SHORT).show();
            }else if(ET_Contact.getText().toString().length()<10){
                Toast.makeText(Sign_UpActivity.this, "Enter a 10 Digit Number",Toast.LENGTH_SHORT).show();
            }
            else if (ET_Nationality.getText().toString().trim().isEmpty()){
                Toast.makeText(Sign_UpActivity.this, "Enter Nationality",Toast.LENGTH_SHORT).show();
            }else if (ET_Email.getText().toString().trim().isEmpty()){
                Toast.makeText(Sign_UpActivity.this, "Enter Email",Toast.LENGTH_SHORT).show();
            }else if (ET_Sex.getText().toString().trim().isEmpty()){
                Toast.makeText(Sign_UpActivity.this, "Enter Gender",Toast.LENGTH_SHORT).show();
            }else if (ET_Pass.getText().toString().trim().isEmpty()){
                Toast.makeText(Sign_UpActivity.this, "Enter Password",Toast.LENGTH_SHORT).show();
            } else if (ET_Pass.getText().toString().length()<6){
                Toast.makeText(Sign_UpActivity.this, "Password must be more than 6 characters",Toast.LENGTH_SHORT).show();
            }else if (!ET_Pass.getText().toString().matches(".*[A-Z].*")) {
                Toast.makeText(Sign_UpActivity.this, "Password must contain an upper case letter",Toast.LENGTH_SHORT).show();
            }else  if (!ET_Pass.getText().toString().matches(".*[0-9].*")) {
                Toast.makeText(Sign_UpActivity.this, "Password must contain a number",Toast.LENGTH_SHORT).show();
            }else if (!ET_Pass.getText().toString().matches("^(?=.*[_.,()?!@#$%^&*]).*$")) {
                Toast.makeText(Sign_UpActivity.this, "Password must contain a symbol",Toast.LENGTH_SHORT).show();
            }else if (ET_Address.getText().toString().trim().isEmpty()){
                Toast.makeText(Sign_UpActivity.this, "Enter Enter Address",Toast.LENGTH_SHORT).show();
            }else if (ET_Municipality.getText().toString().trim().isEmpty()){
                Toast.makeText(Sign_UpActivity.this, "Enter Municipality",Toast.LENGTH_SHORT).show();
            }else if (ET_Postal.getText().toString().trim().isEmpty()){
                Toast.makeText(Sign_UpActivity.this, "Enter Postal Code",Toast.LENGTH_SHORT).show();
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(ET_Email.getText().toString()).matches()){
                Toast.makeText(Sign_UpActivity.this, "Please Enter Valid Email",Toast.LENGTH_SHORT).show();
            }else if (!ET_Pass.getText().toString().equals(ET_ConPass.getText().toString())){
                Toast.makeText(Sign_UpActivity.this, "Password not the same",Toast.LENGTH_SHORT).show();
            }else{


                  mAuth.fetchSignInMethodsForEmail(gv.getEmail())
                        .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                            @Override
                            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                if (task.isSuccessful()){
                                    boolean check =!task.getResult().getSignInMethods().isEmpty();
                                    if (!check){
                                        Intent intent = new Intent(Sign_UpActivity.this, Medical_RecordActivity.class);

                                        startActivity(intent);
                                    }
                                    else {
                                        Toast.makeText(Sign_UpActivity.this, "Email is already in used",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });

            }}


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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}


