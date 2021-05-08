package com.example.capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class patient_record_doctor extends AppCompatActivity {

    private RecyclerView patientrecListdoc;
    private FirebaseFirestore firebaseFirestore;

    private FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_record_doctor);

        firebaseFirestore = FirebaseFirestore.getInstance();
        patientrecListdoc = findViewById(R.id.patientrec_doc);


        //Query
        Query query = firebaseFirestore.collection("Patients");
        //RecyclerOptions
        FirestoreRecyclerOptions<PatientModel> options = new FirestoreRecyclerOptions.Builder<PatientModel>()
                .setQuery(query, PatientModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<PatientModel, patient_record_doctor.PatientViewHolder>(options) {
            @NonNull
            @Override
            public patient_record_doctor.PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_patientrec_doc, parent, false);
                return new patient_record_doctor.PatientViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull patient_record_doctor.PatientViewHolder holder, int position, @NonNull PatientModel model) {
                holder.listFirstname.setText(model.getLastName()+ "," +model.getFirstName());
                holder.listemail.setText(model.getEmail());

                String patientID = model.getUserId();

                holder.patientR.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), patientrec_doc.class);
                        intent.putExtra("patid", patientID);
                        startActivity(intent);
                    }
                });
            }
        };

        patientrecListdoc.setHasFixedSize(true);
        patientrecListdoc.setLayoutManager(new LinearLayoutManager(this));
        patientrecListdoc.setAdapter(adapter);

    }

    private class PatientViewHolder extends  RecyclerView.ViewHolder{

        private TextView listFirstname;
        private TextView listemail;
        private Button patientR;

        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);

            listFirstname = itemView.findViewById(R.id.patientrec_firstname);
            listemail = itemView.findViewById(R.id.patientrec_email);
            patientR = itemView.findViewById(R.id.patientrec_btn);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
}