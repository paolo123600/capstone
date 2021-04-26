package com.example.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    Button login, signup;
    // Change EditText to TextInputLayout (TextInputLayout is for Firebase to read and insert data on that specific tool)
    EditText user, pass;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_page);
        db= FirebaseFirestore.getInstance();
        user = findViewById(R.id.Username);
        pass = findViewById(R.id.Password);
        login = findViewById(R.id.logbtn);
        signup = findViewById(R.id.signup);

        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                userlogin();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forgotPW = new Intent (Login.this, Sign_UpActivity.class);
                startActivity(forgotPW);
            }
        });
    }

    private void userlogin() {
        String email = user.getText().toString();
        String password = pass.getText().toString();
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String Uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DocumentReference docIdref = db.collection("Patients").document(Uid);
                    docIdref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()){

                                    Intent forgotPW = new Intent (Login.this, MainActivity.class);
                                    startActivity(forgotPW);
                                }
                                else {

                                    String Uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    DocumentReference docIdref = db.collection("Doctors").document(Uid);
                                    docIdref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()){
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()){
                                                    Toast.makeText(Login.this, "Pasukan ang Doctor", Toast.LENGTH_SHORT).show();
                                                }
                                                else {
                                                    String Uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                                                    DocumentReference docIdref = db.collection("Secretary").document(Uid);
                                                    docIdref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.isSuccessful()){
                                                                DocumentSnapshot document = task.getResult();
                                                                if (document.exists()){

                                                                    Toast.makeText(Login.this, "Pasukan ang secretary", Toast.LENGTH_SHORT).show();
//                                                            Intent forgotPW = new Intent (Login.this, MainActivity.class);
//                                                            startActivity(forgotPW);
                                                                }
                                                                else {
                                                                    Toast.makeText(Login.this, "Invalid login details", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                            else {


                                                            }
                                                        }
                                                    });
                                                }
//                                                Intent forgotPW = new Intent (Login.this, MainActivity.class);
//                                                startActivity(forgotPW);
                                            }
                                            else {


                                            }
                                        }
                                    });

                                }
                            }
                            else {


                            }
                        }
                    });



                }
                else{
                    Toast.makeText(Login.this, "Invalid Login Details", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }


    public void forgotPassword(View view) {
        Intent forgotPW = new Intent (this, ForgotPassword.class);
        startActivity(forgotPW);

    }
}
