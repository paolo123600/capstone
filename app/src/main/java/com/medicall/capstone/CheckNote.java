package com.medicall.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class CheckNote extends AppCompatActivity {
    String documentid="";
    FirebaseFirestore db;
    TextView note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_note);

        db = FirebaseFirestore.getInstance();
        documentid = getIntent().getExtras().getString("documentid");
        note= (TextView) findViewById(R.id.notetext);

        db.collection("Schedules").document(documentid).collection("Note").document("Doctor_Note").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc =task.getResult();
                    note.setText(doc.getString("Notes"));
                }
            }

        });



    }
}