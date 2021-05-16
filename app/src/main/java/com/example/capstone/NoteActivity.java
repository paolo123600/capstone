package com.example.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class NoteActivity extends AppCompatActivity {
    EditText Note;
    Button confirm;
    FirebaseFirestore db;
    String schedid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        GlobalVariables gv = (GlobalVariables)getApplicationContext();
        Note=(EditText)findViewById(R.id.MLnote);
        confirm=(Button)findViewById(R.id.btnnoteconfirm);
        db = FirebaseFirestore.getInstance();
        schedid= gv.getSDid();
        db.collection("Schedule").document(schedid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc =task.getResult();
                    Note.setText(doc.getString("Note"));
                    }
                }

        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("Schedule").document(schedid).update("Note",Note.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                       finish();
                    }
                });
            }
        });

    }
}