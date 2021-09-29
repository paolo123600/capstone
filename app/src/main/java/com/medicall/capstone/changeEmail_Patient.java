package com.medicall.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.medicall.capstone.utilities.Constants;
import com.medicall.capstone.utilities.PreferenceManager;

import java.util.HashMap;

public class changeEmail_Patient extends AppCompatActivity {

    TextView email;
    EditText changeemail_pass, changeemail_newemail;
    Button submit;

    FirebaseAuth fAuth;
    String userEmail;
    FirebaseFirestore db;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email__patient);

        fAuth = FirebaseAuth.getInstance();
        userEmail = fAuth.getCurrentUser().getEmail();

        email = findViewById(R.id.changeemail_email);
        changeemail_pass = findViewById(R.id.changeemail_password);
        changeemail_newemail = findViewById(R.id.changeemail_new_email);
        submit = findViewById(R.id.changeemail_submit);


        db = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = changeemail_pass.getText().toString();
                String new_email = changeemail_newemail.getText().toString();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                AuthCredential credential = EmailAuthProvider.getCredential(userEmail, password);

                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(changeEmail_Patient.this, "User Re-authenticated", Toast.LENGTH_SHORT).show();

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.verifyBeforeUpdateEmail(new_email).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(changeEmail_Patient.this, "Email updated", Toast.LENGTH_SHORT).show();

                                    FirebaseFirestore database = FirebaseFirestore.getInstance();
                                    DocumentReference documentReference =
                                            db.collection(Constants.KEY_COLLECTION_USERS).document(
                                                    preferenceManager.getString(Constants.KEY_USER_ID)
                                            );
                                    HashMap<String, Object> updates = new HashMap<>();
                                    updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
                                    documentReference.update(updates)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    preferenceManager.clearPreferences();
                                                    startActivity(new Intent(getApplicationContext(), Login.class));
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(changeEmail_Patient.this, "Unable to sign out", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        });
                    }
                });
            }
        });
    }
}