package com.medicall.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.medicall.capstone.R;
import com.medicall.capstone.utilities.Constants;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class NoteActivity extends AppCompatActivity {
    EditText Note;
    Button confirm;
    FirebaseFirestore db;
    String schedid;

    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        GlobalVariables gv = (GlobalVariables)getApplicationContext();
        Note=(EditText)findViewById(R.id.MLnote);
        confirm=(Button)findViewById(R.id.btnnoteconfirm);
        db = FirebaseFirestore.getInstance();
        back = findViewById(R.id.backspace);
        schedid= gv.getSDid();
        db.collection("Schedules").document(schedid).collection("Note").document("Doctor_Note").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc =task.getResult();
                    Note.setText(doc.getString("Notes"));
                    }
                }

        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Notes= Note.getText().toString();
                Date currentTime = Calendar.getInstance().getTime();
                HashMap<String, Object> docunote = new HashMap<>();
                docunote.put("Notes", Notes);
                docunote.put("Dnt", currentTime);

                new AlertDialog.Builder(NoteActivity.this).setTitle("Save Note").setMessage("Are you sure you want to save this note?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.collection("Schedules").document(schedid).collection("Note").document("Doctor_Note").set(docunote).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Toast.makeText(NoteActivity.this, "note sucess", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                startActivity(intent);
                            }
                        });

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                }).show();


            }
        });

    }
}