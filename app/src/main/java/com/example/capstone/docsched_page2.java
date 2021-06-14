package com.example.capstone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

public class docsched_page2 extends AppCompatActivity {

    private RecyclerView mFirestoreList;
    private FirebaseFirestore db;
    FirestoreRecyclerAdapter adapter;
    TextView docnameTV;
    String docname , docid;
    Button addbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docsched_page2);
        docnameTV= (TextView) findViewById(R.id.docnameTV);
        addbtn  = (Button) findViewById(R.id.docsched_add);

        Intent intent = getIntent();
        docname = intent.getStringExtra("docname");
        docid = intent.getStringExtra("docid");
        docnameTV.setText(docname);

        db = FirebaseFirestore.getInstance();
        mFirestoreList = findViewById(R.id.docsched_list);

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), docsched_page3.class);
                intent.putExtra("docid", docid);
                intent.putExtra("docname", "Doctor " + docname);
                startActivity(intent);
            }
        });

    }
}