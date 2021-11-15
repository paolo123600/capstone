package com.medicall.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.medicall.capstone.doctor.Doctor_schedlist_pastsched;

import org.w3c.dom.Text;

public class CheckNote extends AppCompatActivity {
    String documentid="";
    FirebaseFirestore db;
    TextView note;

    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_note);

        db = FirebaseFirestore.getInstance();
        documentid = getIntent().getExtras().getString("documentid");
        note= (TextView) findViewById(R.id.notetext);
        back = findViewById(R.id.backspace);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Doctor_schedlist_pastsched.class);
                startActivity(intent);
            }
        });

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