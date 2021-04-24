package com.example.capstone;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Sign_Up_VerifyActivity extends AppCompatActivity {
    EditText ET_VCode;
    Button btn_Continue;
    FirebaseFirestore db;
    String VEmail= "";
    String Vcode="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_verfiy);


        btn_Continue= (Button) findViewById(R.id.btn_verify);
        ET_VCode=(EditText) findViewById(R.id.verify);
        db= FirebaseFirestore.getInstance();
        btn_Continue.setOnClickListener(new View.OnClickListener() {
            static final String TAG = "Read Data Activity";
            @Override
            public void onClick(View view) {
                String EVCode= ET_VCode.getText().toString();
                //Getting values
                GlobalVariables gv =(GlobalVariables) getApplicationContext ();
                String email = gv.getEmail();




                db.collection("Verification")
                        .whereEqualTo("Email",email)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){



                                    for(QueryDocumentSnapshot document : task.getResult()) {

                                        Log.d(TAG, document.getId() + "=>" + document.getData());
                                         VEmail = document.get("Email").toString();
                                         Vcode = document.get("VCode").toString();


                                    }
//
                                        if(email.equals(VEmail)&&EVCode.equals(Vcode)){
                                            Intent intent = new Intent(Sign_Up_VerifyActivity.this,Login.class);
                                            startActivity(intent);
                                        }

                                        else{
                                            Toast.makeText(Sign_Up_VerifyActivity.this,"Incorrect Verification Code",Toast.LENGTH_SHORT).show();

                                        }



                                }
                                else {

                                    Toast.makeText(Sign_Up_VerifyActivity.this,"Fail",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
}}
